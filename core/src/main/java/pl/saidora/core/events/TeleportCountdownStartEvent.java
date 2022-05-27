package pl.saidora.core.events;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public interface TeleportCountdownStartEvent extends CallbackEvent<TeleportCountdownStartEvent> {

    User getUser();

    long getCount();

    Location from();

    Location to();

    BukkitTask getBukkitTask();

    @Override
    default TeleportCountdownStartEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<TeleportCountdownStartEvent>> getCache(){
        return EventCache.USER_TELEPORT_COUNTDOWN_START_EVENT;
    }
}
