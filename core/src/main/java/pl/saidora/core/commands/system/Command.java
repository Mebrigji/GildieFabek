package pl.saidora.core.commands.system;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Command {

    static org.bukkit.command.Command toBukkitCommand(Command command){
        CommandInfo info = command.getClass().getDeclaredAnnotation(CommandInfo.class);

        org.bukkit.command.Command bukkitCommand = new org.bukkit.command.Command(info.name()) {

            @Override
            public boolean execute(CommandSender commandSender, String string, String[] args) {
                Executor executor = new Executor(commandSender, args, command);
                executor.hasAccess(() -> executor.hasPermissions(() -> command.run(executor)));
                return false;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                Executor executor = new Executor(sender, args, command);
                List<String> complete = new ArrayList<>();
                executor.hasAccess(() -> executor.hasPermissions(() -> complete.addAll(command.tabComplete(executor))));
                return complete;
            }
        };
        bukkitCommand.setAliases(Arrays.asList(info.aliases()));
        bukkitCommand.setPermission(command.permission());
        bukkitCommand.setDescription(info.description());
        return bukkitCommand;
    }

    default CommandInfo commandInfo(){
        return getClass().getDeclaredAnnotation(CommandInfo.class);
    }

    default String usage(){
        return commandInfo().usage();
    }

    default String permission(){
        return commandInfo().permission();
    }

    default String permissionMessage(){
        return CommandMessage.permission_message.replace("%permission%", permission());
    }

    void run(Executor executor);

    default List<String> tabComplete(Executor executor){
        return ImmutableList.of();
    }
}
