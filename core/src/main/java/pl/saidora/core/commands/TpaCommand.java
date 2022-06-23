package pl.saidora.core.commands;

import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;

@CommandInfo(name = "tpa", usage = "/tpa [playerName]", permission = "", executors = ExecutorType.PLAYER)
public class TpaCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if (args.length == 0) {
            executor.sendMessage(commandInfo().usage());
            return;
        }
        if(args[0].equals(executor.getName())){
            executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_TELEPORT_EQUALS);
            return;
        }
        Main.getInstance().getUserCache().findByName(executor.getName(), true)
                .ifPresent(user -> Main.getInstance().getUserCache().findByName(args[0], true)
                .ifPresentOrElse(user::sendTeleportRequest, () -> executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_TELEPORT_OFFLINE)));
    }
}
