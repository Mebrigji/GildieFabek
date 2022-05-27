package pl.saidora.core.helpers;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface MessageHolder {

    static MessageHolder create(String text, List<String> hover){
        return new MessageHolder() {
            @Override
            public String text() {
                return text;
            }

            @Override
            public List<String> hover() {
                return hover;
            }
        };
    }

    static MessageHolder create(String text, ItemStack itemStack){
        return new MessageHolder() {
            @Override
            public String text() {
                return text;
            }

            @Override
            public List<String> hover() {
                return new ArrayList<>();
            }

            @Override
            public TextComponent component() {
                TextComponent component = new TextComponent();
                component.setHoverEvent(new ItemHelper(itemStack).asHoverEvent());
                return component;
            }
        };
    }

    static MessageHolder create(String text){
        return create(text, new ArrayList<>());
    }

    String text();

    List<String> hover();

    default TextComponent component(){return null;}

}
