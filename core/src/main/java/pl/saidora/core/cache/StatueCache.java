package pl.saidora.core.cache;

import org.bukkit.Location;
import pl.saidora.core.helpers.Overwrite;
import pl.saidora.core.model.impl.TeleportStatue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class StatueCache {

    private final Map<Location, TeleportStatue> cache = new HashMap<>();

    public Map<Location, TeleportStatue> getCache() {
        return cache;
    }

    public Optional<TeleportStatue> getStatue(Location location){
        return Optional.ofNullable(cache.get(location));
    }

    public Overwrite<TeleportStatue> registerStatue(Location location){
        return createStatue(location, TeleportStatue::new);
    }

    private Overwrite<TeleportStatue> createStatue(Location location, Function<Location, TeleportStatue> function){
        TeleportStatue statue = function.apply(location);
        return () -> statue;
    }

}
