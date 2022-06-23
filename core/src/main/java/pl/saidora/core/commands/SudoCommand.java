package pl.saidora.core.commands;

import org.apache.commons.lang.StringUtils;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;

@CommandInfo(name = "sudo", usage = "/sudo [playerName] [command/c:(message)]", permission = "sacore.sudo")
public class SudoCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0) executor.sendMessage(commandInfo().usage());
        else if(args.length >= 2){
            Main.getInstance().getUserCache().findByName(args[0], true).ifPresentOrElse(user -> {
                String command = StringUtils.join(args, " ", 1, args.length);
                if(command.startsWith("c:")){
                    command = command.replaceFirst("c: ", "").replaceFirst("c:", "");
                    user.chat(command);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_SUDO_EXECUTE_CHAT).with("playerName", user.getName()).with("message", command).send();
                } else {
                    user.execute(command);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_SUDO_EXECUTE_COMMAND).with("playerName", user.getName()).with("command", command).send();
                }
            }, () -> executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_SUDO_OFFLINE));
        } else executor.sendMessage(commandInfo().usage());
    }
}
