package pl.saidora.core.cache;

import eu.okaeri.configs.serdes.SerializationData;
import pl.saidora.core.events.*;
import pl.saidora.core.events.guild.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventCache {

    public static Set<Consumer<ScoreboardUpdateEvent>> SCOREBOARD_UPDATE_EVENT = new HashSet<>();

    public static Set<Consumer<UserDamageGivenEvent>> USER_DAMAGE_GIVEN_EVENT = new HashSet<>();
    public static Set<Consumer<UserJoinEvent>> USER_JOIN_EVENT = new HashSet<>();
    public static Set<Consumer<UserQuitEvent>> USER_QUIT_EVENT = new HashSet<>();
    public static Set<Consumer<UserInventoryClickEvent>> USER_INVENTORY_CLICK_EVENT = new HashSet<>();
    public static Set<Consumer<UserDeathEvent>> USER_DEATH_EVENT = new HashSet<>();

    public static Set<Consumer<UserBlockBreakEvent>> USER_BLOCK_BREAK_EVENT = new HashSet<>();
    public static Set<Consumer<UserBlockPlaceEvent>> USER_BLOCK_PLACE_EVENT = new HashSet<>();

    public static Set<Consumer<UserChatEvent>> USER_CHAT_EVENT = new HashSet<>();
    public static Set<Consumer<UserCommandEvent>> USER_COMMAND_EVENT = new HashSet<>();

    public static Set<Consumer<TeleportCountdownStartEvent>> USER_TELEPORT_COUNTDOWN_START_EVENT = new HashSet<>();

    public static Set<Consumer<UserSendTeleportRequestEvent>> USER_SEND_TELEPORT_REQUEST = new HashSet<>();
    public static Set<Consumer<UserAcceptTeleportRequestEvent>> USER_ACCEPT_TELEPORT_REQUEST = new HashSet<>();

    public static Set<Consumer<GuildRegionBlockBreakEvent>> GUILD_REGION_BLOCK_BREAK_EVENT = new HashSet<>();
    public static Set<Consumer<GuildRegionBlockPlaceEvent>> GUILD_REGION_BLOCK_PLACE_EVENT = new HashSet<>();

    public static Set<Consumer<GuildRegionBlockExplodeEvent>> GUILD_REGION_BLOCK_EXPLODE_EVENT = new HashSet<>();

    public static Set<Consumer<GuildRegionUserEntryEvent>> GUILD_REGION_USER_ENTRY_EVENT = new HashSet<>();
    public static Set<Consumer<GuildRegionUserExitEvent>> GUILD_REGION_USER_EXIT_EVENT = new HashSet<>();

}
