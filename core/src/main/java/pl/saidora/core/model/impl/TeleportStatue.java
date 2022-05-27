package pl.saidora.core.model.impl;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class TeleportStatue {

    private final Location location;

    private String requiredPermissions;
    private ItemStack teleportCost;

    public TeleportStatue(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(String requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }

    public ItemStack getTeleportCost() {
        return teleportCost;
    }

    public void setTeleportCost(ItemStack teleportCost) {
        this.teleportCost = teleportCost;
    }
}
