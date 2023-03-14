package pl.saidora.core.events;

import org.bukkit.entity.Entity;
import pl.saidora.api.functions.DoubleProvider;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserDamageEvent implements CallbackEvent<UserDamageEvent> {

    private final User victim;
    private final DoubleProvider<User, Entity> provider;
    private double damage;
    private boolean cancelled;

    public UserDamageEvent(User victim, DoubleProvider<User, Entity> provider) {
        this.victim = victim;
        this.provider = provider;
    }


    @Override
    public UserDamageEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<UserDamageEvent>> getCache() {
        return EventCache.USER_DAMAGE_EVENT;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public DoubleProvider<User, Entity> getProvider() {
        return provider;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }

    public User getVictim() {
        return victim;
    }
}
