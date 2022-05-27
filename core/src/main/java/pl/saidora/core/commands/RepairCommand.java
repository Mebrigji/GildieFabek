package pl.saidora.core.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.helpers.MaterialHelper;
import pl.saidora.core.helpers.MessageHolder;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "repair", usage = "/repair [all(optional)]", permission = "sacore.repair", aliases = {"napraw", "rep"})
public class RepairCommand implements Command {
    public RepairCommand() {
    }

    @Override
    public void run(Executor executor) {
        executor.asPlayer().ifPresent(player -> {
            String[] args = executor.getCommandArguments();
            if(args.length == 0){
                ItemStack itemStack = player.getItemInHand();
                if(!MaterialHelper.isTool(itemStack)){
                    executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_REPAIR_MUST_BE_ITEM);
                    return;
                }
                itemStack.setDurability((short) 0);
                player.setItemInHand(itemStack);
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPAIR_ONE)
                        .with("item",
                                MessageHolder.create(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ?
                                        itemStack.getItemMeta().getDisplayName() : itemStack.getType().name(), itemStack))
                        .send();
            } else if(args[0].equalsIgnoreCase("all") && executor.hasPermission("sacore.repair.all")){
                List<ItemStack> itemStacks = getItems(player);
                if(itemStacks.isEmpty()){
                    executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_REPAIR_ALL_EMPTY);
                    return;
                }
                itemStacks = new ArrayList<>();
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if(MaterialHelper.isTool(itemStack)){
                        itemStack.setDurability((short) 0);
                        player.getInventory().setItem(i, itemStack);
                        itemStacks.add(itemStack);
                    }
                }
                ItemStack helmet = repair(player, EquipmentSlot.HEAD), chest = repair(player, EquipmentSlot.CHEST), legs = repair(player, EquipmentSlot.LEGS), boots = repair(player, EquipmentSlot.FEET);
                if(helmet != null) itemStacks.add(helmet);
                if(chest != null) itemStacks.add(chest);
                if(legs != null) itemStacks.add(legs);
                if(boots != null) itemStacks.add(boots);

                List<ItemStack> finalItemStacks = itemStacks;

                MessageBuilder messageBuilder = executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPAIR_ALL)
                        .with("itemsCount", itemStacks.size())
                        .withPreparingComponent("itemsList", component -> {
                            component.setText("");
                            finalItemStacks.forEach(itemStack -> {
                                TextComponent hover = new TextComponent();
                                MessageHolder holder = MessageHolder.create(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name(), itemStack);
                                hover.setText(ColorHelper.translateColors(holder.text()));
                                hover.setHoverEvent(holder.component().getHoverEvent());
                                if(component.getExtra() != null) component.addExtra(", ");
                                component.addExtra(hover);
                            });
                        });

                messageBuilder.send();
            } else {
                ItemStack itemStack = player.getItemInHand();
                if(!MaterialHelper.isTool(itemStack)){
                    executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_REPAIR_MUST_BE_ITEM);
                    return;
                }
                itemStack.setDurability((short) 0);
                player.setItemInHand(itemStack);
                executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_REPAIR_ONE)
                        .with("item",
                                MessageHolder.create(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ?
                                        itemStack.getItemMeta().getDisplayName() : itemStack.getType().name(), itemStack))
                        .send();
            }
        });
    }

    private ItemStack repair(Player player, EquipmentSlot slotType){
        ItemStack itemStack = null;
        if(slotType.equals(EquipmentSlot.HEAD)){
            itemStack = player.getEquipment().getHelmet();
            if(itemStack != null && MaterialHelper.isTool(itemStack)){
                itemStack.setDurability((short) 0);
                player.getEquipment().setHelmet(itemStack);
            }
        } else if(slotType.equals(EquipmentSlot.CHEST)){
            itemStack = player.getEquipment().getChestplate();
            if(itemStack != null && MaterialHelper.isTool(itemStack)){
                itemStack.setDurability((short) 0);
                player.getEquipment().setChestplate(itemStack);
            }
        } else if(slotType.equals(EquipmentSlot.LEGS)){
            itemStack = player.getEquipment().getLeggings();
            if(itemStack != null && MaterialHelper.isTool(itemStack)){
                itemStack.setDurability((short) 0);
                player.getEquipment().setLeggings(itemStack);
            }
        } else if (slotType.equals(EquipmentSlot.FEET)){
            itemStack = player.getEquipment().getBoots();
            if(itemStack != null && MaterialHelper.isTool(itemStack)){
                itemStack.setDurability((short) 0);
                player.getEquipment().setBoots(itemStack);
            }
        } else if(slotType.equals(EquipmentSlot.HAND)){
            itemStack = player.getEquipment().getItemInHand();
            if(itemStack != null && MaterialHelper.isTool(itemStack)){
                itemStack.setDurability((short) 0);
                player.getEquipment().setItemInHand(itemStack);
            }
        }
        return itemStack;
    }

    private List<ItemStack> getItems(Player player){
        List<ItemStack> itemStacks = new ArrayList<>();

        for (ItemStack content : player.getInventory().getContents()) {
            if(content == null || content.getType().equals(Material.AIR)) break;
            if(MaterialHelper.isTool(content)) itemStacks.add(content);
        }

        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            if(armorContent == null || armorContent.getType().equals(Material.AIR)) break;
            if(MaterialHelper.isTool(armorContent)) itemStacks.add(armorContent);
        }

        return itemStacks;
    }
}
