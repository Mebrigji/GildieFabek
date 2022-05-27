package pl.saidora.core.events;

import org.bukkit.entity.Entity;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public interface UserDamageGivenEvent extends CallbackEvent<UserDamageGivenEvent> {

    User getUser();

    double getDamage();

    Entity getAttacker();

    @Override
    default UserDamageGivenEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<UserDamageGivenEvent>> getCache(){
        return EventCache.USER_DAMAGE_GIVEN_EVENT;
    }
}
