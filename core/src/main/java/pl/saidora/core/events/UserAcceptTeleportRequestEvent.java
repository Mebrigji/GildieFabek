package pl.saidora.core.events;

import org.bukkit.Location;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface UserAcceptTeleportRequestEvent extends CallbackEvent<UserAcceptTeleportRequestEvent> {

    List<User> getAcceptedUsers();

    Location getTeleportLocation();

    long getDelayTime();

    @Override
    default UserAcceptTeleportRequestEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<UserAcceptTeleportRequestEvent>> getCache(){
        return EventCache.USER_ACCEPT_TELEPORT_REQUEST;
    }

    User getUser();
}
