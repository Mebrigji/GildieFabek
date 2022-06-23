package pl.saidora.core.commands;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.events.UserAcceptTeleportRequestEvent;
import pl.saidora.core.handlers.TeleportHandler;
import pl.saidora.core.model.impl.User;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CommandInfo(name = "tpaccept", usage = "/tpaccept [list/(player/all)]", permission = "", executors = ExecutorType.PLAYER)
public class TpacceptCommand implements Command {

    private static final Pattern pattern = Pattern.compile("%item|[*]%");

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0) {
            User user = Main.getInstance().getOnlineUsers().get(executor.getName());
            if(user.getTeleportRequests() == 0){
                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT_LIST_EMPTY);
                return;
            }
            TeleportHandler handler = user.getAllTeleportRequests().get(0);
            handler.getUser().getTeleport().ifPresent(teleport -> {
                teleport.teleport(user.asPlayer().get().getLocation(), u -> u.hasPermission("sacore.bypass.delay") ? 0L : 10L);
                user.getAllTeleportRequests().remove(handler);
            });
            //executor.sendMessage(usage());
        } else {
            User user = Main.getInstance().getOnlineUsers().get(executor.getName());
            if(args[0].equalsIgnoreCase("list")){
                if(user.getTeleportRequests() == 0){
                    executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT_LIST_EMPTY);
                    return;
                }
                String message = Main.getInstance().getConfiguration().COMMAND_TPACCEPT_LIST;
                MessageBuilder builder = executor.prepareMessage(message);
                Matcher matcher = pattern.matcher(message);
                while (matcher.find()){
                    String text = matcher.group().replace("%", "").replace("list|", "");
                    builder.withPreparingComponent(matcher.group(), textComponent -> {
                        textComponent.setText(ColorHelper.translateColors(text));
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColorHelper.translateColors(user.getAllTeleportRequests().stream().map(teleportHandler -> teleportHandler.getUser().getName()).collect(Collectors.joining(", "))))));
                    });
                }
                builder.send();
            } else if(args[0].equalsIgnoreCase("all")) {
                UserAcceptTeleportRequestEvent acceptTeleportRequestEvent = new UserAcceptTeleportRequestEvent() {
                    @Override
                    public List<User> getAcceptedUsers() {
                        return user.getAllTeleportRequests().stream().map(TeleportHandler::getUser).collect(Collectors.toList());
                    }

                    @Override
                    public Location getTeleportLocation() {
                        return user.asPlayer().get().getLocation();
                    }

                    @Override
                    public long getDelayTime() {
                        return 10L;
                    }

                    @Override
                    public User getUser() {
                        return user;
                    }
                };
                acceptTeleportRequestEvent.call();

                if (acceptTeleportRequestEvent.getAcceptedUsers().size() == 0) {
                    executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT_LIST_EMPTY);
                    return;
                }

                if(acceptTeleportRequestEvent.getAcceptedUsers().size() == 1){
                    TeleportHandler handler = user.getAllTeleportRequests().get(0);
                    handler.getUser().getTeleport().ifPresent(teleport -> teleport.teleport(user.asPlayer().get().getLocation(), u -> 10L));
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT).with("playerName", handler.getUser().getName()).send();
                    return;
                }

                for (User allTeleportRequest : acceptTeleportRequestEvent.getAcceptedUsers()) {
                    allTeleportRequest.getTeleport().ifPresent(teleport -> teleport.teleport(user.asPlayer().get().getLocation(), u -> 10L));
                }

                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT_ALL).with("amount", user.getAllTeleportRequests().size()).send();
                user.getAllTeleportRequests().clear();
            } else {
                Main.getInstance().getUserCache().findByName(args[0], true).ifPresentOrElse(target -> {
                    user.fetchTeleportRequest(target.getName()).ifPresentOrElse(teleportHandler -> {
                        UserAcceptTeleportRequestEvent acceptTeleportRequestEvent = new UserAcceptTeleportRequestEvent() {
                            @Override
                            public List<User> getAcceptedUsers() {
                                return Collections.singletonList(target);
                            }

                            @Override
                            public Location getTeleportLocation() {
                                return user.asPlayer().get().getLocation();
                            }

                            @Override
                            public long getDelayTime() {
                                return 10L;
                            }

                            @Override
                            public User getUser() {
                                return user;
                            }
                        };
                        acceptTeleportRequestEvent.call();

                        target.getTeleport().get().teleport(user.asPlayer().get().getLocation(), u -> 10L);
                        user.getAllTeleportRequests().remove(teleportHandler);
                        executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT).with("playerName", target.getName()).send();
                    }, () -> {
                        executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_TPACCEPT_NOT_SENT).with("playerName", target.getName()).send();
                    });
                }, () -> executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_TELEPORT_OFFLINE));
            }
        }
    }
}
