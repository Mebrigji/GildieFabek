package pl.saidora.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.model.impl.CachedInventory;
import pl.saidora.core.model.impl.User;

import java.util.Arrays;

@CommandInfo(name = "vanish", usage = "/vanish [on, off, check, panel] [player(optional)]", permission = "sacore.vanish", aliases = "v")
public class VanishCommand implements Command {

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0){
            executor.asPlayer().ifPresentOrElse(player -> {
                User user = Main.getInstance().getOnlineUsers().get(player.getName());
                user.setVanish(!user.isVanish());
                user.sendMessage(user.isVanish() ? Main.getInstance().getConfiguration().COMMAND_VANISH_ENABLE : Main.getInstance().getConfiguration().COMMAND_VANISH_DISABLE);
                user.updateVisibility();
            }, () -> executor.sendMessage(usage()));
        } else if(args.length == 1 && executor.isPlayer()){
            User user = Main.getInstance().getOnlineUsers().get(executor.getName());
            if(args[0].equalsIgnoreCase("on")){
                user.setVanish(true);
                user.updateVisibility();
                user.sendMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_ENABLE);
            } else if(args[0].equalsIgnoreCase("off")){
                user.setVanish(false);
                user.updateVisibility();
                user.sendMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_DISABLE);
            } else if(args[0].equalsIgnoreCase("check")) {
                user.sendMessage(user.isVanish() ? Main.getInstance().getConfiguration().COMMAND_VANISH_CHECK_ENABLED : Main.getInstance().getConfiguration().COMMAND_VANiSH_CHECK_DISABLED);
            } else if(args[0].equalsIgnoreCase("panel")){
                openPanel(user);
            } else user.sendMessage(usage());
        } else if(args.length == 2){
            Main.getInstance().getUserCache().findByName(args[1], true).ifPresentOrElse(user -> {
                if(args[0].equalsIgnoreCase("on")){
                    user.setVanish(true);
                    user.updateVisibility();
                    user.sendMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_ENABLE);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_ENABLE_EXECUTOR).with("playerName", user.getName()).send();
                } else if(args[0].equalsIgnoreCase("off")){
                    user.setVanish(false);
                    user.updateVisibility();
                    user.sendMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_DISABLE);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_DISABLE_EXECUTOR).with("playerName", user.getName()).send();
                } else if(args[0].equalsIgnoreCase("check")){
                    executor.sendMessage(user.isVanish() ? Main.getInstance().getConfiguration().COMMAND_VANISH_CHECK_ENABLED_EXECUTOR : Main.getInstance().getConfiguration().COMMAND_VANISH_CHECK_DISABLED_EXECUTOR);
                } else executor.sendMessage(usage());
            }, () -> executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_VANISH_OFFLINE));
        } else executor.sendMessage(usage());
    }

    private void openPanel(User user){
        CachedInventory cachedInventory = new CachedInventory(user, Bukkit.createInventory(null, 5*9, "Panel"));

        cachedInventory.setItemInSlots(new int[]{1, 3, 5, 7, 9, 11, 15, 17, 27, 35, 37, 39, 41, 43}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
        cachedInventory.setItemInSlots(new int[]{0, 2, 4, 6, 8, 10, 16, 18, 26, 28, 34, 36, 38, 40, 42, 44}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 7).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));

        cachedInventory.setItemInSlots(new int[]{12, 14}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) (user.isVanish() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(" ");
        }));

        cachedInventory.setItem(13, new ItemHelper(Material.SIGN).editMeta(itemMeta -> {
            if(user.isVanish()){
                itemMeta.setDisplayName(ColorHelper.translateColors("&a[VANISH]"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Niewidzialnosc jest uruchomiona", " ", "&7Kliknij, w &fpusta butelke&7 aby &cwylaczyc")));
            } else {
                itemMeta.setDisplayName(ColorHelper.translateColors("&c[VANISH]"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Niewidzialnosc jest wylaczona", " ", "&7Kliknij, w &feliksir niewidzialnosci&7 aby &awlaczyc")));
            }
        }));

        cachedInventory.setItem(19, new ItemHelper(Material.GLASS_BOTTLE).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&cWylacz niewidzialnosc"));
        }));

        cachedInventory.setItem(25, new ItemHelper(Material.POTION, 1, (short) 8270).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&aWlacz niewidzialnosc"));
        }));

        cachedInventory.setItem(20, new ItemHelper(Material.DROPPER).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&dWyrzucanie przedmiotow"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Gdy ta opcja jest &awlaczona&7 to mozesz", "&f&nwyrzucac przedmioty")));
        }));

        cachedInventory.setItem(21, new ItemHelper(Material.HOPPER).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&dPodnoszenie przedmiotow"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Gdy ta opcja jest &awlaczone&7 to mozesz", "&f&npodnosic przedmioty")));
        }));

        cachedInventory.setItem(22, new ItemHelper(Material.GRASS).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&dBudowanie/Niszczenie"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Gdy ta opcja jest &awlaczona&7 to mozesz", "&f&nklasc, niszczyc bloki")));
        }));

        cachedInventory.setItem(23, new ItemHelper(Material.WORKBENCH).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&dInterakcja"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Gdy ta opcja jest &awlaczona&7 to mozesz", "&f&nwchodzic w interakcje z blokami")));
        }));

        cachedInventory.setItem(24, new ItemHelper(Material.DIAMOND_SWORD).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors("&dPVP"));
            itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Gdy ta opcja jest &awlaczona&7 to mozesz", "&f&nbic innych graczy")));
        }));

        cachedInventory.setItem(29, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_drop() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_drop() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));

        cachedInventory.setItem(30, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_pickup() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_pickup() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));

        cachedInventory.setItem(31, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_build() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_build() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));

        cachedInventory.setItem(32, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_interact() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_interact() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));

        cachedInventory.setItem(33, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_attack() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_attack() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));

        cachedInventory.whenClick(event -> {
            event.setBlockClick(true);
            if(event.getSlot() == 25){
                user.setVanish(true);
                user.updateVisibility();
                update(cachedInventory);
            } else if(event.getSlot() == 19){
                user.setVanish(false);
                user.updateVisibility();
                update(cachedInventory);
            } else if(event.getSlot() == 20){
                user.setV_drop(!user.isV_drop());
                update(cachedInventory);
            } else if(event.getSlot() == 21){
                user.setV_pickup(!user.isV_pickup());
                update(cachedInventory);
            } else if(event.getSlot() == 22){
                user.setV_build(!user.isV_build());
                update(cachedInventory);
            } else if(event.getSlot() == 23){
                user.setV_interact(!user.isV_interact());
                update(cachedInventory);
            } else if(event.getSlot() == 24){
                user.setV_attack(!user.isV_attack());
                update(cachedInventory);
            }
        });
        cachedInventory.open();
    }

    private void update(CachedInventory cachedInventory){
        User user = cachedInventory.getUser();

        cachedInventory.setItemInSlots(new int[]{12, 14}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) (user.isVanish() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(" ");
        }));

        cachedInventory.setItem(13, new ItemHelper(Material.SIGN).editMeta(itemMeta -> {
            if(user.isVanish()){
                itemMeta.setDisplayName(ColorHelper.translateColors("&a[VANISH]"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Niewidzialnosc jest uruchomiona", " ", "&7Kliknij, w &fpusta butelke&7 aby &cwylaczyc")));
            } else {
                itemMeta.setDisplayName(ColorHelper.translateColors("&c[VANISH]"));
                itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Niewidzialnosc jest wylaczona", " ", "&7Kliknij, w &feliksir niewidzialnosci&7 aby &awlaczyc")));
            }
        }));

        cachedInventory.setItem(29, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_drop() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_drop() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));
        cachedInventory.setItem(30, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_pickup() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_pickup() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));
        cachedInventory.setItem(31, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_build() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_build() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));
        cachedInventory.setItem(32, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_interact() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_interact() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));
        cachedInventory.setItem(33, new ItemHelper(Material.STAINED_GLASS, 1, (short) (user.isV_attack() ? 5 : 14)).editMeta(itemMeta -> {
            itemMeta.setDisplayName(ColorHelper.translateColors(user.isV_attack() ? "&aFunkcja odblokowana" : "&cFunkcja zablokowana"));
        }));
    }
}