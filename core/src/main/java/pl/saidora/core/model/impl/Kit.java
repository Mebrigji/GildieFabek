package pl.saidora.core.model.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.saidora.core.helpers.ItemHelper;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private final String id;

    private List<ItemStack> itemStackList = new ArrayList<>();
    private String permissions = "";
    private long delay;

    private int slot = -1;
    private ItemStack menuItem;

    public Kit(String id) {
        this.id = id;
        menuItem = new ItemHelper(Material.DIRT).editMeta(itemMeta -> itemMeta.setDisplayName(id+""));
    }

    public Kit(String id, List<ItemStack> itemStackList, String permissions, long delay, int slot, ItemStack menuItem) {
        this.id = id;
        this.itemStackList = itemStackList;
        this.permissions = permissions;
        this.delay = delay;
        this.slot = slot;
        this.menuItem = menuItem;
    }

    public String getId() {
        return id;
    }

    public List<ItemStack> getItemStackList() {
        return itemStackList;
    }

    public void setItemStackList(List<ItemStack> itemStackList) {
        this.itemStackList = itemStackList;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public ItemStack getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(ItemStack menuItem) {
        this.menuItem = menuItem;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
