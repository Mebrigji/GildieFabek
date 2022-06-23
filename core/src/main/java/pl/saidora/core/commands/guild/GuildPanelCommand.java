package pl.saidora.core.commands.guild;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.model.impl.CachedInventory;
import pl.saidora.core.model.impl.guild.Guild;

@CommandInfo(name = "panel", usage = "/g panel", permission = "")
public class GuildPanelCommand implements Command {
    @Override
    public void run(Executor executor) {
        Main.getInstance().getUserCache().findByName(executor.getName(), true).ifPresent(user -> {
            Guild guild = user.getGuild().orElse(null);
            if (guild == null) {
                executor.sendMessage(Main.getInstance().getConfiguration().GUILD_HAVE);
                return;
            }

            CachedInventory cachedInventory = new CachedInventory(user, Bukkit.createInventory(null, 5 * 9, "Panel"));
            cachedInventory.setItemInSlots(new int[]{1, 3, 4, 5, 7, 8, 9, 17, 18, 26, 27, 35, 37, 39, 40, 41, 43}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 2).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));
            cachedInventory.setItemInSlots(new int[]{0, 2, 6, 8, 36, 38, 42, 44}, new ItemHelper(Material.STAINED_GLASS_PANE, 1, (short) 7).editMeta(itemMeta -> itemMeta.setDisplayName(" ")));

            cachedInventory.setItem(13, new ItemHelper(Material.SKULL_ITEM, 1, (short) 3)
                    .editStack(itemHelper -> itemHelper.setSkullOwner(guild.getLeader().getName(), false))
                    .editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&5* &7Zarzadzanie czlonkami &5*"))));

            cachedInventory.whenClick(event -> event.setBlockClick(true));
            cachedInventory.open();
        });
    }
}
