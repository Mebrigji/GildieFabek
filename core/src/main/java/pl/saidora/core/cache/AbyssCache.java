package pl.saidora.core.cache;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.saidora.core.Main;
import pl.saidora.core.configuration.Configuration;
import pl.saidora.core.model.impl.Abyss;

import java.util.HashMap;
import java.util.Map;

public class AbyssCache {

    private final Map<Integer, Abyss> abyssMap = new HashMap<>();
    private long counter;

    public void addItem(ItemStack[] itemStack){
        getPage(0).addItem(itemStack);
    }

    public boolean isOpened(){
        return isOpened(counter);
    }

    public boolean isOpened(long time){
        return time >= (Main.getInstance().getConfiguration().ABYSS_OPEN_TIME * 2) *- 1 && counter < 0;
    }

    public Abyss getPage(int page){
        Abyss abyss = abyssMap.get(page);
        if(abyss == null) {
            abyss = new Abyss(page);
            abyssMap.put(page, abyss);
        }
        return abyss;
    }

    public Inventory prepareInventory(Abyss abyss){
        int page = abyss.getPage();
        Abyss prev = abyssMap.get(page - 1), next = abyssMap.get(page + 1);
        Inventory inventory = abyss.getInventory();

        Configuration configuration = Main.getInstance().getConfiguration();

        inventory.setItem(4*9+3, prev == null ? configuration.ABYSS_GUI_PREVIOUS_EMPTY : configuration.ABYSS_GUI_PREVIOUS_EXISTS);
        inventory.setItem(4*9+5, next == null ? configuration.ABYSS_GUI_NEXT_EMPTY : configuration.ABYSS_GUI_NEXT_EXISTS);
        return inventory;
    }

    public Inventory prepareInventory(int page){
        return prepareInventory(getPage(page));
    }

    public Map<Integer, Abyss> getAbyssMap() {
        return abyssMap;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}
