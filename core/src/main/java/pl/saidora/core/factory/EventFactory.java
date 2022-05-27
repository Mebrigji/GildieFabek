package pl.saidora.core.factory;

import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.saidora.core.helpers.MessageHolder;
import pl.saidora.core.Main;
import pl.saidora.core.cache.EventCache;
import pl.saidora.api.helpers.TimeHelper;
import pl.saidora.core.helpers.PacketHelper;
import pl.saidora.core.model.impl.Generator;
import pl.saidora.core.model.impl.*;
import pl.saidora.core.model.impl.guild.Guild;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class EventFactory {

    public void registerDefaultEvents(){
        EventCache.USER_JOIN_EVENT.add(event -> {
            User user = event.getUser();
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                if (user.getRequiredExp() == 0) user.setRequiredExp(50);

                user.setOnline(true);
                user.setUUID(event.getBukkitJoinEvent().getPlayer().getUniqueId());
                user.setAbsent(AntiLogout::new, Actionbar::new, ScoreBoard::new, TabList::new, Teleport::new, PacketHelper::new);

                //user.getActionbar().ifPresent(actionbar -> actionbar.addMessage("dev-info", new Actionbar.TimedMessage(-1, u -> "&7Cpu: &d" + SystemHelper.getCpuUsage() + "% &8| &7Ram: &d(usage=" + SystemHelper.getUsageRam() + "/total=" + SystemHelper.getTotalRam() + "/free=" + SystemHelper.getFreeRam() + "/max=" + SystemHelper.getMaxRam() + ")")));

                Main.getInstance().tabFactory.setTab(user);
                Main.getInstance().scoreboardFactory.setScoreboard(user);

                user.giveItem(Main.getInstance().getConfiguration().GENERATOR_ITEM);

                Main.getInstance().getLeaderboardCache().get(User.class).ifPresent(userLeaderboard -> userLeaderboard.addIfAbsent(user));
                try {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + user.getUUID().toString() + "?unsigned=false");
                    InputStreamReader streamReader = new InputStreamReader(url.openStream());
                    user.setProperty(new JsonParser().parse(streamReader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject());
                    streamReader.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }, 10);
        });
        EventCache.USER_QUIT_EVENT.add(event -> {
            event.getUser().setOnline(false);
            event.getUser().getGuild().ifPresent(guild -> guild.setOnline(event.getUser(), false));
        });
        EventCache.USER_CHAT_EVENT.add(event -> {
            User user = event.getUser();
            if(user.getChatDelay() > System.currentTimeMillis()){
                user.prepareMessage("\n&5[CHAT] &dNie możesz jeszcze pisać na chacie przez: &5%time% sek.\n ")
                        .with("time", (user.getChatDelay() - System.currentTimeMillis()) / 1000)
                        .send();
                event.setCancelled(true);
                return;
            }

            if(!user.hasPermission("sacore.bypass.chat")) {
                user.setChatDelay(System.currentTimeMillis() + 5000);
                user.getScoreboard().ifPresent(scoreBoard -> scoreBoard.addTimedMessage("delay-chat", new TimedMessage(5000, u -> "&7Blokada chatu: &d" + (user.getChatDelay() - System.currentTimeMillis()) + "ms. &7do konca")));
            }

            //ChatProvider provider = Main.getInstance().getChatCache().get(user.getGroupName()).clone();

            Guild guild = user.getGuild().orElse(null);

            event.setMessage(() -> Arrays.asList(MessageHolder.create(guild == null ? "" : "&8[&5" + guild.getTag() + "&8] ", guild == null ? new ArrayList<>() : Arrays.asList("&7Nazwa: &d" + guild.getName(), "&7Punkty: &d" + guild.getPoints(), "", "&7Godzina: &d" + new TimeHelper())),
                    MessageHolder.create(user.getName(), Arrays.asList("&7Punkty: &d" + user.getPoints(), "&7Zabojstwa: &d" + user.getKills(), "&7Smierci: &d" + user.getDeaths(), " ", "&7Poziom: &d" + user.getLevel())),
                    MessageHolder.create("&8: &7" + event.getBukkitChatEvent().getMessage())));

        });
        EventCache.USER_BLOCK_BREAK_EVENT.add(event -> {
            User user = event.getUser();

            if(user.addExp() >= user.getRequiredExp()){
                user.setLevel(user.getLevel() + 1);
                user.setExp(0);
                user.setRequiredExp((long) (user.getRequiredExp() + (user.getRequiredExp() * 0.3) + 100));
                user.sendTitle("&8:: &d&lAwans! &8::", "&7Osiągnięto wymagany poziom doświadczenia", 10, 23, 5);
            }

            Generator generator = Main.getInstance().getGeneratorCache().getGeneratorMap().get(event.getBlock().getLocation());
            if(generator == null) return;
            Player player = event.getUser().asPlayer().orElse(null);
            if(player != null && player.getItemInHand().getType().equals(Material.GOLD_PICKAXE))
                Main.getInstance().getGeneratorCache().breakGenerator(generator, event);
            else generator.regen();
        });
    }

    public void registerTeleportEvents(){

        EventCache.USER_SEND_TELEPORT_REQUEST.add(event -> {
            User target = event.getTarget();
            User user = event.getUser();

            Optional<Long> optional = user.getTeleportDateExpire(user);

            if(optional.isPresent()){
                user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_REQUEST_ALREADY
                        .replace("%playerName%", target.getName())
                        .replace("%time%", new TimeHelper(optional.get()).secondsToString()));
                user.send();
                return;
            }

            if(user.getTeleportRequests() >= 8){
                user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_REQUEST_MAX_COUNT.replace("%count%", String.valueOf(user.getTeleportRequests())));
                user.send();
                return;
            }

            String time = new TimeHelper(event.getExpireDate()).secondsToString();

            user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_REQUEST_EXECUTOR.replace("%playerName%", target.getName()).replace("%time%", time));
            user.send();

            target.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_REQUEST_TARGET.replace("%playerName%", user.getName()).replace("%time%", time));
            target.send();
        });

        EventCache.USER_ACCEPT_TELEPORT_REQUEST.add(event -> {
            User user = event.getUser();
            user.asPlayer().ifPresent(player -> {
                Location location = player.getLocation();
                if(event.getAcceptedUsers().size() == 1){
                    User target = event.getAcceptedUsers().get(0);

                    target.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_ACCEPT_TARGET_SINGLE.replace("%playerName%", user.getName()));
                    target.send();

                    target.getTeleport().ifPresent(teleport -> teleport.teleport(location, provider -> target.hasPermission("sacore.bypass.teleport.cooldown") ? 0L : Main.getInstance().getConfiguration().TELEPORT_SCHEDULER_DELAY));

                    user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_ACCEPT_EXECUTOR_SINGLE.replace("%playerName%", target.getName()));
                } else {
                    event.getAcceptedUsers().forEach(target -> {
                        target.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_ACCEPT_TARGET_MANY.replace("%playerName%", user.getName()).replace("%count%", String.valueOf(event.getAcceptedUsers().size()-1)));
                        target.send();

                        target.getTeleport().ifPresent(teleport -> teleport.teleport(location, provider -> target.hasPermission("sacore.bypass.teleport.cooldown") ? 0L : Main.getInstance().getConfiguration().TELEPORT_SCHEDULER_DELAY));
                    });

                    user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_ACCEPT_EXECUTOR_MANY.replace("%count%", String.valueOf(event.getAcceptedUsers().size())));
                }
                user.send();
            });
        });

        EventCache.USER_DAMAGE_GIVEN_EVENT.add(event -> {
            User user = event.getUser();
            user.getTeleport().ifPresent(teleport -> {
                if(teleport.isRunning()) {
                    user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_SCHEDULER_OBSTACLE);
                    user.send();
                    teleport.getBukkitTask().cancel();
                }
            });
        });

    }
}
