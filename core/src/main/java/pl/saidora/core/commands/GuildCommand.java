package pl.saidora.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.TimeHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.ExecutorType;

import java.util.HashMap;
import java.util.Map;

@CommandInfo(name = "guild", usage = "", permission = "", aliases = {"g", "gildia"})
public class GuildCommand implements Command {

    public GuildCommand() {
    }

    private static Map<String, Command> subCommands = new HashMap<>();

    public static void registerSubCommand(Command... commands){
        for (Command command : commands) {
            subCommands.put(command.commandInfo().name(), command);
            for (String alias : command.commandInfo().aliases()) {
                subCommands.put(alias.toLowerCase(), command);
            }
        }
    }

    private Main getInstance(){
        return Main.getInstance();
    }

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();

        if(args.length == 0) executor.sendMessage(Main.getInstance().getConfiguration().GUILD_HELP);
        else {
            Command command = subCommands.get(args[0]);
            if (command == null) {
                executor.sendMessage(Main.getInstance().getConfiguration().GUILD_HELP);
                return;
            }
            if (command.commandInfo().executors().equals(ExecutorType.CONSOLE) && executor.isPlayer()) {
                executor.sendMessage("&cThat command is only for console.");
            } else if (command.commandInfo().executors().equals(ExecutorType.PLAYER) && executor.isConsole()) {
                executor.sendMessage("&cThat command is only for player.");
            } else command.run(executor);
        }
    }
}
