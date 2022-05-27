package pl.saidora.core.model;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.saidora.core.model.impl.User;

import java.util.stream.Stream;

public interface Home {

    String getName();

    Location getLocation();

    ItemStack getTeleportCost();

    default boolean canTeleport(User user){
        return user.asPlayer().isPresent() && user.asPlayer().get().getInventory().containsAtLeast(getTeleportCost(), getTeleportCost().getAmount());
    }

    default boolean isSafe(){
        return Stream.of(getLocation(), getLocation().clone().subtract(0, 1, 0), getLocation().clone()).anyMatch(location -> {
            Material blockType = location.getBlock().getType();
            return !blockType.equals(Material.LAVA) && !blockType.equals(Material.STATIONARY_LAVA) && !blockType.equals(Material.AIR);
        });
    }

}
