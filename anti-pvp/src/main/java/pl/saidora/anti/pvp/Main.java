package pl.saidora.anti.pvp;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.saidora.api.Accessibler;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private static Map<String, AtomicBoolean> protectionMap = new HashMap<>();

    public static Map<String, AtomicBoolean> getProtectionMap() {
        return protectionMap;
    }

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void onEnable(){
        instance = this;

        ReflectionHelper.PATH_FINDER.setVersion(ReflectionHelper.getServerVersion(), ReflectionHelper.PATH_FINDER.CRAFT_BUKKIT);

        configuration = ConfigManager.create(Configuration.class, i -> i.withConfigurer(new YamlBukkitConfigurer())
                .withBindFile(getDataFolder() + "/config.yml")
                .saveDefaults()
                .load(true));

        Bukkit.getPluginManager().registerEvents(this, this);
        new Accessibler().addCommand("saidora", new ProtectionCommand(), this.getServer());

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player victim){
            if(event.getDamager() instanceof Player attacker){
                AtomicBoolean atomicBoolean = protectionMap.getOrDefault(attacker.getName(), new AtomicBoolean(false));
                if(atomicBoolean.get()) {
                    event.setCancelled(true);
                    attacker.sendMessage(ColorHelper.translateColors(configuration.MESSAGE_ATTACK));
                    return;
                }
            }
            AtomicBoolean atomicBoolean = protectionMap.getOrDefault(victim.getName(), new AtomicBoolean(false));
            if(!atomicBoolean.get()) return;
            event.setCancelled(true);
            if(event.getDamager() instanceof Player attacker) attacker.sendMessage(ColorHelper.translateColors(configuration.MESSAGE_PROTECTED.replace("%playerName%", victim.getName())));
        }
    }
}
