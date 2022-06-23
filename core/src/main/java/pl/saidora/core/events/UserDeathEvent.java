package pl.saidora.core.events;

import com.google.common.util.concurrent.AtomicDouble;
import net.md_5.bungee.api.chat.TextComponent;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UserDeathEvent implements CallbackEvent<UserDeathEvent> {

    private final User victim, killer;
    private Map<User, AtomicDouble> damageMap = new HashMap<>();

    private TextComponent component;

    private int add, remove;

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }

    public User getVictim() {
        return victim;
    }

    public User getKiller() {
        return killer;
    }

    public Map<User, AtomicDouble> getDamageMap() {
        return damageMap;
    }

    public void setDamageMap(Map<User, AtomicDouble> damageMap) {
        this.damageMap = damageMap;
    }

    public UserDeathEvent(User victim, User killer, TextComponent component){
        this.victim = victim;
        this.killer = killer;
        this.component = component;
    }

    public UserDeathEvent(User victim, User killer, String component){
        this(victim, killer, new TextComponent(ColorHelper.translateColors(component)));
    }

    public TextComponent getComponent() {
        return component;
    }

    public void setComponent(TextComponent component) {
        this.component = component;
    }

    public UserDeathEvent(User victim, User killer){
        this.victim = victim;
        this.killer = killer;
    }

    @Override
    public UserDeathEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<UserDeathEvent>> getCache() {
        return EventCache.USER_DEATH_EVENT;
    }
}
