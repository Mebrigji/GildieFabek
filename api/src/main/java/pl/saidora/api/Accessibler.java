package pl.saidora.api;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import pl.saidora.api.helpers.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Accessibler {

    private static final Class<?> CraftServerClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.CRAFT_BUKKIT, "CraftServer");
    private static final Optional<Field> commandMapField = ReflectionHelper.getField(CraftServerClass, "commandMap");
    private static final Optional<Field> knownCommandsField = ReflectionHelper.getField(SimpleCommandMap.class, "knownCommands");

    public Map<String, Command> getKnowCommands(Server server){
        AtomicReference<Map<String, Command>> reference = new AtomicReference<>(new HashMap<>());
        commandMapField.ifPresent(field -> {
            try {
                field.setAccessible(true);
                SimpleCommandMap commandMap = (SimpleCommandMap) field.get(server);
                knownCommandsField.ifPresent(field1 -> {
                    try {
                        field1.setAccessible(true);
                        reference.set((Map<String, Command>) field1.get(commandMap));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return reference.get();
    }

    public Optional<Command> getCommand(String commandName, Server server){
        return getKnowCommands(server).values().stream().filter(command -> command.getName().equals(commandName)).findFirst();
    }

    public void removeCommand(Command command, Server server){
        Map<String, Command> knowCommands = getKnowCommands(server);
        knowCommands.remove(command.getName().toLowerCase());
        command.getAliases().forEach(s -> knowCommands.remove(s.toLowerCase()));
    }

    public void addCommand(Command command, Server server){
        addCommand("accessibler", command, server);
    }

    public void addCommand(String fallbackPrefix, Command command, Server server){
        getCommandMap(server).register(fallbackPrefix, command);
    }

    public void setKnowCommands(Server server, Map<String, Command> commands){
        commandMapField.ifPresent(field -> {
            try {
                field.setAccessible(true);
                SimpleCommandMap commandMap = (SimpleCommandMap) field.get(server);
                knownCommandsField.ifPresent(field1 -> {
                    try {
                        field1.setAccessible(true);
                        field1.set(commandMap, commands);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public SimpleCommandMap getCommandMap(Server server){
        AtomicReference<SimpleCommandMap> reference = new AtomicReference<>();
        commandMapField.ifPresent(field -> {
            try {
                field.setAccessible(true);
                reference.set((SimpleCommandMap) field.get(server));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return reference.get();
    }
}
