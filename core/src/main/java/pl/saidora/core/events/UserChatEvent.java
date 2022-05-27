package pl.saidora.core.events;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.model.ChatMessage;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserChatEvent implements CallbackEvent<UserChatEvent> {

    private final User user;
    private final AsyncPlayerChatEvent playerChatEvent;

    private ChatMessage message;

    private boolean cancelled;

    public UserChatEvent(User user, AsyncPlayerChatEvent asyncPlayerChatEvent, ChatMessage chatMessage){
        this.user = user;
        this.playerChatEvent = asyncPlayerChatEvent;
        this.message = chatMessage;
    }

    public User getUser(){
        return user;
    }

    public AsyncPlayerChatEvent getBukkitChatEvent(){
        return playerChatEvent;
    }

    @Override
    public UserChatEvent getEvent(){
        return this;
    }

    @Override
    public Set<Consumer<UserChatEvent>> getCache(){
        return EventCache.USER_CHAT_EVENT;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
