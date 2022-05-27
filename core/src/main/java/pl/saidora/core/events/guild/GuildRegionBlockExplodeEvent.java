package pl.saidora.core.events.guild;

import org.bukkit.block.Block;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuildRegionBlockExplodeEvent implements CallbackEvent<GuildRegionBlockExplodeEvent> {

    private final Guild guild;
    private final User user;

    private final Block block;

    private boolean access, cancelled;

    public GuildRegionBlockExplodeEvent(Guild guild, User user, Block block, Function<User, Boolean> function){
        this.guild = guild;
        this.user = user;
        this.block = block;
        this.access = function.apply(user);
    }

    public Guild getGuild() {
        return guild;
    }

    public User getUser() {
        return user;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public GuildRegionBlockExplodeEvent getEvent() {
        return this;
    }

    @Override
    public Set<Consumer<GuildRegionBlockExplodeEvent>> getCache() {
        return EventCache.GUILD_REGION_BLOCK_EXPLODE_EVENT;
    }

    public Block getBlock() {
        return block;
    }
}
