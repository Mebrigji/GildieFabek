package pl.saidora.core.model.impl;


import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CachedInventory extends InventoryHolder{

    public CachedInventory(User user, Inventory inventory){
        super(user, inventory);
    }

    public Map<Integer, ItemStack> addItem(ItemStack... itemStacks){
        return getInventory().addItem(itemStacks);
    }

    public void setItemInSlots(int[] slots, ItemStack itemStack){
        for (int slot : slots) {
            setItem(slot, itemStack);
        }
    }

    public void setItem(int slot, ItemStack itemStack){
        getInventory().setItem(slot, itemStack);
    }

    public void setItemRange(int from, int to, ItemStack itemStack){
        if(from > to) return;
        for (int i = from; i < to; i++) {
            setItem(i, itemStack);
        }
    }

    public void setItemRange(int from, int to, Function<User, ItemStack> function) {
        if (from > to) return;
        for (int i = from; i < to; i++) {
            setItem(i, function.apply(getUser()));
        }
    }

    public CompletableFuture<User> open(){
        User user = getUser();
        user.asPlayer().ifPresent(player -> player.openInventory(getInventory()));
        user.setInventoryHolder(CachedInventory.this);
        return CompletableFuture.completedFuture(user);
    }

    public CompletableFuture<User> close(){
        User user = getUser();
        user.asPlayer().ifPresent(HumanEntity::closeInventory);
        return CompletableFuture.completedFuture(user);
    }
}
