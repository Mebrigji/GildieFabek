package pl.saidora.core.builder;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.helpers.MessageHolder;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.model.impl.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MessageBuilder {

    private CommandSender executor;

    private String message;

    private Map<String, String> replacements = new HashMap<>();
    private Map<String, MessageHolder> hovers = new HashMap<>();

    private Map<String, Consumer<TextComponent>> multiHovers = new HashMap<>();

    public MessageBuilder(Executor executor, String message){
        this.executor = executor.getCommandSender();
        this.message = message;
    }

    public MessageBuilder(User user, String message){
        this.executor = user.getCommandSender();
        this.message = message;
    }

    public MessageBuilder(Player player, String message) {
        this.executor = player;
        this.message = message;
    }

    public void setExecutor(CommandSender executor) {
        this.executor = executor;
    }

    public MessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public MessageBuilder with(String key, Object value){
        if(value instanceof MessageHolder)
            hovers.put(key, (MessageHolder) value);
        else replacements.put(key, value.toString());
        return this;
    }

    public MessageBuilder withPreparingComponent(String key, Consumer<TextComponent> consumer){
        multiHovers.put(key, consumer);
        return this;
    }

    public void send() {
        TextComponent component = new TextComponent("");
        if (message == null) return;
        replacements.forEach((k, v) -> message = message.replace("%" + k + "%", v));

        AtomicInteger integer = new AtomicInteger();

        if(!hovers.isEmpty())
        hovers.forEach((k, v) -> {
            String[] s = message.split("%" + k + "%", 2);
            if(component.getExtra() == null) {
                component.addExtra(ColorHelper.translateColors(s[0]));
            }
            TextComponent hover = new TextComponent(ColorHelper.translateColors(v.text()));
            if(v.component() == null){
                if(!v.hover().isEmpty()) {
                    hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                            new TextComponent(ColorHelper.translateColors(String.join("\n", v.hover())))}));
                }
            } else hover.setHoverEvent(v.component().getHoverEvent());
            component.addExtra(hover);
            if(last(integer.incrementAndGet()) && s.length == 2) component.addExtra(ColorHelper.translateColors(s[1]));
        });

        if(!multiHovers.isEmpty())
        multiHovers.forEach((k, v) -> {
            TextComponent component1 = new TextComponent();
            String[] s = message.split("%" + k + "%", 2);
            if(component.getExtra() == null){
                component.addExtra(ColorHelper.translateColors(s[0]));
            }
            v.accept(component1);
            component.addExtra(component1);
            if(last(integer.incrementAndGet()) && s.length == 2) component.addExtra(ColorHelper.translateColors(s[1]));
        });

        if(component.getText().isEmpty() && component.getExtra() == null) component.setText(ColorHelper.translateColors(message));
        if(executor instanceof Player) ((Player)executor).spigot().sendMessage(component);
        else executor.sendMessage(component.getExtra().stream().map(a -> a.toLegacyText()).collect(Collectors.joining(" ")));
    }

    private boolean last(int id){
        return id == hovers.size() + multiHovers.size();
    }

    public String getMessage() {
        return message;
    }

    public static TextComponent prepare(MessageHolder holder){
        TextComponent component = new TextComponent();
        component.setText(ColorHelper.translateColors(holder.text()));
        if(holder.component() == null) component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ColorHelper.translateColors(String.join("\n", holder.hover())))}));
        else component.setHoverEvent(holder.component().getHoverEvent());
        return component;
    }
}
