package pl.saidora.core.model.impl;

import org.bukkit.inventory.Inventory;
import pl.saidora.core.events.UserInventoryClickEvent;

import java.util.Optional;
import java.util.function.Consumer;


public class InventoryHolder {

    private final User user;
    private final Inventory inventory;

    private Consumer<UserInventoryClickEvent> eventConsumer;

    public InventoryHolder(User user, Inventory inventory){
        this.user = user;
        this.inventory = inventory;
    }

    public void onClick(UserInventoryClickEvent event){
        if(eventConsumer == null) return;
        eventConsumer.accept(event);
    }

    public void whenClick(Consumer<UserInventoryClickEvent> eventConsumer){
        this.eventConsumer = eventConsumer;
    }

    public User getUser() {
        return user;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Consumer<UserInventoryClickEvent> getEventConsumer() {
        return eventConsumer;
    }

    public void setEventConsumer(Consumer<UserInventoryClickEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    public Optional<CachedInventory> asCached(){
        return Optional.ofNullable(this instanceof CachedInventory ? (CachedInventory) this : null);
    }
}
