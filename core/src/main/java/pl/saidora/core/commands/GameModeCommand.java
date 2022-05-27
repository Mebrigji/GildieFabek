package pl.saidora.core.commands;

import org.bukkit.GameMode;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;

import java.util.Arrays;

@CommandInfo(name = "gamemode", usage = "/gamemode [mode] [player(optional)]", permission = "sacore.gamemode", aliases = "gm")
public class GameModeCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0){
            executor.sendMessage(commandInfo().usage());
            return;
        }

        GameMode gameMode = getMode(args[0]);

    }

    private GameMode getMode(String argument){
        return Arrays.stream(GameMode.values())
                .filter(g -> g.name().equalsIgnoreCase(argument))
                .findFirst()
                .orElseGet(() -> Arrays.stream(GameMode.values())
                        .filter(g -> String.valueOf(g.getValue())
                                .equals(argument))
                        .findFirst()
                        .orElse(null));
    }
}
