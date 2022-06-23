package pl.saidora.core.configuration.serializers;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventorySerializer implements ObjectSerializer<Inventory> {
    @Override
    public boolean supports(Class<? super Inventory> type) {
        return Inventory.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(Inventory object, SerializationData data) {
        data.add("title", object.getTitle().replace("§", "&"));
        data.add("size", object.getSize());
        data.addArray("contents", object.getContents(), ItemStack.class);
    }

    @Override
    public Inventory deserialize(DeserializationData data, GenericsDeclaration generics) {
        Inventory inventory = Bukkit.createInventory(null, data.get("size", int.class), data.get("title", String.class).replace("&", "§"));
        inventory.setContents(data.getAsList("contents", ItemStack.class).toArray(new ItemStack[]{}));
        return inventory;
    }
}
