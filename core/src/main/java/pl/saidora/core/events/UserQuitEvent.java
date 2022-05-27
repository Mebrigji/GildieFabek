package pl.saidora.core.events;

import org.bukkit.event.player.PlayerQuitEvent;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public interface UserQuitEvent extends CallbackEvent<UserQuitEvent> {

    User getUser();

    PlayerQuitEvent getBukkitQuitEvent();

    @Override
    default UserQuitEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<UserQuitEvent>> getCache(){
        return EventCache.USER_QUIT_EVENT;
    }
}
