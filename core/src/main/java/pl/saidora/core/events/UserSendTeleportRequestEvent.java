package pl.saidora.core.events;

import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserSendTeleportRequestEvent implements CallbackEvent<UserSendTeleportRequestEvent> {

    private final User executor;
    private final User target;
    private long expireDate;

    private boolean cancelled;

    public UserSendTeleportRequestEvent(User executor, User target, long expireDate){
        this.executor = executor;
        this.target = target;
        this.expireDate = expireDate;
    }

    public User getExecutor() {
        return executor;
    }

    public User getTarget(){
        return target;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public UserSendTeleportRequestEvent getEvent(){
        return this;
    }

    @Override
    public Set<Consumer<UserSendTeleportRequestEvent>> getCache(){
        return EventCache.USER_SEND_TELEPORT_REQUEST;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public User getUser() {
        return executor;
    }
}
