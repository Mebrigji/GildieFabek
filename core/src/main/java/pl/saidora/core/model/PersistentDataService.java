package pl.saidora.core.model;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import pl.saidora.core.cache.AbyssCache;
import pl.saidora.core.cache.LeaderboardCache;
import pl.saidora.core.configuration.Configuration;
import pl.saidora.core.model.impl.Generator;
import pl.saidora.core.model.impl.User;

import java.util.Map;

public interface PersistentDataService {

    JavaPlugin getPlugin();

    Configuration getConfiguration();

    Map<String, User> getUsers();
    Map<String, User> getOnlineUsers();
    Map<Location, Generator> getGeneratorsToRegen();

    Map<Long, String> _abyss_getMessages();

    long _abyss_getCountDown();

    void _abyss_setCountDown(long time);

    LeaderboardCache getLeaderboardCache();
    AbyssCache getAbyssCache();

}
