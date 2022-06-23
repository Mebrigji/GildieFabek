package pl.saidora.core.commands;

import org.apache.commons.lang3.StringUtils;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.model.impl.User;

@CommandInfo(name = "reply", usage = "/reply [message]", permission = "", aliases = {"r", "odpisz"}, executors = ExecutorType.PLAYER)
public class ReplyCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0){
            executor.sendMessage(usage());
        } else {
            User user = Main.getInstance().getOnlineUsers().get(executor.getName());

            if(user.getLastReceiver() == null) {
                user.sendMessage(Main.getInstance().getConfiguration().COMMAND_REPLY_EMPTY);
                return;
            }

            if(!user.getLastReceiver().isOnline()){
                user.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPLY_OFFLINE).with("playerName", user.getLastReceiver().getName()).send();
                return;
            }

            User lastMessager = user.getLastReceiver();
            if(lastMessager.isBlocked(user)){
                user.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPLY_BLOCK).with("playerName", lastMessager.getName()).send();
                return;
            }

            if(lastMessager.isToggle(User.ToggleType.MESSAGE)){
                user.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPLY_TOGGLE).with("playerName", lastMessager.getName()).send();
                return;
            }

            String message = StringUtils.join(args, " ", 0, args.length).trim();

            lastMessager.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPLY_PREFIX_SECOND)
                    .with("playerName", user.getName())
                    .with("message", message)
                    .send();

            user.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPLY_PREFIX_ONE)
                    .with("playerName", user.getName())
                    .with("message", message)
                    .send();
        }
    }
}
