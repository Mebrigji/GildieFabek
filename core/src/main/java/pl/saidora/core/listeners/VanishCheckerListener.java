package pl.saidora.core.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import pl.saidora.core.Main;
import pl.saidora.core.model.impl.TimedMessage;

public class VanishCheckerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPick(PlayerPickupItemEvent event){
        Main.getInstance().getUserCache().findByName(event.getPlayer().getName(), true).ifPresent(user -> {
            if(user.isVanish() && !user.isV_pickup()) {
                event.setCancelled(true);
                user.getActionbar().ifPresent(actionbar -> actionbar.addMessage("vanish", new TimedMessage(3000, u -> "&a[VANISH] &cZablokowano podnoszenie")));
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event){
        Main.getInstance().getUserCache().findByName(event.getPlayer().getName(), true).ifPresent(user -> {
            if(user.isVanish() && !user.isV_drop()) {
                event.setCancelled(true);
                user.getActionbar().ifPresent(actionbar -> actionbar.addMessage("vanish", new TimedMessage(3000, u -> "&a[VANISH] &cZablokowano wyrzucanie")));
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        if(!event.isCancelled() && event.getClickedBlock() != null){
            Main.getInstance().getUserCache().findByName(event.getPlayer().getName(), true).ifPresent(user -> {
                if(user.isVanish() && !user.isV_interact()) {
                    event.setCancelled(true);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setUseItemInHand(Event.Result.ALLOW);
                    user.getActionbar().ifPresent(actionbar -> actionbar.addMessage("vanish", new TimedMessage(3000, u -> "&a[VANISH] &cZablokowano interakcje")));
                }
            });
        }
    }
}
