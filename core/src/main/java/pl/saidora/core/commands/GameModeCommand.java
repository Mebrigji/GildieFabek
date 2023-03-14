package pl.saidora.core.commands;

import org.bukkit.GameMode;
import pl.saidora.api.helpers.StringHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.model.impl.User;

import java.util.Arrays;
import java.util.List;

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
        if(gameMode == null){
            executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_GAMEMODE_INVALID_MODE).with("mode", args[0]).send();
            return;
        }

        User user = null;
        if(args.length == 2) user = Main.getInstance().getOnlineUsers().get(args[1]);
        else if(executor.isPlayer()) user = Main.getInstance().getOnlineUsers().get(executor.getName());

        if(user == null){
            executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_GAMEMODE_INVALID_PLAYER).with("%playerName%", executor.getName()).with("%mode%", gameMode).send();
            return;
        }

        final User finalUser = user;
        user.asPlayer().ifPresent(player -> {
            player.setGameMode(gameMode);
            if(player.getName().equals(executor.getName())) executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_GAMEMODE_CHANGE_SELF).with("mode", gameMode).send();
            else {
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_GAMEMODE_CHANGE_EXECUTOR).with("playerName", player.getName()).with("mode", gameMode).send();
                finalUser.prepareMessage(Main.getInstance().getConfiguration().COMMAND_GAMEMODE_CHANGE_TARGET).with("executorName", executor.getName()).with("mode", gameMode).send();
            }
        });
    }

    @Override
    public List<String> tabComplete(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 1) return StringHelper.startWith(args, args[0]);
        else if(args.length == 2) return StringHelper.startWith(Main.getInstance().getOnlineUsers().keySet().toArray(new String[]{}), args[1]);
        return Command.super.tabComplete(executor);
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
