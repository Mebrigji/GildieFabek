package pl.saidora.core.commands;

import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;

@CommandInfo(name = "vanish", usage = "/vanish [on, off, panel] [player(optional)]", permission = "sacore.vanish", aliases = "v")
public class VanishCommand implements Command {

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0){

        }
    }
}
