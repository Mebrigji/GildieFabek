package pl.saidora.anti.grief;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public static Configuration configuration;

    public void onEnable(){
        instance = this;
        configuration = ConfigManager.create(Configuration.class, i -> {
            i.withBindFile(getDataFolder() + "/config.yml");
            i.withConfigurer(new YamlBukkitConfigurer());
            i.saveDefaults();
            i.load(true);
        });
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            new HashMap<>(locationLongMap).forEach(((location, aLong) -> {
                if(aLong > System.currentTimeMillis()) return;
                location.getBlock().setType(Material.AIR);
                locationLongMap.remove(location);
            }));
        }, 0, 20);
    }

    private Map<Location, Long> locationLongMap = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("bypass.antigrief")) return;
        locationLongMap.put(event.getBlock().getLocation(), System.currentTimeMillis() + configuration.TIME);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', timeParse(configuration.MESSAGE, configuration.TIME)));
    }

    protected static String timeParse(String text, long time){
        long minute, second;
        minute = TimeUnit.MILLISECONDS.toMinutes(time);
        String prefix = configuration.TIME_PARSER;
        if(minute > 0){
            time -= TimeUnit.MINUTES.toMillis(minute);
            prefix = prefix.replace("%minute%", minute + configuration.TIME_PARSER_MINUTE);
        }
        second = TimeUnit.MILLISECONDS.toSeconds(time);
        if(second > 0){
            time -= TimeUnit.SECONDS.toMillis(time);
            prefix = prefix.replace("%second%", second + configuration.TIME_PARSER_SECOND);
        }
        return text.replace("%time%", prefix.replace("%millis%", time + configuration.TIME_PARSER_MILLISECOND));
    }
}
