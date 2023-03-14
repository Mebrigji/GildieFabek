package pl.saidora.core.cache;

import pl.saidora.core.factory.NewerOptional;
import pl.saidora.core.model.Leaderboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LeaderboardCache {

    private final Map<String, Leaderboard> cache = new HashMap<>();

    public Map<String, Leaderboard> getCache() {
        return cache;
    }

    public <V> NewerOptional<Leaderboard<V>> get(String id, Class<V> type){
        return NewerOptional.fromOldOptional(cache.values().stream().filter(leaderboard -> leaderboard.getLeaderboardName().equals(id)).map(leaderboard -> (Leaderboard<V>)leaderboard).findFirst());
    }

    public <V> void register(String id, Leaderboard<V> leaderboard){
        cache.put(id, leaderboard);
    }

    public <V> void register(Leaderboard<V> leaderboard){
        cache.put(leaderboard.getLeaderboardName(), leaderboard);
    }
    

}
