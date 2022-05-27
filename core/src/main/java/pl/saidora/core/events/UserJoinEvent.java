package pl.saidora.core.events;

import org.bukkit.event.player.PlayerJoinEvent;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public interface UserJoinEvent extends CallbackEvent<UserJoinEvent> {

    User getUser();

    PlayerJoinEvent getBukkitJoinEvent();

    @Override
    default UserJoinEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<UserJoinEvent>> getCache(){
        return EventCache.USER_JOIN_EVENT;
    }
}
