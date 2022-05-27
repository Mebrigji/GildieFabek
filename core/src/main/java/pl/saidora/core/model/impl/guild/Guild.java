package pl.saidora.core.model.impl.guild;

import org.bukkit.Location;
import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.core.Main;
import pl.saidora.core.helpers.RegionHelper;
import pl.saidora.core.model.*;
import pl.saidora.api.model.Rank;
import pl.saidora.api.model.guild.*;
import pl.saidora.core.model.impl.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Guild implements Rank, Region, GuildHearth {

    private final String tag;

    private String name;

    private Map<String, User> users = new HashMap<>();
    private Map<String, User> onlineUsers = new HashMap<>();

    private Map<User, GuildRank> guildRankMap = new HashMap<>();

    private GuildTask guildTask;
    private GuildDeposit guildDeposit;
    private War war;
    private Home home;

    private List<Guild> allies = new ArrayList<>();
    private Map<Guild, Long> allyInvite = new HashMap<>();

    private Map<Long, GuildHistory> guildHistory = new HashMap<>();

    private Map<GuildPermission, List<User>> permissions = new HashMap<>();

    private double minX, minZ, maxX, maxZ;
    private int size, lives = 1, membersLevel = 1;

    private long blockExpireDate = 0, expireGuild = 0;

    public Guild(String tag){
        this.tag = tag;
    }

    public boolean isEnemy(Guild guild){
        return this.war != null && guild.war != null && (this.war.getAttacker().equals(guild) || this.war.getDefenders().equals(guild));
    }

    public int getPoints() {
        return users.values().stream().mapToInt(User::getPoints).sum();
    }

    public int getKills() {
        return users.values().stream().mapToInt(User::getKills).sum();
    }

    public int getDeaths() {
        return users.values().stream().mapToInt(User::getDeaths).sum();
    }

    public int getKDR() {

        int kills = getKills();
        int deaths = getDeaths();

        return kills > 0 ? deaths > 0 ? kills / deaths : kills : deaths;
    }

    public void setPoints(int points) {
        throw new UnsupportedOperationException("This function is unavailable in guild object.");
    }

    public void setKills(int kills) {
        throw new UnsupportedOperationException("This function is unavailable in guild object.");
    }

    public void setDeaths(int deaths) {
        throw new UnsupportedOperationException("This function is unavailable in guild object.");
    }

    public int getPosition() {
        return Main.getInstance().getLeaderboardCache().get(Guild.class).get().getPosition(this)+1;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Home getGuildHome() {
        return home;
    }

    public void setGuildHome(Home home) {
        this.home = home;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public List<User> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    public void setOnline(User user, boolean online) {
        onlineUsers.remove(user.getName());
        if(online) onlineUsers.put(user.getName(), user);
    }

    public void setLeader(User user) {
        guildRankMap.remove(user);
        guildRankMap.put(user, GuildRank.LEADER);

        users.forEach((name, u) -> {
            if(name.equalsIgnoreCase(user.getName())) return;
            GuildRank rank = getUserRank(u);
            if(rank.equals(GuildRank.OUTSIDE)) {
                System.out.println("I found outside user.");
            } else if(rank.equals(GuildRank.LEADER)) {
                guildRankMap.remove(u);
                guildRankMap.put(u, GuildRank.MEMBER);
            }
        });

        users.putIfAbsent(user.getName(), user);
        if(user.isOnline()) onlineUsers.putIfAbsent(user.getName(), user);

    }

    public void addUser(User user) {
        this.users.putIfAbsent(user.getName(), user);
        setUserRank(user, GuildRank.MEMBER);
    }

    public void setUserRank(User user, GuildRank guildRank) {
        guildRankMap.remove(user);
        guildRankMap.put(user, guildRank);
    }

    public GuildRank getUserRank(User user) {
        return guildRankMap.getOrDefault(user, GuildRank.OUTSIDE);
    }

    public double getMinX() {
        return minX;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public GuildDeposit getDeposit() {
        return guildDeposit;
    }

    public GuildTask getCurrentlyGuildTask() {
        return guildTask;
    }

    public void setGuildTask(GuildTask guildTask) {
        this.guildTask = guildTask;
    }

    public Optional<Long> getPlaceBlockExpireDate() {
        return Optional.ofNullable(blockExpireDate < System.currentTimeMillis() ? null : blockExpireDate);
    }

    public void setPlaceBlock(long date) {
        this.blockExpireDate = date;
    }

    public double getDistance(Location location) {
        return RegionHelper.getDistance(location, this);
    }

    public boolean isIn(Location location) {
        return RegionHelper.isIn(location, this);
    }

    public User getLeader() {
        return LambdaBypass.get(users.values(), users1 -> users1.stream().filter(user -> getUserRank(user).equals(GuildRank.LEADER)).findFirst().orElse(null)).getObject();
    }

    public void updateCorners() {
        Location center = getCenter();

        double x1 = center.getX() + size;
        double x2 = center.getX() - size;

        double z1 = center.getZ() + size;
        double z2 = center.getZ() - size;

        minX = Math.min(x1, x2);
        minZ = Math.min(z1, z2);

        maxX = Math.max(x1, x2);
        maxZ = Math.max(z1, z2);
    }

    /**
     * Hearth
     */


    private double hearth_minX, hearth_minZ, hearth_maxX, hearth_maxZ;
    private Location center, up, down;

    @Override
    public double getDistanceToBorder(Location location) {
        return RegionHelper.getDistance(location, hearth_minX, hearth_minZ, hearth_maxX, hearth_maxZ);
    }

    @Override
    public Location getCenter() {
        return center;
    }

    @Override
    public Location getUp() {
        return up;
    }

    @Override
    public Location getDown() {
        return down;
    }

    @Override
    public void setCenter(Location location) {
        this.center = location;
    }

    @Override
    public void setUp(Location location) {
        this.up = location;
    }

    @Override
    public void setDown(Location location) {
        this.down = location;
    }

    @Override
    public void updateHearthCorners() {
        Location center = getCenter();

        double x1 = center.getX() + 10;
        double x2 = center.getX() - 10;

        double z1 = center.getZ() + 10;
        double z2 = center.getZ() - 10;

        hearth_minX = Math.min(x1, x2);
        hearth_minZ = Math.min(z1, z2);

        hearth_maxX = Math.max(x1, x2);
        hearth_maxZ = Math.max(z1, z2);

        down = new Location(center.getWorld(), hearth_minX, 0, hearth_minZ);
        up = new Location(center.getWorld(), hearth_maxX, 255, hearth_maxZ);
    }

    public void setSize(int size) {
        this.size = Math.max(10, size);
    }

    public int getSize() {
        return size;
    }

    public void setGuildDeposit(GuildDeposit guildDeposit) {
        this.guildDeposit = guildDeposit;
    }

    public Map<Long, GuildHistory> getGuildHistory() {
        return guildHistory;
    }

    public void setGuildHistory(Map<Long, GuildHistory> guildHistory) {
        this.guildHistory = guildHistory;
    }

    public void addToHistory(long date, User user, String comment){
        guildHistory.put(date, new GuildHistory() {
            @Override
            public long getDate() {
                return date;
            }

            @Override
            public User who() {
                return user;
            }

            @Override
            public String description() {
                return comment;
            }
        });
    }

    public Map<GuildPermission, List<User>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<GuildPermission, List<User>> permissions) {
        this.permissions = permissions;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public long getExpireGuild() {
        return expireGuild;
    }

    public void setExpireGuild(long expireGuild) {
        this.expireGuild = expireGuild;
    }

    public War getWar() {
        return war;
    }

    public void setWar(War war) {
        this.war = war;
    }

    @Override
    public String toString() {
        return "Guild{" +
                "tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                ", guildTask=" + guildTask +
                ", guildDeposit=" + guildDeposit +
                ", war=" + war +
                ", home=" + home +
                ", guildHistory=" + guildHistory +
                ", permissions=" + permissions +
                ", minX=" + minX +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxZ=" + maxZ +
                ", size=" + size +
                ", lives=" + lives +
                ", blockExpireDate=" + blockExpireDate +
                ", expireGuild=" + expireGuild +
                ", hearth_minX=" + hearth_minX +
                ", hearth_minZ=" + hearth_minZ +
                ", hearth_maxX=" + hearth_maxX +
                ", hearth_maxZ=" + hearth_maxZ +
                ", center=" + center +
                ", up=" + up +
                ", down=" + down +
                '}';
    }

    public List<Guild> getAllies() {
        return allies;
    }

    public void setAllies(List<Guild> allies) {
        this.allies = allies;
    }

    public Map<Guild, Long> getAllyInvite() {
        return allyInvite;
    }

    public void setAllyInvite(Map<Guild, Long> allyInvite) {
        this.allyInvite = allyInvite;
    }

    public int getMaxMembers(){
        return membersLevel == 1 ? Main.getInstance().getConfiguration().GUILD_MEMBERS_LEVEL_1_AMOUNT :
                membersLevel == 2 ? Main.getInstance().getConfiguration().GUILD_MEMBERS_LEVEL_2_AMOUNT :
                        Main.getInstance().getConfiguration().GUILD_MEMBERS_LEVEL_3_AMOUNT;
    }

    public int getMembersLevel() {
        return membersLevel;
    }

    public void setMembersLevel(int membersLevel) {
        this.membersLevel = membersLevel;
    }
}
