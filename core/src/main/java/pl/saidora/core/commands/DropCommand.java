package pl.saidora.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.model.impl.CachedInventory;
import pl.saidora.core.model.impl.User;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@CommandInfo(name = "drop", usage = "", permission = "", aliases = {"drops"}, executors = ExecutorType.PLAYER)
public class DropCommand implements Command {

    private static final DecimalFormat decimalFormat = new DecimalFormat("##.##");

    @Override
    public void run(Executor executor) {
        User user = Main.getInstance().getOnlineUsers().get(executor.getName());
        openMenu(user, 0);
    }

    private void openMenu(User user, int categoryID){
        AtomicInteger id = new AtomicInteger(categoryID);
        CachedInventory cachedInventory = new CachedInventory(user, Bukkit.createInventory(null, 5*9, "Drop"));

        cachedInventory.setItemInSlots(new int[]{0, 1, 3, 5, 7, 8, 10, 16, 18, 26, 28, 34, 36, 37, 39, 41, 43, 44}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 7).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
        cachedInventory.setItemInSlots(new int[]{2, 4, 6, 9, 17, 27, 35, 38, 40, 42}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
        cachedInventory.setItemInSlots(new int[]{21, 23}, new ItemHelper(Material.STAINED_GLASS_PANE).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));

        cachedInventory.setItemInSlots(new int[]{11, 13, 15, 30, 32}, new ItemHelper(Material.VINE).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));

        cachedInventory.setItem(12, new ItemHelper(Material.ANVIL).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&7Napraw swoj kilof"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Wydales na to juz: &d" + user.getPaidExperience() + " exp.")));
        }));

        cachedInventory.setItem(14, new ItemHelper(Material.ENDER_STONE).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&dGeneratory"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Posiadasz &d" + user.getGeneratorMap().size() + " &7generatorow.", "&7w tym &d" + user.getGeneratorMap().values().stream().filter(generator -> generator.getGenerate().equals(Material.OBSIDIAN)).count() + " &7obsydianu i &d" + user.getGeneratorMap().values().stream().filter(generator -> generator.getGenerate().equals(Material.STONE)).count() + " &7kamienia.", " ", "&7Kliknij aby usunac lub ulepszyc generator.")));
        }));

        cachedInventory.setItem(19, new ItemHelper(Material.SKULL_ITEM, 1, (short) 3).setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", true).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&7Przewin kategorie w lewo"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Kliknij klawisz &fSHIFT &7i kliknij na przycisk", "&7aby otworzyc strone z wszystkimi kategoriami.")));
        }));

        cachedInventory.setItem(25, new ItemHelper(Material.SKULL_ITEM, 1, (short) 3).setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", true).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&7Przewin kategorie w prawo"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Kliknij klawisz &fSHIFT &7i kliknij na przycisk", "&7aby otworzyc strone z wszystkimi kategoriami.")));
        }));

        cachedInventory.setItem(29, new ItemHelper(Material.MONSTER_EGG, 1, (short) 120)
                .editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&aSklep"))));

        cachedInventory.setItem(31, new ItemHelper(Material.ENCHANTED_BOOK).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&5* &7Turbo &5*"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Kliknij aby wyswietlic wszystkie", "&7rodzaje &dturbo&7 lub je &aaktywuj.")));
        }));

        cachedInventory.setItem(33, new ItemHelper(Material.IRON_PICKAXE).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&eTopka kopaczy"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Kliknij aby wyswietlic topke kopaczy", " ",
                    "&8* &7Twoja pozycja: &d" + Main.getInstance().getLeaderboardCache().get("userLevel", User.class).get().getPosition(user),
                    " ",
                    "&8* &7Poziom: &d" + user.getLevel(),
                    "&8* &7Doswiadczenie: &d" + user.getExp() + "&8/&d" + user.getRequiredExp(),
                    "&8* &7Postep: &d" + decimalFormat.format((user.getExp() * 100.0 )/ user.getRequiredExp()) + "%",
                    "&8* &7Pozostalo: &d" + (user.getRequiredExp() - user.getExp()))));
        }));

        setCategoriesByID(cachedInventory, id, 0);

        cachedInventory.open();
    }

    private void setCategoriesByID(CachedInventory cachedInventory, AtomicInteger id, int change){
        int categoryID = id.get() + change;

        if(categoryID < 0) categoryID = 4;
        else if(categoryID > 4) categoryID = 0;

        Map<Integer, Runnable> runnableMap = new HashMap<>();

        id.set(categoryID);

        User user = cachedInventory.getUser();

        if(categoryID == 0){
            cachedInventory.setItem(20, new ItemHelper(Material.CHEST).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&e&lPremium&6&lCase"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &e&lPremium&6&lCase")));
            }));
            cachedInventory.setItem(22, new ItemHelper(Material.STONE).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&d&lKamien"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &dKamienia")));
            }));
            cachedInventory.setItem(24, new ItemHelper(Material.MOSSY_COBBLESTONE).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&a&lCobble&f&lX"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &da&lCobble&f&lX")));
            }));

            runnableMap.put(20, () -> openDrop(user, 0));
            runnableMap.put(22, () -> openDrop(user, 1));
            runnableMap.put(24, () -> openDrop(user, 2));
        } else if(categoryID == 1){
            cachedInventory.setItem(20, new ItemHelper(Material.STONE).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&fKamien"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &dKamienia")));
            }));
            cachedInventory.setItem(22, new ItemHelper(Material.MOSSY_COBBLESTONE).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&a&lCobble&f&lX"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &a&lCobble&f&lX")));
            }));
            cachedInventory.setItem(24, new ItemHelper(Material.LEAVES).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&aLiscie"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &aLiscie")));
            }));

            runnableMap.put(20, () -> openDrop(user, 1));
            runnableMap.put(22, () -> openDrop(user, 2));
            runnableMap.put(24, () -> openDrop(user, 3));
        } else if(categoryID == 2){
            cachedInventory.setItem(20, new ItemHelper(Material.MOSSY_COBBLESTONE).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&a&lCobble&f&lX"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &a&lCobble&f&lX")));
            }));
            cachedInventory.setItem(22, new ItemHelper(Material.LEAVES).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&aLiscie"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &aLiscie")));
            }));
            cachedInventory.setItem(24, new ItemHelper(Material.DRAGON_EGG).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&5Pierozek"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &dPierozek")));
            }));

            runnableMap.put(20, () -> openDrop(user, 2));
            runnableMap.put(22, () -> openDrop(user, 3));
            runnableMap.put(24, () -> openDrop(user, 4));
        } else if(categoryID == 3){
            cachedInventory.setItem(20, new ItemHelper(Material.LEAVES).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&aLiscie"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &aLiscie")));
            }));
            cachedInventory.setItem(22, new ItemHelper(Material.DRAGON_EGG).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&5Pierozek"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &dPierozek")));
            }));
            cachedInventory.setItem(24, new ItemHelper(Material.CHEST).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&e&lPremium&6&lCase"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &e&lPremium&6&lCase")));
            }));

            runnableMap.put(20, () -> openDrop(user, 3));
            runnableMap.put(22, () -> openDrop(user, 4));
            runnableMap.put(24, () -> openDrop(user, 0));
        } else if(categoryID == 4){
            cachedInventory.setItem(20, new ItemHelper(Material.DRAGON_EGG).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&5Pierozek"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &dPierozek")));
            }));
            cachedInventory.setItem(22, new ItemHelper(Material.CHEST).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&e&lPremium&6&lCase"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &e&lPremium&6&lCase")));
            }));
            cachedInventory.setItem(24, new ItemHelper(Material.STONE).editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&fKamien"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Po kliknieciu otworzysz okno", "&7gdzie znajduja sie dropy z &dKamienia")));
            }));

            runnableMap.put(20, () -> openDrop(user, 4));
            runnableMap.put(22, () -> openDrop(user, 0));
            runnableMap.put(24, () -> openDrop(user, 1));
        } else user.sendMessage("Error " + categoryID);

        cachedInventory.whenClick(event -> {
            event.setBlockClick(true);
            if(event.getSlot() == 19){
                if(event.getClickType().equals(ClickType.SHIFT_LEFT)){
                    openDropList(user);
                    return;
                }
                setCategoriesByID(cachedInventory, id, -1);
            } else if(event.getSlot() == 25){
                if(event.getClickType().equals(ClickType.SHIFT_LEFT)){
                    openDropList(user);
                    return;
                }
                setCategoriesByID(cachedInventory, id, 1);
            }
            runnableMap.forEach((slot, runnable) -> {
                if(event.getSlot() == slot) runnable.run();
            });
        });

    }

    private void openDropList(User user){
        CachedInventory cachedInventory = new CachedInventory(user, Bukkit.createInventory(null, 3*9, "Dropy"));
        cachedInventory.setItemInSlots(new int[]{1, 3, 5, 7, 9, 15, 19, 21, 23, 25}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 7).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
        cachedInventory.setItemInSlots(new int[]{0, 2, 4, 6, 8, 18, 20, 22, 24, 26}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
        cachedInventory.setItem(17, new ItemHelper(Material.STAINED_GLASS_PANE).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));

        cachedInventory.setItem(10, new ItemHelper(Material.STONE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Drop z &fKamien"))));
        cachedInventory.setItem(11, new ItemHelper(Material.MOSSY_COBBLESTONE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Drop z &a&lCobble&f&lX"))));
        cachedInventory.setItem(12, new ItemHelper(Material.CHEST).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Drop z &e&lPremium&6&lCase"))));
        cachedInventory.setItem(13, new ItemHelper(Material.LEAVES).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Drop z &aLiscie"))));
        cachedInventory.setItem(14, new ItemHelper(Material.DRAGON_EGG).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Drop z &dPierozek"))));

        cachedInventory.setItem(16, new ItemHelper(Material.FENCE_GATE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&7Powrot do menu glownego"))));

        cachedInventory.whenClick(event -> {
            event.setBlockClick(true);
            if(event.getSlot() == 16){
                openMenu(user, 0);
            } else if(event.getSlot() == 14){

            }
        });

        cachedInventory.open();
    }

    private void openDrop(User user, int category){
        if(category == 0) {

        }
        else if(category == 1) user.sendMessage("stone");
        else if(category == 2) user.sendMessage("cobblex");
        else if(category == 3) user.sendMessage("leaves");
        else if(category == 4) user.sendMessage("pierozek");
        else if(category == 5){

        }
    }
}
