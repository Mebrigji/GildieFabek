package pl.saidora.core.events.guild;

import org.bukkit.Location;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface GuildRegionUserEntryEvent extends CallbackEvent<GuildRegionUserEntryEvent> {

    User getUser();

    Guild getGuild();

    Location getLocation();

    List<User> getUsersToNotify();

    @Override
    default GuildRegionUserEntryEvent getEvent(){
        return this;
    }

    @Override
    default Set<Consumer<GuildRegionUserEntryEvent>> getCache(){
        return EventCache.GUILD_REGION_USER_ENTRY_EVENT;
    }
}
