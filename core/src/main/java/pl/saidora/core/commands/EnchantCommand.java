package pl.saidora.core.commands;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.helpers.StringHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(name = "enchant", usage = "/enchant [enchant] [level]", permission = "sacore.enchant", executors = ExecutorType.PLAYER)
public class EnchantCommand implements Command {

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 2){
            int level;
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_ENCHANT_LEVEL);
                return;
            }

            Enchantment enchantment = getEnchantment(args[0]);
            if(enchantment == null){
                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_ENCHANT_INVALID);
                return;
            }

            Player player = (Player) executor.getCommandSender();
            ItemStack itemStack = player.getItemInHand();
            if(itemStack == null || itemStack.getType().equals(Material.AIR)){
                executor.sendMessage(Main.getInstance().getConfiguration().COMMAND_ENCHANT_AIR);
                return;
            }

            if(itemStack.getItemMeta().hasEnchant(enchantment)){
                if(level == 0){
                    itemStack.removeEnchantment(enchantment);
                    executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ENCHANT_REMOVE).with("enchantName", enchantment.getName()).send();
                } else executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ENCHANT_UPDATE).with("enchantName", enchantment.getName()).with("level", level).send();
            } else executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_ENCHANT_ADD).with("enchantName", enchantment.getName()).with("level", level).send();

            itemStack.addUnsafeEnchantment(enchantment, level);
            player.updateInventory();
        } else executor.sendMessage(commandInfo().usage());
    }

    @Override
    public List<String> tabComplete(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 1) return StringHelper.startWith(Arrays.stream(Enchantment.values()).map(Enchantment::getName).collect(Collectors.toList()), args[0], false, -1);
        return Command.super.tabComplete(executor);
    }

    private Enchantment getEnchantment(String enchant){
        Enchantment enchantment = Enchantment.getByName(enchant);
        if(enchantment == null){
            Integer id = getInteger(enchant);
            if(id != null) enchantment = Enchantment.getById(id);
        }
        return enchantment;
    }

    private Integer getInteger(String id){
        try {
            return Integer.getInteger(id);
        } catch (NumberFormatException e){
            return null;
        }
    }
}
