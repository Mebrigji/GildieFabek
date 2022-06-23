package pl.saidora.core.events;

import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserDamageGivenEvent implements CallbackEvent<UserDamageGivenEvent> {

    private final User user;
    private double damage;

    public User getUser() {
        return user;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public UserDamageGivenEvent(User user){
        this.user = user;
    }

    @Override
    public UserDamageGivenEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<UserDamageGivenEvent>> getCache() {
        return EventCache.USER_DAMAGE_GIVEN_EVENT;
    }
}
