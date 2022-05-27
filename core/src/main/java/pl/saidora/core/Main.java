package pl.saidora.core;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import pl.saidora.api.Accessibler;
import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ReflectionHelper;
import pl.saidora.api.helpers.SystemHelper;
import pl.saidora.core.commands.RepairCommand;
import pl.saidora.core.commands.guild.GuildCreateCommand;
import pl.saidora.core.commands.guild.GuildInfoCommand;
import pl.saidora.core.commands.guild.GuildPanelCommand;
import pl.saidora.core.model.ChatMessage;
import pl.saidora.core.helpers.MessageHolder;
import pl.saidora.core.cache.*;
import pl.saidora.core.commands.AbyssCommand;
import pl.saidora.core.commands.CoreCommand;
import pl.saidora.core.commands.GuildCommand;
import pl.saidora.core.configuration.Configuration;
import pl.saidora.core.configuration.ConfigurationSerdes;
import pl.saidora.core.factory.ScoreboardFactory;
import pl.saidora.core.factory.TabFactory;
import pl.saidora.core.helpers.ItemHelper;
import pl.saidora.core.helpers.LicenseHelper;
import pl.saidora.core.model.impl.Generator;
import pl.saidora.core.model.PersistentDataService;
import pl.saidora.core.factory.EventFactory;
import pl.saidora.core.factory.TaskFactory;
import pl.saidora.core.listeners.EventAdapterListener;
import pl.saidora.core.model.impl.GuildLeaderboard;
import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.UserLeaderboard;
import pl.saidora.core.model.impl.guild.Guild;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaPlugin implements PersistentDataService, Listener {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private final UserCache userCache = new UserCache();
    private final GuildCache guildCache = new GuildCache();
    private final StatueCache statueCache = new StatueCache();
    private final LeaderboardCache leaderboardCache = new LeaderboardCache();
    private final GeneratorCache generatorCache = new GeneratorCache();
    private final AbyssCache abyssCache = new AbyssCache();
    private ChatCache chatCache;

    public GuildCache getGuildCache() {
        return guildCache;
    }

    public UserCache getUserCache() {
        return userCache;
    }

    public StatueCache getStatueCache() {
        return statueCache;
    }

    public ChatCache getChatCache() {
        return chatCache;
    }

    public LeaderboardCache getLeaderboardCache() {
        return leaderboardCache;
    }

    public GeneratorCache getGeneratorCache() {
        return generatorCache;
    }

    public AbyssCache getAbyssCache() {
        return abyssCache;
    }

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void onLoad() {
        instance = this;
    }

    private Accessibler accessibler;

    public TabFactory tabFactory = new TabFactory();
    public ScoreboardFactory scoreboardFactory = new ScoreboardFactory();

    private LicenseHelper cl;


    private Map<Long, String> abyssMessageMap = new HashMap<>();


    public void reload() {
        configuration = ConfigManager.create(Configuration.class, i -> {
            i.withBindFile(getDataFolder() + "/config.yml");
            i.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            i.saveDefaults();
            i.load(true);
        });

        if(configuration.ABYSS_GUI_NEXT_EMPTY.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_NEXT_EMPTY = new ItemHelper(configuration.ABYSS_GUI_NEXT_EMPTY).setSkullOwner(configuration.ABYSS_GUI_NEXT_EMPTY_HEAD, true);
        }

        if(configuration.ABYSS_GUI_NEXT_EXISTS.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_NEXT_EXISTS = new ItemHelper(configuration.ABYSS_GUI_NEXT_EXISTS).setSkullOwner(configuration.ABYSS_GUI_NEXT_EXISTS_HEAD, true);
        }

        if(configuration.ABYSS_GUI_PREVIOUS_EMPTY.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_PREVIOUS_EMPTY = new ItemHelper(configuration.ABYSS_GUI_PREVIOUS_EMPTY).setSkullOwner(configuration.ABYSS_GUI_PREVIOUS_EMPTY_HEAD, true);
        }

        if(configuration.ABYSS_GUI_PREVIOUS_EXISTS.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_PREVIOUS_EXISTS = new ItemHelper(configuration.ABYSS_GUI_PREVIOUS_EXISTS).setSkullOwner(configuration.ABYSS_GUI_PREVIOUS_EXISTS_HEAD, true);
        }

        chatCache = ConfigManager.create(ChatCache.class, i -> {
            i.withBindFile(getDataFolder() + "/formats.yml");
            i.withConfigurer(new YamlBukkitConfigurer(), new ConfigurationSerdes());
            i.saveDefaults();
            i.load(true);
        });

        abyssMessageMap.clear();

        Pattern pattern = Pattern.compile("<[-0-9-0-9]+>.*");

        configuration.ABYSS_MESSAGES.forEach(s -> {
            Matcher mat = pattern.matcher(s);
            if(!mat.matches()) return;
            String matcher = mat.group();
            long time = Long.parseLong(matcher.split(" ")[0].replace("<", "").replace(">", ""));
            Main.this.abyssMessageMap.put(time, s.split("<" + time + "> ", 2)[1]);
        });

    }

    public void onEnable() {

        ReflectionHelper.PATH_FINDER.setVersion(ReflectionHelper.getServerVersion(), ReflectionHelper.PATH_FINDER.MINECRAFT);
        ReflectionHelper.PATH_FINDER.setVersion(ReflectionHelper.getServerVersion(), ReflectionHelper.PATH_FINDER.CRAFT_BUKKIT);

        configuration = ConfigManager.create(Configuration.class, i -> {
            i.withBindFile(getDataFolder() + "/config.yml");
            i.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
            i.saveDefaults();
            i.load(true);
        });

        //if ((cl = new LicenseHelper(configuration.LICENSE_KEY, "https://buybrain.pl/license/verify.php", this)).register(false)) {

        if(configuration.ABYSS_GUI_NEXT_EMPTY.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_NEXT_EMPTY = new ItemHelper(configuration.ABYSS_GUI_NEXT_EMPTY).setSkullOwner(configuration.ABYSS_GUI_NEXT_EMPTY_HEAD, true);
        }

        if(configuration.ABYSS_GUI_NEXT_EXISTS.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_NEXT_EXISTS = new ItemHelper(configuration.ABYSS_GUI_NEXT_EXISTS).setSkullOwner(configuration.ABYSS_GUI_NEXT_EXISTS_HEAD, true);
        }

        if(configuration.ABYSS_GUI_PREVIOUS_EMPTY.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_PREVIOUS_EMPTY = new ItemHelper(configuration.ABYSS_GUI_PREVIOUS_EMPTY).setSkullOwner(configuration.ABYSS_GUI_PREVIOUS_EMPTY_HEAD, true);
        }

        if(configuration.ABYSS_GUI_PREVIOUS_EXISTS.getType().equals(Material.SKULL_ITEM)){
            configuration.ABYSS_GUI_PREVIOUS_EXISTS = new ItemHelper(configuration.ABYSS_GUI_PREVIOUS_EXISTS).setSkullOwner(configuration.ABYSS_GUI_PREVIOUS_EXISTS_HEAD, true);
        }

        chatCache = ConfigManager.create(ChatCache.class, i -> {
            i.withBindFile(getDataFolder() + "/formats.yml");
            i.withConfigurer(new YamlBukkitConfigurer(), new ConfigurationSerdes());
            i.saveDefaults();
            i.load(true);
        });

        Pattern pattern = Pattern.compile("<[-0-9-0-9]+>.*");

        configuration.ABYSS_MESSAGES.forEach(s -> {
            Matcher mat = pattern.matcher(s);
            if(!mat.matches()) return;
            String matcher = mat.group();
            long time = Long.parseLong(matcher.split(" ")[0].replace("<", "").replace(">", ""));
            Main.this.abyssMessageMap.put(time, s.split("<" + time + "> ", 2)[1]);
        });

            tabFactory.setHeader(configuration.TABLIST_HEADER);
            tabFactory.setFooter(configuration.TABLIST_FOOTER);
            tabFactory.setPing(configuration.TABLIST_PING);

            for (int i = 0; i < configuration.TABLIST_ROWS.size(); i++) {
                tabFactory.getRows().put(i, configuration.TABLIST_ROWS.get(i));
            }

            scoreboardFactory.setDisplayName(configuration.SCOREBOARD_TITLE);
            scoreboardFactory.setLines(configuration.SCOREBOARD_LINES);

            scoreboardFactory.addReplacement("position", User::getPosition);
            scoreboardFactory.addReplacement("g-tag", user -> user.getGuild().isPresent() ? user.getGuild().get().getTag() : "Brak");
            scoreboardFactory.addReplacement("points", User::getPoints);
            scoreboardFactory.addReplacement("kills", User::getKills);
            scoreboardFactory.addReplacement("deaths", User::getDeaths);
            scoreboardFactory.addReplacement("level", User::getLevel);
            scoreboardFactory.addReplacement("exp", User::getExp);

            EventFactory eventFactory = new EventFactory();
            eventFactory.registerTeleportEvents();
            eventFactory.registerDefaultEvents();

            Bukkit.getPluginManager().registerEvents(new EventAdapterListener(), this);

            final Class<?> MinecraftServerClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "MinecraftServer");
            final Optional<Method> MGServer = ReflectionHelper.getMethod(MinecraftServerClass, "getServer");
            final Optional<Field> recentTps = ReflectionHelper.getField(MinecraftServerClass, "recentTps");

            leaderboardCache.register(User.class, new UserLeaderboard());
            leaderboardCache.register(Guild.class, new GuildLeaderboard());

            DecimalFormat format = new DecimalFormat("##.##");

            tabFactory.addReplacement("enabled-guilds", u -> "Nie");
            tabFactory.addReplacement("enabled-kits", u -> "Nie");
            tabFactory.addReplacement("enabled-diamond", u -> "Nie");
            tabFactory.addReplacement("tps", user -> format.format(((double[]) LambdaBypass.getOptional(recentTps, field -> ReflectionHelper.getValue(field, LambdaBypass.getOptional(MGServer, method -> ReflectionHelper.invoke(method, null)).getObject()).get()).getObject())[0]));
            tabFactory.addReplacement("cpu", user -> format.format(SystemHelper.getCpuUsage()));
            tabFactory.addReplacement("name", User::getName);
            tabFactory.addReplacement("points", User::getPoints);
            tabFactory.addReplacement("kills", User::getKills);
            tabFactory.addReplacement("deaths", User::getDeaths);
            tabFactory.addReplacement("kdr", User::getKDR);
            tabFactory.addReplacement("position", User::getPosition);
            tabFactory.addReplacement("ping", User::getPing);
            tabFactory.addReplacement("teleport-requests", User::getTeleportRequests);
            tabFactory.addReplacement("incognito", user -> user.isIncognito() ? "Tak" : "Nie");
            tabFactory.addReplacement("playersOnline", user -> Bukkit.getOnlinePlayers().size());
            tabFactory.addReplacement("accounts", user -> userCache.getCache().size());
            tabFactory.addReplacement("playersMax", user -> Bukkit.getServer().getMaxPlayers());
            tabFactory.addReplacement("groupDisplay", user -> "Gracz");
            tabFactory.addReplacement("g_create-first", user -> configuration.FIRST_GUILD);
            tabFactory.addReplacement("g_create-second", user -> configuration.SECOND_GUILD);
            tabFactory.addReplacement("g_create-third", user -> configuration.THIRD_GUILD);
            tabFactory.addReplacement("ram-free", user -> SystemHelper.getFreeRam());
            tabFactory.addReplacement("ram-use", user -> SystemHelper.getUsageRam());
            tabFactory.addReplacement("ram-total", user -> SystemHelper.getTotalRam());
            tabFactory.addReplacement("ram-max", user -> SystemHelper.getMaxRam());

            tabFactory.addGuildReplacement("g-tag", guild -> guild == null ? "Brak" : guild.getTag());
            tabFactory.addGuildReplacement("g-leader", guild -> guild == null ? "Brak" : guild.getLeader().getName());
            tabFactory.addGuildReplacement("g-name", guild -> guild == null ? "Brak" : guild.getName());
            tabFactory.addGuildReplacement("g-points", guild -> guild == null ? "0" : String.valueOf(guild.getPoints()));
            tabFactory.addGuildReplacement("g-kills", guild -> guild == null ? "0" : String.valueOf(guild.getKills()));
            tabFactory.addGuildReplacement("g-deaths", guild -> guild == null ? "0" : String.valueOf(guild.getDeaths()));
            tabFactory.addGuildReplacement("g-kdr", guild -> guild == null ? "0" : String.valueOf(guild.getKDR()));
            tabFactory.addGuildReplacement("g-position", guild -> guild == null ? "Brak" : String.valueOf(guild.getPosition()));

            leaderboardCache.get(User.class).ifPresent(userLeaderboard -> {
                for (int i = 0; i < 40; i++) {
                    int finalI = i;
                    tabFactory.addReplacement("top_" + (i + 1) + "-points", u -> {
                        User user = userLeaderboard.getByPosition(finalI);
                        return user == null ? "---" : user.getPoints();
                    });
                    int finalI1 = i;
                    tabFactory.addReplacement("top_" + (i + 1) + "-g-tag", u -> {
                        User user = userLeaderboard.getByPosition(finalI1);
                        return user == null ? "---" : user.getGuild().isPresent() ? u.getGuildPrefix(user.getGuild().get()) : "";
                    });
                    int finalI2 = i;
                    tabFactory.addReplacement("top_" + (i + 1) + "-name", u -> {
                        User user = userLeaderboard.getByPosition(finalI2);
                        return user == null ? "Brak" : user.getName();
                    });
                }
            });

            leaderboardCache.get(Guild.class).ifPresent(userLeaderboard -> {
                for (int i = 0; i < 40; i++) {
                    int finalI = i;
                    tabFactory.addReplacement("g_top_" + (i + 1) + "-points", g -> {
                        Guild guild = userLeaderboard.getByPosition(finalI);
                        return guild == null ? "---" : guild.getPoints();
                    });
                    int finalI1 = i;
                    tabFactory.addReplacement("g_top_" + (i + 1) + "-tag", g -> {
                        Guild guild = userLeaderboard.getByPosition(finalI1);
                        return guild == null ? "Brak" : guild.getTag();
                    });
                }
            });

            TaskFactory taskFactory = new TaskFactory();
            taskFactory.registerUserScheduler(this);
            taskFactory.registerSortScheduler(this);
            taskFactory.registerGeneratorScheduler(this);
            taskFactory.registerAbyssScheduler(this);

            accessibler = new Accessibler();

            Map<String, Command> commandMap = accessibler.getKnowCommands(getServer());
            commandMap.remove("version");
            commandMap.remove("plugins");
            commandMap.remove("about");
            commandMap.remove("reload");
            commandMap.remove("ver");
            commandMap.remove("restart");
            commandMap.remove("tps");
            commandMap.remove("rl");
            commandMap.remove("pl");
            commandMap.remove("bukkit:version");
            commandMap.remove("spigot:tps");
            commandMap.remove("bukkit:reload");
            commandMap.remove("bukkit:plugins");
            commandMap.remove("bukkit:ver");
            commandMap.remove("bukkit:pl");
            commandMap.remove("bukkit:timings");
            commandMap.remove("bukkit:rl");
            commandMap.remove("bukkit:about");
            commandMap.remove("spigot:restart");

            registerCommand(new CoreCommand(), new GuildCommand(), new AbyssCommand(), new RepairCommand());
            GuildCommand.registerSubCommand(new GuildCreateCommand(), new GuildInfoCommand(), new GuildPanelCommand());
        //} else {
        //    Bukkit.getPluginManager().registerEvents(this, this);
        //}
    }

    private void registerCommand(pl.saidora.core.commands.system.Command... commands){
        for (pl.saidora.core.commands.system.Command command : commands) {
            accessibler.addCommand(command.commandInfo().name(), pl.saidora.core.commands.system.Command.toBukkitCommand(command), getServer());
        }
    }

    @Override
    public Map<Location, Generator> getGeneratorsToRegen() {
        return generatorCache.getRegenMap();
    }

    @Override
    public Map<Long, String> _abyss_getMessages() {
        return abyssMessageMap;
    }

    @Override
    public long _abyss_getCountDown() {
        return abyssCache.getCounter();
    }

    @Override
    public void _abyss_setCountDown(long time) {
        this.abyssCache.setCounter(time);
    }

    public void setAbyssMessageMap(Map<Long, String> abyssMessageMap) {
        this.abyssMessageMap = abyssMessageMap;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void joinEvent(PlayerJoinEvent event){
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            if(event.getPlayer().hasPermission("sacore") || event.getPlayer().isOp()){
                ChatMessage chatMessage = () -> Arrays.asList(MessageHolder.create("&9[SA-CORE] &7Aby plugin działał poprawnie zakup licencje na naszym "), MessageHolder.create("&9&ndiscordzie", Collections.singletonList("&9https://dc.wolfmc.pl")));
                event.getPlayer().spigot().sendMessage(chatMessage.build());
            }
        }, 5);
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public Map<String, User> getUsers() {
        return new HashMap<>(userCache.getCache());
    }

    @Override
    public Map<String, User> getOnlineUsers() {
        return new HashMap<>(userCache.getOnlineCache());
    }

    public LicenseHelper getCl() {
        return cl;
    }
}