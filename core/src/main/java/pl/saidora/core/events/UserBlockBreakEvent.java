package pl.saidora.core.events;

import org.bukkit.block.Block;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserBlockBreakEvent implements CallbackEvent<UserBlockBreakEvent> {

    private final User user;
    private final Block block;
    private boolean dropItems = true, cancelled;

    public User getUser() {
        return user;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public UserBlockBreakEvent(User user, Block block){
        this.user = user;
        this.block = block;
    }

    @Override
    public UserBlockBreakEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<UserBlockBreakEvent>> getCache() {
        return EventCache.USER_BLOCK_BREAK_EVENT;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
