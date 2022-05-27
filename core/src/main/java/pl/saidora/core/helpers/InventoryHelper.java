package pl.saidora.core.helpers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryHelper {

    public static int getAmountOf(Player player, ItemStack itemStack){
        int amount = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if(item == null || item.getType() == Material.AIR || !item.isSimilar(itemStack)) continue;
            amount += item.getAmount();
        }
        return amount;
    }

    public static int getAmountOf(Player player, Material material){
        int amount = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if(item == null || !item.getType().equals(material)) continue;
            amount += item.getAmount();
        }
        return amount;
    }

    public static void removeItems(Player player, ItemStack itemStack, int amount){
        itemStack.setAmount(amount);
        player.getInventory().removeItem(itemStack);
    }

    public static void giveItem(Player player, ItemStack itemStack, Consumer<ItemStack> itemStackConsumer){
        player.getInventory().addItem(itemStack).values().forEach(itemStackConsumer);
    }

    public static void giveItem(Player player, ItemStack[] itemStacks, Consumer<ItemStack> itemStackConsumer){
        player.getInventory().addItem(itemStacks).values().forEach(itemStackConsumer);
    }
}
