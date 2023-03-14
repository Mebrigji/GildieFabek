package pl.saidora.core.helpers;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.helpers.RandomHelper;

public class MaterialHelper {

    public static boolean isTool(ItemStack itemStack){
        if(itemStack == null || itemStack.getType().equals(Material.AIR)) return false;
        return isTool(itemStack.getType());
    }

    public static boolean isTool(Material material){
        return material.getMaxDurability() > 10;
    }

    public static void updateDurability(Player player) {
        
        ItemStack item = player.getItemInHand();
        if (item.getType().getMaxDurability() != 0) {
            int enchantLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
            short d = item.getDurability();
            if (enchantLevel > 0) {
                if (100 / (enchantLevel + 1) > RandomHelper.getInt(0, 100)) {
                    if (d == item.getType().getMaxDurability()) {
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                    } else {
                        item.setDurability((short) (d + 1));
                    }
                }
            } else if (d == item.getType().getMaxDurability()) {
                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            } else {
                item.setDurability((short) (d + 1));
            }
        }
    }
}
