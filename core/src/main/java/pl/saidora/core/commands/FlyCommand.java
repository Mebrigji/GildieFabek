package pl.saidora.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;

@CommandInfo(name = "fly", usage = "/fly [player(optional)] [on, off, check]", permission = "sacore.fly", aliases = {"flight", "latanie"})
public class FlyCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(executor.isConsole()){
            if(args.length == 0){
                executor.sendMessage("/fly [player]");
            } else {
                Player player = Bukkit.getPlayerExact(args[0]);
                if(player == null){
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_OFFLINE).with("playerName", args[0]).send();
                    return;
                }
                MessageBuilder builder = new MessageBuilder(player, "");
                if(args.length == 2){
                    if(args[1].equalsIgnoreCase("on")){
                        player.setAllowFlight(true);
                        executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_ON).with("playerName", player.getName()).send();
                        builder.setMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_ON).send();
                    } else if(args[1].equalsIgnoreCase("off")){
                        player.setAllowFlight(false);
                        executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_OFF).with("playerName", player.getName()).send();
                        builder.setMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OFF).send();
                    } else if(args[1].equalsIgnoreCase("check")){
                        if(player.getAllowFlight()) executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHECK_ON).with("playerName", player.getName()).send();
                        else executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHECK_OFF).with("playerName", player.getName()).send();
                    } else executor.sendMessage(usage());
                    return;
                }

                player.setAllowFlight(!player.getAllowFlight());
                executor.prepareMessage(player.getAllowFlight() ? Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_ON : Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_OFF).with("playerName", player.getName()).send();
                builder.setMessage(player.getAllowFlight() ? Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_ON : Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OFF).send();
            }
        } else if(args.length == 0){
            executor.asPlayer().ifPresent(player -> {
                player.setAllowFlight(!player.getAllowFlight());
                executor.sendMessage(player.getAllowFlight() ? Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_ON : Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OFF);
            });
        } else {
            Player player = Bukkit.getPlayerExact(args[0]);
            if(player == null){
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_OFFLINE).with("playerName", args[0]).send();
                return;
            }
            MessageBuilder builder = new MessageBuilder(player, "");
            if(args.length == 2){
                if(args[1].equalsIgnoreCase("on")){
                    player.setAllowFlight(true);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_ON).with("playerName", player.getName()).send();
                    builder.setMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_ON).send();
                } else if(args[1].equalsIgnoreCase("off")){
                    player.setAllowFlight(false);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_OFF).with("playerName", player.getName()).send();
                    builder.setMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OFF).send();
                } else if(args[1].equalsIgnoreCase("check")){
                    if(player.getAllowFlight()) executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHECK_ON).with("playerName", player.getName()).send();
                    else executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_FLY_CHECK_OFF).with("playerName", player.getName()).send();
                } else executor.sendMessage(usage());
                return;
            }

            player.setAllowFlight(!player.getAllowFlight());
            executor.prepareMessage(player.getAllowFlight() ? Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_ON : Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OTHER_OFF).with("playerName", player.getName()).send();
            builder.setMessage(player.getAllowFlight() ? Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_ON : Main.getInstance().getConfiguration().COMMAND_FLY_CHANGE_OFF).send();
        }
    }
}
