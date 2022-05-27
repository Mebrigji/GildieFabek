package pl.saidora.core.events;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.saidora.core.cache.EventCache;
import pl.saidora.core.events.engine.CallbackEvent;
import pl.saidora.core.model.impl.User;

import java.util.Set;
import java.util.function.Consumer;

public class UserCommandEvent implements CallbackEvent<UserCommandEvent> {

    private final User user;
    private final PlayerCommandPreprocessEvent playerCommandPreprocessEvent;

    private boolean cancelled;

    private String commandLine;

    public UserCommandEvent(User user, PlayerCommandPreprocessEvent playerCommandPreprocessEvent){
        this.user = user;
        this.playerCommandPreprocessEvent = playerCommandPreprocessEvent;
        this.commandLine = playerCommandPreprocessEvent.getMessage();
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public User getUser(){
        return user;
    }

    public PlayerCommandPreprocessEvent getBukkitCommandEvent(){
        return playerCommandPreprocessEvent;
    }

    @Override
    public UserCommandEvent getEvent(){
        return this;
    }

    @Override
    public Set<Consumer<UserCommandEvent>> getCache(){
        return EventCache.USER_COMMAND_EVENT;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
