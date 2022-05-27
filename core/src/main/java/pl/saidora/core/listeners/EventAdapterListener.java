package pl.saidora.core.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.events.*;
import pl.saidora.core.events.guild.GuildRegionBlockBreakEvent;
import pl.saidora.core.events.guild.GuildRegionBlockPlaceEvent;
import pl.saidora.core.helpers.MaterialHelper;
import pl.saidora.core.model.GuildPermission;
import pl.saidora.core.helpers.MessageHolder;
import pl.saidora.core.model.impl.TimedMessage;
import pl.saidora.core.model.impl.User;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class EventAdapterListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){

            User user = Main.getInstance().getUserCache().get(User::new, event.getPlayer());
            user.setPlayer(event.getPlayer());

            new UserJoinEvent() {
                @Override
                public User getUser() {
                    return user;
                }

                @Override
                public PlayerJoinEvent getBukkitJoinEvent() {
                    return event;
                }
            }.call();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event){
        User user = Main.getInstance().getUserCache().get(User::new, event.getPlayer());
        new UserQuitEvent() {
            @Override
            public User getUser() {
                return user;
            }

            @Override
            public PlayerQuitEvent getBukkitQuitEvent() {
                return event;
            }
        }.call();
        user.setPlayer(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event){
        Main.getInstance().getUserCache().lookByName(event.getPlayer().getName(), true).ifPresent(user -> {

            UserChatEvent userChatEvent = new UserChatEvent(user, event, () -> Collections.singletonList(MessageHolder.create(user.getName() + ": " + event.getMessage())));
            userChatEvent.call();

            if(userChatEvent.isCancelled()) return;

            TextComponent component = userChatEvent.getMessage().build();

            Bukkit.getConsoleSender().sendMessage(ColorHelper.translateColors(userChatEvent.getMessage().message().stream().map(MessageHolder::text).collect(Collectors.joining(" "))));

            Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(component));

        });

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event){
        Main.getInstance().getUserCache().lookByName(event.getWhoClicked().getName(), true).ifPresent(user -> {
            user.getInventoryHolder().ifPresent(inventoryHolder -> {
                if(!event.getInventory().equals(inventoryHolder.getInventory())){
                    user.setInventoryHolder(null);
                    return;
                }

                UserInventoryClickEvent userInventoryClickEvent = new UserInventoryClickEvent(inventoryHolder, event.getSlot());
                inventoryHolder.onClick(userInventoryClickEvent);

                if(userInventoryClickEvent.isCancelled()) return;

                event.setCancelled(userInventoryClickEvent.isBlockClick());
            });
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        Main.getInstance().getUserCache().lookByName(event.getPlayer().getName(), true).ifPresent(user -> {
            UserCommandEvent commandEvent = new UserCommandEvent(user, event);
            commandEvent.call();

            event.setCancelled(commandEvent.isCancelled());
            event.setMessage(commandEvent.getCommandLine());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event){

        event.setCancelled(true);

        Main.getInstance().getUserCache().lookByName(event.getPlayer().getName(), true).ifPresent(user -> {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);

            Main.getInstance().getGuildCache().getGuildByLocation(event.getBlock().getLocation()).ifPresent(guild -> {
                GuildRegionBlockBreakEvent blockBreakEvent = new GuildRegionBlockBreakEvent(guild, user, event.getBlock(), GuildPermission.BLOCK_BREAK::hasPermission);
                blockBreakEvent.call();
                atomicBoolean.set(blockBreakEvent.isCancelled());
            });

            UserBlockBreakEvent userBlockBreakEvent = new UserBlockBreakEvent(user, event.getBlock());
            userBlockBreakEvent.call();
            atomicBoolean.set(userBlockBreakEvent.isCancelled());

            if(atomicBoolean.get()) return;

            Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getItemInHand());

            event.getBlock().setType(Material.AIR, false);

            event.getPlayer().giveExp(event.getExpToDrop());
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 0.1f, 0.2f);

            if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

            MaterialHelper.recalculateDurability(event.getPlayer());

            if(userBlockBreakEvent.isDropItems()) user.giveItem(drops);
        });

    }

    private final static DecimalFormat hearthFormat = new DecimalFormat("##.##");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Main.getInstance().getUserCache().lookByName(event.getEntity().getName(), true).ifPresent(user -> {
                user.getAntyLogout().ifPresent(antyLogout -> {
                    if (event.getDamager() instanceof Arrow) {
                        Arrow arrow = (Arrow) event.getDamager();
                        if (arrow.getShooter() instanceof Player) {
                            antyLogout.setEnd(Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME * 1000);
                            user.getActionbar().ifPresent(actionbar -> actionbar.addMessage("combat", new TimedMessage((antyLogout.getEnd() - System.currentTimeMillis()) + 1000, u -> {
                                double percent = (((antyLogout.getEnd() - System.currentTimeMillis()) / 1000.0) * 100.0) / Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME;
                                if (percent <= 0) {
                                    return Main.getInstance().getConfiguration().EVENT_COMBAT_END;
                                } else if (percent > 99.999)
                                    user.sendMessage(Main.getInstance().getConfiguration().EVENT_COMBAT_START.replace("%attackerName%", (((LivingEntity) arrow.getShooter()).getName())));
                                return String.format(Main.getInstance().getConfiguration().ANTY_LOGOUT_MESSAGE,
                                        getColor(percent, 100),
                                        getColor(percent, 90),
                                        getColor(percent, 80),
                                        getColor(percent, 70),
                                        getColor(percent, 60),
                                        getColor(percent, 50),
                                        getColor(percent, 40),
                                        getColor(percent, 30),
                                        getColor(percent, 20),
                                        getColor(percent, 10));
                            })));
                            Main.getInstance().getUserCache().lookByName(((Player) arrow.getShooter()).getName(), true).ifPresent(attacker -> antyLogout.damage(user, event.getDamage()));

                            new MessageBuilder(user, Main.getInstance().getConfiguration().EVENT_COMBAT_ARROW)
                                    .with("victimName", user.getName())
                                    .with("health", hearthFormat.format((((Player) event.getEntity()).getHealth() / 2)))
                                    .send();
                        }
                    } else if (event.getDamager() instanceof Snowball) {
                        Snowball snowball = (Snowball) event.getDamager();
                        if (snowball.getShooter() instanceof Player) {

                            antyLogout.setEnd(Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME * 1000);
                            user.getActionbar().ifPresent(actionbar -> actionbar.addMessage("combat", new TimedMessage((antyLogout.getEnd() - System.currentTimeMillis()) + 1000, u -> {
                                double percent = (((antyLogout.getEnd() - System.currentTimeMillis()) / 1000.0) * 100.0) / Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME;

                                if (percent <= 0) return Main.getInstance().getConfiguration().EVENT_COMBAT_END;
                                else if (percent > 99.999)
                                    user.sendMessage(Main.getInstance().getConfiguration().EVENT_COMBAT_START.replace("%attackerName%", (((LivingEntity) snowball.getShooter()).getName())));

                                return String.format(Main.getInstance().getConfiguration().ANTY_LOGOUT_MESSAGE,
                                        getColor(percent, 100),
                                        getColor(percent, 90),
                                        getColor(percent, 80),
                                        getColor(percent, 70),
                                        getColor(percent, 60),
                                        getColor(percent, 50),
                                        getColor(percent, 40),
                                        getColor(percent, 30),
                                        getColor(percent, 20),
                                        getColor(percent, 10));
                            })));
                            Main.getInstance().getUserCache().lookByName(((Player) snowball.getShooter()).getName(), true).ifPresent(attacker -> antyLogout.damage(user, event.getDamage()));

                            new MessageBuilder(user, Main.getInstance().getConfiguration().EVENT_COMBAT_SNOWBALL)
                                    .with("victimName", user.getName())
                                    .with("health", hearthFormat.format((((Player) event.getEntity()).getHealth() / 2)))
                                    .send();
                        }
                    } else if (event.getDamager() instanceof Player) {
                        Player player = (Player) event.getDamager();
                        Main.getInstance().getUserCache().lookByName(player.getName(), true).ifPresent(attacker -> {

                            UserCombatEvent combatEvent = new UserCombatEvent(user, attacker, event.getDamage());
                            combatEvent.call();
                            event.setDamage(combatEvent.getDamage());

                            attacker.getAntyLogout().ifPresent(logout -> logout.setEnd(Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME));
                            if (antyLogout.getEnd() < System.currentTimeMillis())
                                user.prepareMessage(Main.getInstance().getConfiguration().EVENT_COMBAT_START).with("attackerName", attacker.getName()).send();
                            antyLogout.setEnd(Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME);
                            attacker.getActionbar().ifPresent(actionbar -> actionbar.addMessage("combat", new TimedMessage((antyLogout.getEnd() - System.currentTimeMillis()) + 1000, u -> {
                                double percent = (((antyLogout.getEnd() - System.currentTimeMillis()) / 1000.0) * 100.0) / Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME;
                                if (percent <= 0) return Main.getInstance().getConfiguration().EVENT_COMBAT_END;
                                return String.format(Main.getInstance().getConfiguration().ANTY_LOGOUT_MESSAGE,
                                        getColor(percent, 100),
                                        getColor(percent, 90),
                                        getColor(percent, 80),
                                        getColor(percent, 70),
                                        getColor(percent, 60),
                                        getColor(percent, 50),
                                        getColor(percent, 40),
                                        getColor(percent, 30),
                                        getColor(percent, 20),
                                        getColor(percent, 10));
                            })));
                        });
                    }
                });
            });
        } else {
            Player player = getAttacker(event.getDamager());
            if(player == null) return;
            Main.getInstance().getUserCache().lookByName(player.getName(), true).ifPresent(attacker -> {
                attacker.getAntyLogout().ifPresent(logout -> logout.setEnd(Main.getInstance().getConfiguration().ANTY_LOGOUT_TIME));
                attacker.getScoreboard()
                        .ifPresent(scoreBoard -> scoreBoard.addTimedMessage("victim-hp",
                                new TimedMessage(5000, u -> "&7HP ofiary: &c" + (((LivingEntity)event.getEntity()).getHealth() / 2.0))));
            });
        }
    }

    private Player getAttacker(Entity entity){
        if(entity instanceof Player) return (Player) entity;
        if(entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            if(projectile.getShooter() instanceof Player)
                return (Player) projectile.getShooter();
        }
        return null;
    }

    private String getColor(double value, double percent){
        return value > percent ? "&c" : "&a";
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event){
        Main.getInstance().getUserCache().lookByName(event.getPlayer().getName(), true).ifPresent(user -> {
            Main.getInstance().getGuildCache().getGuildByLocation(event.getBlock().getLocation()).ifPresent(guild -> {
                GuildRegionBlockPlaceEvent blockPlaceEvent = new GuildRegionBlockPlaceEvent(guild, user, event.getBlock(), GuildPermission.BLOCK_PLACE::hasPermission);
                blockPlaceEvent.setCancelled(event.isCancelled());
                blockPlaceEvent.call();
                event.setCancelled(blockPlaceEvent.isCancelled());
            });
            UserBlockPlaceEvent userBlockPlaceEvent = new UserBlockPlaceEvent(user, event.getBlock());
            userBlockPlaceEvent.setCancelled(event.isCancelled());
            userBlockPlaceEvent.call();
            event.setCancelled(userBlockPlaceEvent.isCancelled());
            Main.getInstance().getGeneratorCache().placeGenerator(event.getBlock().getLocation(), user, event.getPlayer().getItemInHand());
        });
    }
}
