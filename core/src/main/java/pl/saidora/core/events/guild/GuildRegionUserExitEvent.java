package pl.saidora.core.events.guild;

import org.bukkit.Location;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface GuildRegionUserExitEvent extends CallbackEvent<GuildRegionUserExitEvent> {

    User getUser();

    Guild getGuild();

    Location getLocation();

    List<User> getUsersToNotify();

    @Override
    default GuildRegionUserExitEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<GuildRegionUserExitEvent>> getCache(){
        return EventCache.GUILD_REGION_USER_EXIT_EVENT;
    }
}
