package pl.saidora.core.model.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.saidora.core.Main;
import pl.saidora.core.helpers.ItemHelper;

import java.util.Collection;
import java.util.Optional;

public class Abyss {

    private final int page;

    private final Inventory inventory;

    public Abyss(int page) {
        this.page = page;
        inventory = Bukkit.createInventory(null, 5*9, "Otchlan ( strona: " + page + " )");
        ItemStack glass = new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short)7).editMeta(itemMeta -> itemMeta.setDisplayName(" "));
        inventory.setItem(4*9, glass);
        inventory.setItem(4*9+1, glass);
        inventory.setItem(4*9+2, glass);

        inventory.setItem(4*9+3, glass);
        inventory.setItem(4*9+4, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short)2).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
        inventory.setItem(4*9+5, glass);

        inventory.setItem(4*9+6, glass);
        inventory.setItem(4*9+7, glass);
        inventory.setItem(4*9+8, glass);
    }

    public int getPage() {
        return page;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public synchronized void addItem(ItemStack[] itemStack){
        Collection<ItemStack> itemStackList = inventory.addItem(itemStack).values();
        if(!itemStackList.isEmpty())
            Main.getInstance()
                    .getAbyssCache()
                    .getPage(page + 1)
                    .addItem(itemStackList.toArray(new ItemStack[]{}));
    }

    public Optional<Abyss> getNext(){
        return Optional.ofNullable(Main.getInstance().getAbyssCache().getAbyssMap().get(page + 1));
    }
    public Optional<Abyss> getPrevious(){
        return Optional.ofNullable(Main.getInstance().getAbyssCache().getAbyssMap().get(page - 1));
    }
}
