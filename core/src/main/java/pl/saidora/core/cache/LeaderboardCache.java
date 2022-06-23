package pl.saidora.core.cache;

import pl.saidora.core.factory.NewerOptional;
import pl.saidora.core.model.Leaderboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LeaderboardCache {

    private final Map<Class, Leaderboard> cache = new HashMap<>();

    public Map<Class, Leaderboard> getCache() {
        return cache;
    }

    public <V> NewerOptional<Leaderboard<V>> get(Class<V> clazz){
        return NewerOptional.ofNullable(cache.get(clazz));
    }

    public <V> void register(Class<V> clazz, Leaderboard<V> leaderboard){
        cache.put(clazz, leaderboard);
    }

}
