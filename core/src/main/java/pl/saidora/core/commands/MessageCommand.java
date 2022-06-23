package pl.saidora.core.commands;

import org.apache.commons.lang.StringUtils;
import pl.saidora.api.functions.CompleteFuture;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.model.impl.User;

@CommandInfo(name = "message", usage = "/message [player] [message]", permission = "", aliases = {"msg", "tell"}, executors = ExecutorType.PLAYER)
public class MessageCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length <= 1){
            executor.sendMessage(usage());
        } else {
            User exec = Main.getInstance().getOnlineUsers().get(executor.getName());
            Main.getInstance().getUserCache().findByName(args[0], true).ifPresentOrElse(user -> {

                if(user.equals(exec)){
                    user.sendMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_EQUALS);
                    return;
                }

                if (user.isBlocked(exec)) {
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_BLOCK).with("playerName", user.getName()).send();
                    return;
                }

                ((CompleteFuture<User>) () -> user)
                        .check(user.isToggle(User.ToggleType.MESSAGE), () -> executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_TOGGLE)
                                .with("playerName", user.getName()).send())
                        .then(() -> {
                            String message = StringUtils.join(args, " ", 1, args.length).trim();
                            if (message.isEmpty()) {
                                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_EMPTY);
                                return;
                            }
                            exec.setLastReceiver(user);

                            exec.prepareMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_PREFIX_ONE)
                                    .with("playerName", user.getName())
                                    .with("message", message)
                                    .send();

                            user.prepareMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_PREFIX_SECOND)
                                    .with("playerName", exec.getName())
                                    .with("message", message)
                                    .send();
                        });

            }, () -> executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_MESSAGE_OFFLINE));
        }
    }
}
