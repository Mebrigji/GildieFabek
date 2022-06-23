package pl.saidora.core.cache;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.factory.NewerOptional;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.model.impl.Kit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Header("####################")
@Header("# Kit Cache ")
@Header("####################")
public class KitCache extends OkaeriConfig {

    public Inventory PREVIEW = LambdaBypass.get(Bukkit.createInventory(null, 5*9, ColorHelper.translateColors("&7Przegladasz zestaw: &d%kit%")), inventory -> {
        ItemHelper glass = new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" "));
        inventory.setItem(5*9-6, glass);
        inventory.setItem(5*9-5, new ItemHelper(Material.FENCE_GATE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&dPowrot"))));
        inventory.setItem(5*9-4, glass);
        return inventory;
    }).getObject();
    public Inventory MENU = LambdaBypass.get(Bukkit.createInventory(null, 5*9, ColorHelper.translateColors("&7Zestawy")), inventory -> {
        ItemHelper gray = new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 7).editMeta(itemMeta -> itemMeta.setDisplayName(" "));
        ItemHelper pink = new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" "));

        List<Integer> pinkSlots = Arrays.asList(1, 2, 3, 5, 6, 7, 9, 2*9-1, 2*9, 3*9-1, 3*9, 4*9-1, 4*9+1, 4*9+2, 4*9+3, 4*9+5, 4*9+6, 4*9+7);

        for (int i = 0; i < inventory.getSize(); i++) {
            if(pinkSlots.contains(i)) inventory.setItem(i, pink);
            else inventory.setItem(i, gray);
        }
        return inventory;
    }).getObject();

    public Inventory CLAIM = LambdaBypass.get(Bukkit.createInventory(null, 4*9, ColorHelper.translateColors("&7Odbierasz zestaw: &d%kit%")), inventory -> inventory).getObject();

    private final List<Kit> cache = Arrays.asList(
            new Kit("player", Collections.singletonList(new ItemHelper(Material.STONE_PICKAXE)
                    .editMeta(itemMeta -> itemMeta.addEnchant(Enchantment.DURABILITY, 2, true))), "kit.gracz", 120000, 20, new ItemHelper(Material.WOOD_PICKAXE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Zestaw GRACZ")))),
            new Kit("vip", Collections.singletonList(new ItemHelper(Material.IRON_PICKAXE)
                    .editMeta(itemMeta -> {
                        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                        itemMeta.addEnchant(Enchantment.DIG_SPEED, 3, true);
                    })), "kit.vip", 120000, 21, new ItemHelper(Material.STONE_PICKAXE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Zestaw &eVIP"))))
    );

    public List<Kit> getCache() {
        return cache;
    }

    public boolean createKit(Kit kit){
        if(cache.equals(kit)) return false;
        cache.add(kit);
        return true;
    }

    public NewerOptional<Kit> find(String kitName) {
        return NewerOptional.fromOldOptional(cache.stream().filter(kit -> kit.getId().equals(kitName)).findAny());
    }
}
