package pl.saidora.core.commands.system;

import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.factory.NewerOptional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Executor {

    private final CommandSender commandSender;
    private final String[] commandArguments;
    private final Command command;

    public Executor(CommandSender commandSender, String[] commandArguments, Command command){
        this.commandSender = commandSender;
        this.commandArguments = commandArguments;
        this.command = command;
    }

    public String getName(){
        return commandSender.getName();
    }

    public boolean isPlayer(){
        return commandSender instanceof Player;
    }

    public boolean isConsole(){
        return commandSender instanceof ConsoleCommandSender;
    }

    public NewerOptional<Player> asPlayer(){
        return NewerOptional.ofNullable(Bukkit.getPlayerExact(commandSender.getName()));
    }

    public NewerOptional<ConsoleCommandSender> asConsoleSender(){
        return NewerOptional.ofNullable(commandSender instanceof Player ? null : (ConsoleCommandSender) commandSender);
    }

    public MessageBuilder prepareMessage(List<String> messages){
        return prepareMessage(String.join("\n", messages));
    }

    public String[] getCommandArguments() {
        return commandArguments;
    }

    public MessageBuilder prepareMessage(String... message){
        return prepareMessage(String.join("\n", message));
    }

    public MessageBuilder prepareMessage(String message){
        return new MessageBuilder(this, message);
    }

    public void sendMessage(String message){
        commandSender.sendMessage(ColorHelper.translateColors(message));
    }

    public boolean hasPermission(String permission){
        return commandSender.hasPermission(permission);
    }

    public void hasAccess(Runnable runnable){
        if(command.commandInfo().executors().equals(ExecutorType.CONSOLE) && isPlayer()){
            prepareMessage(CommandMessage.only_console);
            return;
        }
        if(command.commandInfo().executors().equals(ExecutorType.PLAYER) && isConsole()){
            prepareMessage(CommandMessage.only_player);
            return;
        }
        runnable.run();
    }

    public void hasPermissions(Runnable runnable){
        if(command.permission() != null && !command.permission().isEmpty() && !hasPermission(command.permission())){
            prepareMessage(CommandMessage.permission_message.replace("%permission%", command.permission()));
            return;
        }
        runnable.run();
    }

    public String getMessage(int start, String append, int end){
        return StringUtils.join(commandArguments, append, start, end);
    }

    public void sendMessage(TextComponent component) {
        if(commandSender instanceof Player) ((Player)commandSender).spigot().sendMessage(component);
        else commandSender.sendMessage(component.getExtra().stream().map(a -> a.toLegacyText()).collect(Collectors.joining(" ")));
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }
}
