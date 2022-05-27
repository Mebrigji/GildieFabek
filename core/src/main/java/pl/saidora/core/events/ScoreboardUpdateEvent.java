package pl.saidora.core.events;

import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.ScoreBoard;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class ScoreboardUpdateEvent implements CallbackEvent<ScoreboardUpdateEvent> {

    private final User user;

    private boolean removePacket, cancelled;

    public User getUser() {
        return user;
    }

    public boolean isRemovePacket() {
        return removePacket;
    }

    public void setRemovePacket(boolean removePacket) {
        this.removePacket = removePacket;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ScoreboardUpdateEvent(User user){
        this.user = user;
    }

    public ScoreBoard getScoreBoard(){
        return user.getScoreboard().orElse(null);
    }

    @Override
    public ScoreboardUpdateEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<ScoreboardUpdateEvent>> getCache() {
        return EventCache.SCOREBOARD_UPDATE_EVENT;
    }
}
