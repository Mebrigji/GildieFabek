package pl.saidora.core.model;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.helpers.MessageHolder;

import java.util.List;

public interface ChatMessage {

    List<MessageHolder> message();

    default TextComponent build(){
        TextComponent textComponent = new TextComponent("");
        message().forEach(messageHolder -> {
            String s = messageHolder.text();
            List<String> strings = messageHolder.hover();
            TextComponent component = new TextComponent(ColorHelper.translateColors(s));
            if(!strings.isEmpty()) component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorHelper.translateColors(String.join("\n", strings))).create()));
            textComponent.addExtra(component);
        });
        return textComponent;
    }

}
