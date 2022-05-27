package pl.saidora.core.cache;

import org.bukkit.entity.Player;
import pl.saidora.core.model.impl.Abyss;
import pl.saidora.core.model.Options;
import pl.saidora.core.model.impl.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class UserCache {

    private final Map<String, User> cache = new HashMap<>();
    private final Map<String, User> onlineCache = new HashMap<>();

    public Optional<User> lookByUUID(UUID uuid, boolean online){
        return online ? onlineCache.values().stream().filter(user -> user.getUUID().equals(uuid)).findFirst() :  cache.values().stream().filter(user -> user.getUUID().equals(uuid)).findFirst();
    }

    public Optional<User> lookByName(String name, boolean online){
        return Optional.ofNullable(online ? onlineCache.get(name) : cache.get(name));
    }

    public User get(Function<String, User> function, String name){
        User user = cache.get(name);
        if(user == null){
            user = function.apply(name);
            ((Options)user).registerOption(Abyss.class);
            cache.put(name, user);
        }
        return user;
    }

    public User get(Function<String, User> function, Player player){
        User user = get(function, player.getName());
        user.setPlayer(player);
        return user;
    }

    public Map<String, User> getOnlineCache() {
        return onlineCache;
    }

    public Map<String, User> getCache() {
        return cache;
    }
}
