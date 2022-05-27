package pl.saidora.core.model;

import org.bukkit.command.CommandSender;
import pl.saidora.core.builder.MessageBuilder;

import java.util.List;

public interface Sender {

    CommandSender getCommandSender();

    void execute(String commandLine);


    void addMessage(String message);

    void addMessages(List<String> messages);

    void addMessages(String[] messages);

    MessageBuilder prepareMessage(String message);

    MessageBuilder prepareMessage(String... messages);


    List<String> getMessages();


    void send();

    void send(boolean async);

}
