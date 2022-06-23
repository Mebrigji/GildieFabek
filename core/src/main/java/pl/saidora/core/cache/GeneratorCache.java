package pl.saidora.core.cache;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.events.UserBlockBreakEvent;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.model.impl.CachedInventory;
import pl.saidora.core.model.impl.Generator;
import pl.saidora.core.model.impl.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GeneratorCache {

    private final Map<Location, Generator> generatorMap = new HashMap<>();
    private final Map<Location, Generator> regenMap = new HashMap<>();

    public Map<Location, Generator> getRegenMap() {
        return regenMap;
    }

    public Map<Location, Generator> getGeneratorMap() {
        return generatorMap;
    }

    public void breakGenerator(Generator generator, UserBlockBreakEvent event){
        if(event.isCancelled()) return;

        Location location = generator.getLocation();
        if(event.getUser().equals(generator.getOwner())){
            event.setDropItems(false);
            location.getWorld().dropItemNaturally(location, Main.getInstance().getConfiguration().GENERATOR_ITEM);
            generatorMap.remove(location);
            event.getUser().getGeneratorMap().remove(location);
            generator.getOwner().getGeneratorMap().remove(location);
        } else {
            generator.setHealth(generator.getHealth() - 1);
            event.setCancelled(true);
            event.getUser().prepareMessage(Main.getInstance().getConfiguration().EVENT_GENERATOR_DAMAGE_ATTACKER).with("hp", generator.getHealth()).send();
            User owner = generator.getOwner();
            if(owner.isOnline()){
                owner.prepareMessage(Main.getInstance().getConfiguration().EVENT_GENERATOR_DAMAGE_OWNER).with("hp", generator.getHealth()).withPreparingComponent("location", textComponent -> {
                    textComponent.setText("[LOCATION]");
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColorHelper.translateColors("&7X: &d" + location.getBlockX() + "\n&7Y: &d" + location.getBlockY() + "\n&7Z: &d" + location.getBlockZ()))));
                }).send();
            }
            if(generator.getHealth() <= 0){
                location.getBlock().setType(Material.AIR);
                location.getWorld().dropItemNaturally(location, Main.getInstance().getConfiguration().GENERATOR_ITEM);
                generatorMap.remove(generator.getLocation());
                regenMap.remove(generator.getLocation());
            }
        }
    }

    public void breakGenerator(Location location, UserBlockBreakEvent event) {
        if(event.isCancelled()) return;

        Generator generator = generatorMap.get(location);
        if (generator == null) return;

        if(event.getUser().equals(generator.getOwner())){
            event.setDropItems(false);
            location.getWorld().dropItemNaturally(location, Main.getInstance().getConfiguration().GENERATOR_ITEM);
            generatorMap.remove(location);
            event.getUser().getGeneratorMap().remove(location);
            generator.getOwner().getGeneratorMap().remove(location);
        } else {
            generator.setHealth(generator.getHealth() - 1);
            event.setCancelled(true);
            if(generator.getHealth() <= 0){
                location.getBlock().setType(Material.AIR);
                location.getWorld().dropItemNaturally(location, Main.getInstance().getConfiguration().GENERATOR_ITEM);
            }
        }

    }

    public void placeGenerator(Location location, User user, ItemStack itemStack){
        if(itemStack.isSimilar(Main.getInstance().getConfiguration().GENERATOR_ITEM)){
            Generator old = generatorMap.get(location);

            if(old != null) return;

            location.getBlock().setType(Material.AIR);

            if(user.getGeneratorMap().size() >= (user.hasPermission("sacore.limit.vip") ?
                    Main.getInstance().getConfiguration().GENERATORS_PER_USER_FOR_VIP : Main.getInstance().getConfiguration().GENERATORS_PER_USER_FOR_PLAYER)){
                user.sendMessage(Main.getInstance().getConfiguration().EVENT_GENERATOR_LIMIT);
                return;
            }

            Player player = user.asPlayer().orElse(null);

            if(user.getLastGeneratorMaterial() == null || player == null || !player.isSneaking()) {

                CachedInventory cachedInventory = new CachedInventory(user, Bukkit.createInventory(null, 3 * 9, "Wybor generatora"));

                cachedInventory.setItemRange(0, 27, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 7).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
                cachedInventory.setItem(11, new ItemHelper(Material.STONE).editMeta(itemMeta -> {
                    itemMeta.setDisplayName(ColorHelper.translateColors("&d* &7Generator Kamienia &d*"));
                    itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Jeśli chcesz, aby generator tworzył kamien", "&dto kliknij na ten item", " ", "&7Posiadane generatory kamienia: &d" + user.getGeneratorMap().values().stream().filter(generator -> generator.getGenerate().equals(Material.STONE)).count())));
                }));
                cachedInventory.setItem(13, new ItemHelper(Main.getInstance().getConfiguration().GENERATOR_ITEM.getType()).editMeta(itemMeta -> {
                    itemMeta.setDisplayName(ColorHelper.translateColors("&dStan generatorów"));
                    itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Liczba posiadanych generatorów: &d" + user.getGeneratorMap().size(), " ",
                            "&7Generatory kamienia: &d" + user.getGeneratorMap().values().stream().filter(generator -> generator.getGenerate().equals(Material.STONE)).count(),
                            "&7Generatory obsydianu: &d" + user.getGeneratorMap().values().stream().filter(generator -> generator.getGenerate().equals(Material.OBSIDIAN)).count(),
                            " ",
                            "&7Liczba dostępnych generatorów: &d" + (user.hasPermission("sacore.limit.vip") ? Main.getInstance().getConfiguration().GENERATORS_PER_USER_FOR_VIP - user.getGeneratorMap().size() : Main.getInstance().getConfiguration().GENERATORS_PER_USER_FOR_PLAYER - user.getGeneratorMap().size()))));
                }));
                cachedInventory.setItem(15, new ItemHelper(Material.OBSIDIAN).editMeta(itemMeta -> {
                    itemMeta.setDisplayName(ColorHelper.translateColors("&d* &7Generator Obsydianu &d*"));
                    itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Jeśli chcesz, aby generator tworzył obsydian", "&dto kliknij na ten item", " ", "&7Posiadane generatory obsydianu: &d" + user.getGeneratorMap().values().stream().filter(generator -> generator.getGenerate().equals(Material.OBSIDIAN)).count())));
                }));

                cachedInventory.whenClick(event -> {
                    event.setBlockClick(true);
                    Generator generator;
                    if (event.getSlot() == 11) {
                        generator = new Generator(user, Material.STONE);
                        generator.setDelay(Main.getInstance().getConfiguration().GENERATOR_STONE_DELAY);
                        generator.setCountdown(generator.getDelay());
                        generator.setLocation(location);
                        generator.setUpgradeCost(Main.getInstance().getConfiguration().GENERATOR_STONE_UPGRADE_COST_PER_LEVEL);
                        generator.setHealth(Main.getInstance().getConfiguration().GENERATOR_STONE_HEALTH);
                        cachedInventory.close();
                        user.getGeneratorMap().put(location, generator);
                        generatorMap.put(location, generator);
                        location.getBlock().setType(generator.getGenerate());
                        user.setLastGeneratorMaterial(Material.STONE);
                    } else if (event.getSlot() == 15) {
                        generator = new Generator(user, Material.OBSIDIAN);
                        generator.setDelay(Main.getInstance().getConfiguration().GENERATOR_OBSIDIAN_DELAY);
                        generator.setCountdown(generator.getDelay());
                        generator.setLocation(location);
                        generator.setUpgradeCost(Main.getInstance().getConfiguration().GENERATOR_OBSIDIAN_UPGRADE_COST_PER_LEVEL);
                        generator.setHealth(Main.getInstance().getConfiguration().GENERATOR_OBSIDIAN_HEALTH);
                        cachedInventory.close();
                        user.getGeneratorMap().put(location, generator);
                        generatorMap.put(location, generator);
                        location.getBlock().setType(generator.getGenerate());
                        user.setLastGeneratorMaterial(Material.OBSIDIAN);
                    }
                });
                cachedInventory.open();
            } else {
                Generator generator;
                if(user.getLastGeneratorMaterial().equals(Material.STONE)){
                    generator = new Generator(user, Material.STONE);
                    generator.setDelay(Main.getInstance().getConfiguration().GENERATOR_STONE_DELAY);
                    generator.setCountdown(generator.getDelay());
                    generator.setLocation(location);
                    generator.setUpgradeCost(Main.getInstance().getConfiguration().GENERATOR_STONE_UPGRADE_COST_PER_LEVEL);
                    generator.setHealth(Main.getInstance().getConfiguration().GENERATOR_STONE_HEALTH);
                } else {
                    generator = new Generator(user, Material.OBSIDIAN);
                    generator.setDelay(Main.getInstance().getConfiguration().GENERATOR_OBSIDIAN_DELAY);
                    generator.setCountdown(generator.getDelay());
                    generator.setLocation(location);
                    generator.setUpgradeCost(Main.getInstance().getConfiguration().GENERATOR_OBSIDIAN_UPGRADE_COST_PER_LEVEL);
                    generator.setHealth(Main.getInstance().getConfiguration().GENERATOR_OBSIDIAN_HEALTH);
                }
                user.getGeneratorMap().put(location, generator);
                generatorMap.put(location, generator);
                location.getBlock().setType(generator.getGenerate());
            }
        }
    }

}
