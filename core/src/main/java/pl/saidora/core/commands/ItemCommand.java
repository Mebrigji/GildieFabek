package pl.saidora.core.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.saidora.api.functions.CompleteFuture;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.helpers.MessageHolder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommandInfo(name = "item", usage = "/item name [name/clear] - set item name\n/item lore [lore(newLine=\":nl\")/clear]", permission = "sacore.info", executors = ExecutorType.PLAYER)
public class ItemCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        Player player = (Player) executor.getCommandSender();

        if (args.length <= 1) {
            executor.sendMessage(commandInfo().usage());
        } else if (args[0].equalsIgnoreCase("name")) {
            ItemHelper itemHelper = new ItemHelper(player.getItemInHand());
            if (itemHelper.getType().equals(Material.AIR)) {
                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_ITEM_AIR);
                return;
            }
            if (args[1].equals("clear")) {
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ITEM_NAME_CLEAR)
                        .withPreparingComponent("item-changes", textComponent -> {
                            textComponent.setText("");

                            TextComponent extra = new TextComponent("");
                            ItemHelper helper = new ItemHelper(itemHelper);

                            extra.setText(ColorHelper.translateColors("&c[" + (helper.hasItemMeta() && helper.getItemMeta().hasDisplayName() ? helper.getItemMeta().getDisplayName() : helper.getType().name()) + "&c]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());

                            textComponent.addExtra(extra);

                            itemHelper.editMeta(itemMeta -> itemMeta.setDisplayName(null));
                            player.setItemInHand(itemHelper);

                            textComponent.addExtra(ColorHelper.translateColors(" &8-> "));
                            extra = new TextComponent(ColorHelper.translateColors("&a[" + (itemHelper.hasItemMeta() && itemHelper.getItemMeta().hasDisplayName() ? itemHelper.getItemMeta().getDisplayName() : itemHelper.getType().name()) + "&a]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());
                            textComponent.addExtra(extra);
                        }).send();
            } else {
                String message = StringUtils.join(args, " ", 1, args.length);
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ITEM_NAME_SET)
                        .withPreparingComponent("item-changes", textComponent -> {
                            textComponent.setText("");

                            TextComponent extra = new TextComponent("");
                            ItemHelper helper = new ItemHelper(itemHelper);

                            extra.setText(ColorHelper.translateColors("&c[" + (helper.hasItemMeta() && helper.getItemMeta().hasDisplayName() ? helper.getItemMeta().getDisplayName() : helper.getType().name()) + "&c]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());

                            textComponent.addExtra(extra);

                            itemHelper.editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors(message)));
                            player.setItemInHand(itemHelper);

                            textComponent.addExtra(ColorHelper.translateColors(" &8-> "));
                            extra = new TextComponent(ColorHelper.translateColors("&a[" + (itemHelper.hasItemMeta() && itemHelper.getItemMeta().hasDisplayName() ? itemHelper.getItemMeta().getDisplayName() : itemHelper.getType().name()) + "&a]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());
                            textComponent.addExtra(extra);
                        }).send();
            }
        } else if (args[0].equalsIgnoreCase("lore")) {
            ItemHelper itemHelper = new ItemHelper(player.getItemInHand());
            if (itemHelper.getType().equals(Material.AIR)) {
                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_ITEM_AIR);
                return;
            }
            if (args[1].equals("clear")) {
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ITEM_LORE_CLEAR)
                        .withPreparingComponent("item-changes", textComponent -> {
                            textComponent.setText("");

                            TextComponent extra = new TextComponent("");
                            ItemHelper helper = new ItemHelper(itemHelper);

                            extra.setText(ColorHelper.translateColors("&c[" + (helper.hasItemMeta() && helper.getItemMeta().hasDisplayName() ? helper.getItemMeta().getDisplayName() : helper.getType().name()) + "&c]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());

                            textComponent.addExtra(extra);

                            itemHelper.editMeta(itemMeta -> itemMeta.setLore(new ArrayList<>()));
                            player.setItemInHand(itemHelper);

                            textComponent.addExtra(ColorHelper.translateColors(" &8-> "));
                            extra = new TextComponent(ColorHelper.translateColors("&a[" + (itemHelper.hasItemMeta() && itemHelper.getItemMeta().hasDisplayName() ? itemHelper.getItemMeta().getDisplayName() : itemHelper.getType().name()) + "&a]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());
                            textComponent.addExtra(extra);
                        }).send();
            } else {
                List<String> lore = Arrays.asList(StringUtils.join(args, " ", 1, args.length).split(":nl"));
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ITEM_LORE_SET)
                        .withPreparingComponent("item-changes", textComponent -> {
                            textComponent.setText("");

                            TextComponent extra = new TextComponent("");
                            ItemHelper helper = new ItemHelper(itemHelper);

                            extra.setText(ColorHelper.translateColors("&c[" + (helper.hasItemMeta() && helper.getItemMeta().hasDisplayName() ? helper.getItemMeta().getDisplayName() : helper.getType().name()) + "&c]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());

                            textComponent.addExtra(extra);

                            itemHelper.editMeta(itemMeta -> itemMeta.setLore(ColorHelper.translateColors(lore)));
                            player.setItemInHand(itemHelper);

                            textComponent.addExtra(ColorHelper.translateColors(" &8-> "));
                            extra = new TextComponent(ColorHelper.translateColors("&a[" + (itemHelper.hasItemMeta() && itemHelper.getItemMeta().hasDisplayName() ? itemHelper.getItemMeta().getDisplayName() : itemHelper.getType().name()) + "&a]"));
                            extra.setHoverEvent(itemHelper.asHoverEvent());
                            textComponent.addExtra(extra);
                        }).send();
            }
        } else executor.sendMessage(commandInfo().usage());
    }
}
