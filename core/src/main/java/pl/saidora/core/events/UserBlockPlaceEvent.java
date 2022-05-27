package pl.saidora.core.events;

import org.bukkit.block.Block;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserBlockPlaceEvent implements CallbackEvent<UserBlockPlaceEvent> {

    private final User user;
    private final Block block;
    private boolean cancelled;

    public User getUser() {
        return user;
    }

    public Block getBlock() {
        return block;
    }

    public UserBlockPlaceEvent(User user, Block block){
        this.user = user;
        this.block = block;
    }

    @Override
    public UserBlockPlaceEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<UserBlockPlaceEvent>> getCache() {
        return EventCache.USER_BLOCK_PLACE_EVENT;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
