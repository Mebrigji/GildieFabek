package pl.saidora.core.model.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import pl.saidora.core.Main;
import pl.saidora.core.configuration.Configuration;

import java.util.function.Function;

public class Teleport {

    private final User user;

    private long delay;

    private Location from;

    private BukkitTask bukkitTask;

    public Teleport(User user){
        this.user = user;
    }

    public boolean isRunning() {
        return bukkitTask != null;
    }

    public void teleport(Location to, Function<User, Long> delayFunction){
        user.asPlayer().ifPresent(player -> {

            if(this.delay != 0 && bukkitTask != null){
                user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_SCHEDULER_END_NEW);
                user.send();
                bukkitTask.cancel();
            } else {
                user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_SCHEDULER_START);
                user.send();
            }

            this.delay = delayFunction.apply(user) * 4;
            this.from = player.getLocation();
            this.bukkitTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
                Location playerLocation = player.getLocation();
                delay--;
                if(delay <= 0){
                    user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_SCHEDULER_END);
                    user.send();
                    player.teleport(to);

                    bukkitTask.cancel();
                    bukkitTask = null;
                    return;
                }
                if(from.getX() != playerLocation.getX() || from.getY() != playerLocation.getY() || from.getZ() != playerLocation.getZ()){
                    user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_SCHEDULER_MOVE);
                    user.send();
                    bukkitTask.cancel();
                    bukkitTask = null;
                    return;
                }
                if((delay) % 4 == 0) {
                    user.addMessage(Main.getInstance().getConfiguration().MESSAGE_TELEPORT_SCHEDULER_TIMER.replace("%count%", String.valueOf(delay / 4)));
                    user.send();
                }
            }, 0, 5);
        });
    }

    public User getUser() {
        return user;
    }

    public long getDelay() {
        return delay;
    }

    public Location getFrom() {
        return from;
    }

    public BukkitTask getBukkitTask() {
        return bukkitTask;
    }
}
