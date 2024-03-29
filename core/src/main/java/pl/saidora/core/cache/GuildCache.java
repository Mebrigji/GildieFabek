package pl.saidora.core.cache;

import org.bukkit.Location;
import pl.saidora.api.helpers.ReflectionHelper;
import pl.saidora.core.Main;
import pl.saidora.core.configuration.Configuration;
import pl.saidora.core.factory.NewerOptional;
import pl.saidora.core.model.impl.guild.GuildPermission;
import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.guild.Guild;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GuildCache {

    private final Map<String, Guild> cache = new HashMap<>();

    public NewerOptional<Guild> getGuildByTag(String tag){
        return NewerOptional.ofNullable(cache.get(tag));
    }

    public NewerOptional<Guild> getGuildByName(String name){
        return NewerOptional.fromOldOptional(cache.values().stream().filter(guild -> guild.getName().equals(name)).findFirst());
    }

    public NewerOptional<Guild> getGuildByLocation(Location location){
        return NewerOptional.fromOldOptional(cache.values().stream().filter(guild -> guild.isIn(location)).findFirst());
    }

    public Map<String, Guild> getCache() {
        return cache;
    }

    public CompletableFuture<Guild> createGuild(String tag, Location location, User user){
        Guild guild = new Guild(tag.toUpperCase());

        guild.setName(tag + "-UnsetName");
        guild.setSize(40);
        guild.setLeader(user);
        guild.setCenter(location);
        guild.updateCorners();
        guild.updateHearthCorners();

        for (Field declaredField : GuildPermission.class.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if(declaredField.getType().equals(GuildPermission.class)) guild.getPermissions().put((GuildPermission) ReflectionHelper.getValue(declaredField, null).get(), new ArrayList<>());
            declaredField.setAccessible(false);
        }

        guild.setExpireGuild(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3));
        return CompletableFuture.completedFuture(guild);
    }

    public Guild register(Guild guild){
        Configuration configuration = Main.getInstance().getConfiguration();

        if(configuration.FIRST_GUILD.isEmpty() || configuration.FIRST_GUILD.equalsIgnoreCase("Brak")) configuration.FIRST_GUILD = guild.getTag();
        else if(configuration.SECOND_GUILD.isEmpty() || configuration.SECOND_GUILD.equalsIgnoreCase("Brak")) configuration.SECOND_GUILD = guild.getTag();
        else if(configuration.THIRD_GUILD.isEmpty() || configuration.THIRD_GUILD.equalsIgnoreCase("Brak")) configuration.THIRD_GUILD = guild.getTag();

        cache.put(guild.getTag(), guild);
        guild.getLeader().setGuild(guild);
        Main.getInstance().getLeaderboardCache().get("guildPoints", Guild.class).ifPresent(guildLeaderboard -> guildLeaderboard.addIfAbsent(guild));
        return guild;
    }
}
