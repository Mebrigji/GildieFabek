package pl.saidora.core.events;

import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.InventoryHolder;

import java.util.Set;
import java.util.function.Consumer;

public class UserInventoryClickEvent implements CallbackEvent<UserInventoryClickEvent> {

    private final InventoryHolder inventoryHolder;

    private boolean cancelled, blockClick;

    private final int slot;

    public UserInventoryClickEvent(InventoryHolder inventoryHolder, int slot){
        this.inventoryHolder = inventoryHolder;
        this.slot = slot;
    }

    @Override
    public UserInventoryClickEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<UserInventoryClickEvent>> getCache() {
        return EventCache.USER_INVENTORY_CLICK_EVENT;
    }

    public InventoryHolder getInventoryHolder() {
        return inventoryHolder;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isBlockClick() {
        return blockClick;
    }

    public void setBlockClick(boolean blockClick) {
        this.blockClick = blockClick;
    }

    public int getSlot() {
        return slot;
    }
}
