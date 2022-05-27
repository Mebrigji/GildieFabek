package pl.saidora.api.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface Generator {

    Material generateWhat();


    long getDelay();

    void setDelay(long delay);


    int getLevel();

    void setLevel(int level);


    Map<ItemStack, Integer> getUpgradeCost();

}
