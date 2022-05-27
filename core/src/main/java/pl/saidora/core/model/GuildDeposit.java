package pl.saidora.core.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.saidora.core.model.impl.User;

import java.util.List;
import java.util.Map;

public interface GuildDeposit {

    Map<Material, Integer> getContainer();

    void addItem(ItemStack itemStack);


    int getAmountOf(Material material);


    boolean contains(Material material);


    List<User> getHolders();

    void open(User user);


    Map<Long, GuildHistory> getHistory();

}
