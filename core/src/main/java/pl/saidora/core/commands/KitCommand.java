package pl.saidora.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.TimeHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.model.impl.CachedInventory;
import pl.saidora.core.model.impl.Kit;
import pl.saidora.core.model.impl.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandInfo(name = "kit", usage = "/kit [list/kitName]", permission = "", aliases = {"zestaw", "zestawy", "kits"}, executors = ExecutorType.PLAYER)
public class KitCommand implements Command {

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0) {
            User user = Main.getInstance().getOnlineUsers().get(executor.getName());

            CachedInventory cachedInventory = new CachedInventory(user, Main.getInstance().getKitCache().MENU);

            Map<Integer, Kit> preparedSlots = new HashMap<>();

            for (Kit kit : Main.getInstance().getKitCache().getCache()) {
                if(!Main.getInstance().getConfiguration().KIT_GUI_ADD_WHEN_NO_PERM && !user.hasPermission(kit.getPermissions())) continue;
                preparedSlots.put(kit.getSlot(), kit);
                cachedInventory.setItem(kit.getSlot(), new ItemHelper(kit.getMenuItem()).editMeta(itemMeta -> {
                        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                        Main.getInstance().getConfiguration().KIT_GUI_ITEM_KIT_LORE.forEach(line -> lore.add(ColorHelper.translateColors(line
                                .replace("%have-access%", executor.hasPermission(kit.getPermissions()) ? "Tak" : "Nie")
                                .replace("%can-claim%", user.canClaim(kit) ? "Tak" : "Nie, poczekaj " + new TimeHelper(user.getKitDelay(kit)).secondsToString()))));
                        itemMeta.setLore(lore);
                    }));
            }

            cachedInventory.whenClick(event -> {
                event.setBlockClick(true);
                Kit kit = preparedSlots.get(event.getSlot());

                if (kit == null) return;

                if(event.getClickType().isRightClick()){
                    user.execute("kit preview " + kit.getId());
                    return;
                }

                if (!user.hasPermission(kit.getPermissions())) {
                    executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_NO_PERM)
                            .with("permission", kit.getPermissions()).send();
                    return;
                }

                if (!user.canClaim(kit)) {
                    executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_ALREADY_CLAIM)
                            .with("time", new TimeHelper(user.getKitDelay(kit)).secondsToString()).send();
                    return;
                }

                CachedInventory inventory = new CachedInventory(user, user.claim(kit));
                inventory.open();

                executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_CLAIM)
                        .with("kitName", kit.getId()).with("time", new TimeHelper(System.currentTimeMillis() + kit.getDelay()).secondsToString()).send();
            });
            cachedInventory.open();
        } else if(args[0].equalsIgnoreCase("list")) {
            User user = Main.getInstance().getOnlineUsers().get(executor.getName());
            CachedInventory cachedInventory = new CachedInventory(user, Bukkit.createInventory(null, 3 * 9, ""));

            for (Kit kit : Main.getInstance().getKitCache().getCache()) {
                cachedInventory.addItem(kit.getMenuItem());
            }

            ItemHelper itemHelper = new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" "));

            cachedInventory.setItem(cachedInventory.getInventory().getSize() - 4, itemHelper);
            cachedInventory.setItem(cachedInventory.getInventory().getSize() - 5, new ItemHelper(Material.FENCE_GATE).editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&dPowrot do zestawow."))));
            cachedInventory.setItem(cachedInventory.getInventory().getSize() - 6, itemHelper);

            cachedInventory.whenClick(event -> {
                event.setBlockClick(true);
                if (event.getSlot() == 3 * 9 - 5) user.execute("kit");
            });

            cachedInventory.open();
        } else if(args[0].equalsIgnoreCase("preview") && args.length == 2){
            Main.getInstance().getKitCache().find(args[1]).ifPresentOrElse(kit -> {
                User user = Main.getInstance().getOnlineUsers().get(executor.getName());
                CachedInventory inventory = new CachedInventory(user, user.preview(kit));
                inventory.whenClick(event -> {
                    event.setBlockClick(true);
                    if(event.getSlot() == 5*9-5) user.execute("kit");
                });
                inventory.open();
            }, () -> executor.sendMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_DOESNT_EXISTS));
        } else {
            Main.getInstance().getKitCache().find(args[0]).ifPresentOrElse(kit -> {
                User user = Main.getInstance().getOnlineUsers().get(executor.getName());

                if (!user.hasPermission(kit.getPermissions())) {
                    executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_NO_PERM)
                            .with("permission", kit.getPermissions()).send();
                    return;
                }

                if (!user.canClaim(kit)) {
                    executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_ALREADY_CLAIM)
                            .with("time", new TimeHelper(user.getKitDelay(kit)).secondsToString()).send();
                    return;
                }

                CachedInventory inventory = new CachedInventory(user, user.claim(kit));
                inventory.open();

                executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_CLAIM)
                        .with("kitName", kit.getId()).with("time", new TimeHelper(System.currentTimeMillis() + kit.getDelay()).secondsToString()).send();
            }, () -> executor.prepareMessage(Main.getInstance().getConfiguration().KIT_MESSAGE_DOESNT_EXISTS).with("kitName", args[0]).send());
        }
    }
}
