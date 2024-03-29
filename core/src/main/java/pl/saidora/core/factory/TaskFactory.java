package pl.saidora.core.factory;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.model.impl.*;
import pl.saidora.core.model.Options;
import pl.saidora.core.model.PersistentDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class TaskFactory {

    public BukkitTask registerUserScheduler(PersistentDataService service) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(service.getPlugin(), () -> service.getOnlineUsers().forEach((name, user) -> {
            if(user.isVanish()){
                user.getActionbar().ifPresent(actionbar -> {
                    actionbar.addMessage("vanish-enabled", new TimedMessage(1500, u -> "&2[VANISH] &aPrzypominajka"));
                    actionbar.send();
                });
            }
            user.getTabList().ifPresent(TabList::update);
            user.getScoreboard().ifPresent(ScoreBoard::send);
        }), 0, 5);
    }

    public BukkitTask registerSortScheduler(PersistentDataService service) {
        return Bukkit.getScheduler().runTaskTimer(service.getPlugin(), () -> service.getLeaderboardCache().getCache().forEach(((aClass, leaderboard) ->
                leaderboard.sort()
        )), 0, 200);
    }

    public BukkitTask registerGeneratorScheduler(PersistentDataService service) {
        return Bukkit.getScheduler().runTaskTimer(service.getPlugin(), () -> {
            new HashMap<>(service.getGeneratorsToRegen()).forEach(((location, generator) -> {
                generator.setCountdown(generator.getCountdown() - 1);
                if (generator.getCountdown() <= 0) {
                    service.getGeneratorsToRegen().remove(location);
                    generator.setCountdown(generator.getDelay());
                    location.getBlock().setType(generator.getGenerate());
                }
            }));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.setNoDamageTicks(0);
                onlinePlayer.setMaximumNoDamageTicks(0);
            }
        }, 0, 1);
    }

    public BukkitTask registerAbyssScheduler(PersistentDataService service) {
        return Bukkit.getScheduler().runTaskTimer(service.getPlugin(), () -> {

            long t = service._abyss_getCountDown();

            String message = service._abyss_getMessages().get(t);
            MessageBuilder builder = new MessageBuilder((Player) null, message);


            if(t == 0){
                service.getAbyssCache().getAbyssMap().clear();
                t = -service.getConfiguration().ABYSS_OPEN_TIME;

                AtomicLong items = new AtomicLong();

                List<ItemStack> toInv = new ArrayList<>();

                Bukkit.getWorlds().forEach(world ->
                        world.getEntities().stream()
                                .filter(entity -> entity.getType().equals(EntityType.DROPPED_ITEM))
                                .forEach(entity -> {
                                    Item item = (Item) entity;
                                    item.remove();

                                    ItemStack stack = item.getItemStack();

                                    items.set(items.get() + stack.getAmount());
                                    toInv.add(stack);
                                }));

                service.getAbyssCache().addItem(toInv.toArray(new ItemStack[]{}));

                builder.with("items", items.get());
                service.getOnlineUsers().forEach((name, user) -> {
                    if(user.isEnabled(Abyss.class)) {
                        builder.setExecutor(user.getCommandSender());
                        builder.send();
                    }
                });
            } else if(service.getAbyssCache().isOpened() && !service.getAbyssCache().isOpened(t - 1)) {
                service.getAbyssCache().getAbyssMap().forEach((id, abyss) ->
                        new ArrayList<>(abyss.getInventory().getViewers()).forEach(HumanEntity::closeInventory));

                builder.setMessage(service._abyss_getMessages().get(-1L));

                t = service.getConfiguration().ABYSS_COUNTDOWN+1;

                service.getOnlineUsers().forEach((name, user) -> {
                    if(((Options) user).isEnabled(Abyss.class)) {
                        builder.setExecutor(user.getCommandSender());
                        builder.send();
                    }
                });
            } else {

                service.getOnlineUsers().forEach((name, user) -> {
                    if (user.isEnabled(Abyss.class)) {
                        builder.setExecutor(user.getCommandSender());
                        builder.send();
                    }
                });
            }

            service._abyss_setCountDown(t - 1);

        }, 0, 20);
    }
}
