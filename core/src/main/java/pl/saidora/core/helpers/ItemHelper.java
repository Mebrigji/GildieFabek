package pl.saidora.core.helpers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.saidora.api.helpers.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;


public class ItemHelper extends ItemStack {

    public ItemHelper(Material type) {
        super(type);
    }

    public ItemHelper(Material type, int amount) {
        super(type, amount);
    }

    public ItemHelper(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    public ItemHelper(ItemStack stack) throws IllegalArgumentException {
        super(stack);
    }

    public ItemHelper editMeta(Consumer<ItemMeta> consumer){
        ItemMeta meta = getItemMeta();
        consumer.accept(meta);
        setItemMeta(meta);
        return this;
    }

    public <T extends ItemMeta> ItemHelper editMeta(Class<T> clazz, Consumer<T> consumer){
        ItemMeta meta = getItemMeta();
        if(meta != null){
            consumer.accept((T) meta);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemHelper editStack(Consumer<ItemHelper> consumer){
        consumer.accept(this);
        return this;
    }

    public ItemHelper setSkullOwner(String value, boolean serialized){
        editMeta(SkullMeta.class, skullMeta -> {
            if(!serialized) skullMeta.setOwner(value);
            else {
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", value));

                try {
                    Field field = skullMeta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(skullMeta, profile);
                    field.setAccessible(false);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    private static final Class<?> CraftItemStackClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.CRAFT_BUKKIT, "inventory.CraftItemStack");
    private static final Class<?> NmsItemStackClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "ItemStack");
    private static final Class<?> NbtTagCompoundClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "NBTTagCompound");

    private static final Optional<Method> SAVE = ReflectionHelper.getMethod(NmsItemStackClass, "save", NbtTagCompoundClass);
    private static final Optional<Method> AS_NMS_COPY = ReflectionHelper.getMethod(CraftItemStackClass, "asNMSCopy", ItemStack.class);

    public HoverEvent asHoverEvent(){
        return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponent[]{new TextComponent(convertItemStackToJson())});
    }

    public String convertItemStackToJson(){
        Object nbtCompound = ReflectionHelper.newInstance(NbtTagCompoundClass);
        Object nmsItemStack = ReflectionHelper.invoke(AS_NMS_COPY.get(), this, this);
        return ReflectionHelper.invoke(SAVE.get(), nmsItemStack, nbtCompound).toString();
    }
}
