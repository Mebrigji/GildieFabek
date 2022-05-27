package pl.saidora.core.events;

import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserCombatEvent implements CallbackEvent<UserCombatEvent> {

    private final User victim, attacker;
    private double damage;

    public UserCombatEvent(User victim, User attacker, double damage){
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
    }

    @Override
    public UserCombatEvent getEvent() {
        return this;
    }

    public User getVictim() {
        return victim;
    }

    public User getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public Set<Consumer<UserCombatEvent>> getCache() {
        return EventCache.USER_COMBAT_EVENT;
    }
}
