package pl.saidora.core.model.impl;

import com.google.gson.JsonObject;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.ReflectionHelper;
import pl.saidora.api.model.*;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.configuration.Configuration;
import pl.saidora.core.events.UserAcceptTeleportRequestEvent;
import pl.saidora.core.events.UserSendTeleportRequestEvent;
import pl.saidora.core.factory.NewerOptional;
import pl.saidora.core.handlers.TeleportHandler;
import pl.saidora.core.helpers.ChatTransformer;
import pl.saidora.core.helpers.InventoryHelper;
import pl.saidora.core.model.ChatMessage;
import pl.saidora.core.model.Options;
import pl.saidora.core.model.Sender;
import pl.saidora.core.model.impl.guild.Guild;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class User implements Rank, Sender, Options {

    private final String name;
    private UUID uuid;
    private InetSocketAddress inetSocketAddress;

    private JsonObject property;

    private int points = 200;
    private int kills;
    private int deaths;

    private int level = 1;

    private long exp, requiredExp;
    private final List<String> messages = new ArrayList<>();

    private double money;

    private Player player;

    private ScoreBoard scoreBoard;
    private TabList tabList;
    private Guild guild;
    private Teleport teleport;
    private PlayerPacket playerPacket;
    private Actionbar actionbar;
    private InventoryHolder inventoryHolder;
    private AntiLogout antiLogout;
    private boolean online,  incognito, vanish, v_interact, v_build, v_attack, v_drop, v_pickup;

    private long teleportDelay, turboDrop, turboExp, chatDelay, join, quit, timeSpend;

    private final Map<ItemStack, Integer> deposit = new HashMap<>();
    private final Map<Location, Generator> generatorMap = new HashMap<>();
    private final Map<Class<?>, AtomicBoolean> optionMap = new HashMap<>();

    private final Map<Kit, AtomicLong> intervalMap = new HashMap<>();

    private Material lastGeneratorMaterial;

    private Map<String, TeleportHandler> requests = new HashMap<>();
    private Map<String, User> sentRequests = new HashMap<>();

    private User lastReceiver;
    private final List<User> blocked = new ArrayList<>();
    private final List<User.ToggleType> toggleTypes = new ArrayList<>();

    public User(String name){
        this.name = name;
    }

    public boolean canClaim(Kit kit){
        AtomicLong a = intervalMap.get(kit);
        return a == null || a.get() < System.currentTimeMillis();
    }

    public Inventory claim(Kit kit){
        AtomicLong atomicLong = intervalMap.get(kit);

        if(atomicLong == null) {
            atomicLong = new AtomicLong();
            intervalMap.put(kit, atomicLong);
        }

        atomicLong.set(System.currentTimeMillis() + kit.getDelay());

        int size;

        if(kit.getItemStackList().size() <= 9) size = 9;
        else if(kit.getItemStackList().size() <= 2*9) size = 2*9;
        else if(kit.getItemStackList().size() <= 3*9) size = 3*9;
        else if(kit.getItemStackList().size() <= 4*9) size = 4*9;
        else if(kit.getItemStackList().size() <= 5*9) size = 5*9;
        else size = 6*9;

        Inventory claim = Main.getInstance().getKitCache().CLAIM;

        Inventory inventory = Bukkit.createInventory(null, size, claim.getTitle().replace("%kit%", kit.getId()));
        inventory.addItem(kit.getItemStackList().toArray(new ItemStack[]{}));

        return inventory;
    }

    public boolean isV_interact() {
        return v_interact;
    }

    public void setV_interact(boolean v_interact) {
        this.v_interact = v_interact;
    }

    public boolean isV_build() {
        return v_build;
    }

    public void setV_build(boolean v_build) {
        this.v_build = v_build;
    }

    public boolean isV_attack() {
        return v_attack;
    }

    public void setV_attack(boolean v_attack) {
        this.v_attack = v_attack;
    }

    public boolean isV_drop() {
        return v_drop;
    }

    public void setV_drop(boolean v_drop) {
        this.v_drop = v_drop;
    }

    public boolean isV_pickup() {
        return v_pickup;
    }

    public void setV_pickup(boolean v_pickup) {
        this.v_pickup = v_pickup;
    }

    public Inventory preview(Kit kit){
        Inventory preview = Main.getInstance().getKitCache().PREVIEW;

        Inventory inventory = Bukkit.createInventory(null, preview.getSize(), preview.getTitle().replace("%kit%", kit.getId()));
        inventory.setContents(preview.getContents());
        inventory.addItem(kit.getItemStackList().toArray(new ItemStack[]{}));

        return inventory;
    }

    public long getKitDelay(Kit kit){
        return intervalMap.get(kit).get();
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public void setTabList(TabList tabList) {
        this.tabList = tabList;
    }

    public void setTeleport(Teleport teleport) {
        this.teleport = teleport;
    }

    public void setTeleportDelay(long teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    public void setActionbar(Actionbar actionbar) {
        this.actionbar = actionbar;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getRequiredExp() {
        return requiredExp;
    }

    public void setRequiredExp(long requiredExp) {
        this.requiredExp = requiredExp;
    }

    public Group getGroup(){
        return LuckPermsProvider.get().getGroupManager().getGroup(getGroupName());
    }

    public String getGroupName(){
        net.luckperms.api.model.user.User user;
        if((user = LuckPermsProvider.get().getUserManager().getUser(name)) == null) return "Gracz";
        return user.getPrimaryGroup();
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public int getKDR() {
        return kills <= 0 ? deaths : deaths <= 0 ? kills : kills / deaths;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public int getPosition() {
        return Main.getInstance().getLeaderboardCache().get(User.class).get().getPosition(this)+1;
    }

    @Override
    public void execute(String commandLine) {
        if(player == null) return;
        Bukkit.dispatchCommand(player, commandLine);
    }

    @Override
    public void addMessage(String message) {
        this.messages.add(message);
    }

    @Override
    public void addMessages(List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public void addMessages(String[] messages) {
        this.messages.addAll(Arrays.asList(messages));
    }

    @Override
    public MessageBuilder prepareMessage(String message) {
        return new MessageBuilder(this, message);
    }

    @Override
    public MessageBuilder prepareMessage(String... messages) {
        return prepareMessage(String.join("\n", messages));
    }

    @Override
    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void send() {
        if(player == null) return;
        player.sendMessage(ColorHelper.translateColors(String.join("\n", messages)));
        messages.clear();
    }

    @Override
    public void send(boolean async) {
        if(!async) send();
        else {
            messages.removeIf(s -> {
                player.sendMessage(ColorHelper.translateColors(s));
                return true;
            });
        }

    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public NewerOptional<Player> asPlayer() {
        return NewerOptional.ofNullable(player);
    }

    public NewerOptional<Guild> getGuild() {
        return NewerOptional.ofNullable(guild);
    }

    public boolean hasGuild(){
        return guild != null;
    }

    public NewerOptional<ScoreBoard> getScoreboard() {
        return NewerOptional.ofNullable(scoreBoard);
    }

    public NewerOptional<TabList> getTabList() {
        return NewerOptional.ofNullable(tabList);
    }

    public NewerOptional<Teleport> getTeleport() {
        return NewerOptional.ofNullable(teleport);
    }

    public NewerOptional<TeleportHandler> fetchTeleportRequest(String name) {
        return NewerOptional.ofNullable(requests.get(name));
    }

    public NewerOptional<Actionbar> getActionbar(){
        return NewerOptional.ofNullable(actionbar);
    }

    public NewerOptional<AntiLogout> getAntyLogout() {
        return NewerOptional.ofNullable(antiLogout);
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public boolean sendTeleportRequest(User user) {
        if(!user.fetchTeleportRequest(name).isPresent()){
            UserSendTeleportRequestEvent event = new UserSendTeleportRequestEvent(this, user, System.currentTimeMillis() + Main.getInstance().getConfiguration().TELEPORT_EXPIRE);
            event.call();
            if(event.isCancelled()) return false;
            event.getTarget().requests.put(name, new TeleportHandler() {
                @Override
                public User getTarget() {
                    return user;
                }

                @Override
                public User getUser() {
                    return User.this;
                }

                @Override
                public long getExpireDate() {
                    return event.getExpireDate();
                }
            });
            this.sentRequests.put(user.getName(), user);
            return true;
        } else {
            prepareMessage(Main.getInstance().getConfiguration().COMMAND_TELEPORT_ALREADY_REQUEST).with("playerName", user.getName()).send();
            return false;
        }
    }

    public long getTeleportDelay() {
        return teleportDelay;
    }

    public NewerOptional<Long> getTeleportDateExpire(User user) {
        TeleportHandler handler;
        return NewerOptional.ofNullable((handler = requests.get(user.getName())) == null ? null : handler.getExpireDate());
    }

    public int getTeleportRequests() {
        return sentRequests.size();
    }

    public Map<String, TeleportHandler> getRequests() {
        return requests;
    }

    public List<TeleportHandler> getAllTeleportRequests() {
        return new ArrayList<>(requests.values());
    }

    public void acceptRequest(User user) {
        fetchTeleportRequest(user.getName()).ifPresent(teleportHandler -> {

            new UserAcceptTeleportRequestEvent() {
                @Override
                public List<User> getAcceptedUsers() {
                    return Collections.singletonList(user);
                }

                @Override
                public Location getTeleportLocation() {
                    return player.getLocation();
                }

                @Override
                public long getDelayTime() {
                    return Main.getInstance().getConfiguration().TELEPORT_EXPIRE;
                }

                @Override
                public User getUser() {
                    return User.this;
                }
            }.call();

            requests.remove(user.getName());
            user.getSentRequests().remove(user.getName());
        });
    }

    public Map<String, User> getSentRequests() {
        return sentRequests;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public CommandSender getCommandSender() {
        return player;
    }

    public boolean hasPermission(String s) {
        return player != null && player.hasPermission(s);
    }

    public NewerOptional<PlayerPacket> getPlayerPacket() {
        return NewerOptional.ofNullable(playerPacket);
    }

    public void setPlayerPacket(PlayerPacket playerPacket) {
        this.playerPacket = playerPacket;
    }

    public void setAbsent(Function<User, AntiLogout> antyLogoutFunction, Function<User, Actionbar> actionbarFunction, Function<User, ScoreBoard> scoreBoardFunction, Function<User, TabList> tabListFunction, Function<User, Teleport> teleportFunction, Function<User, PlayerPacket> packetFunction) {
        if(antiLogout == null) antiLogout = antyLogoutFunction.apply(this);
        if(actionbar == null) actionbar = actionbarFunction.apply(this);
        if(scoreBoard == null) scoreBoard = scoreBoardFunction.apply(this);
        if(tabList == null) tabList = tabListFunction.apply(this);
        if(teleport == null) teleport = teleportFunction.apply(this);
        if(playerPacket == null) playerPacket = packetFunction.apply(this);
    }

    public String getGuildPrefix(Guild guild){
        Configuration configuration = Main.getInstance().getConfiguration();
        if(guild == null) return "";
        else if(this.guild == null) return configuration.PREFIX_GUILD_OTHER.replace("%tag%", guild.getTag());
        else if(this.guild.isEnemy(guild)) return configuration.PREFIX_GUILD_WAR.replace("%tag%", guild.getTag());
        else if(this.guild.getAllies().contains(guild)) return configuration.PREFIX_GUILD_ALLY.replace("%tag%", guild.getTag());
        else if(this.guild.equals(guild)) return configuration.PREFIX_GUILD_OWN.replace("%tag%", guild.getTag());
        else return configuration.PREFIX_GUILD_OTHER.replace("%tag%", guild.getTag());
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;

        Main.getInstance().getUserCache().getOnlineCache().remove(name);

        if(online)
            Main.getInstance().getUserCache().getOnlineCache().put(name, this);
    }

    private static final Class<?> EntityPlayerClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "EntityPlayer");

    private static final Optional<Field> pingField = ReflectionHelper.getField(EntityPlayerClass, "ping");

    public int getPing() {
        Object ep;
        if((ep = playerPacket.getEntityPlayer().orElse(null)) == null) return -100;
        Object finalEp = ep;
        return (int) LambdaBypass.getOptional(pingField, field -> ReflectionHelper.getValue(field, finalEp).get()).getObject();
    }

    public boolean isIncognito() {
        return incognito;
    }

    public void setIncognito(boolean incognito) {
        this.incognito = incognito;
    }

    public void sendMessage(String s) {
        if(player != null) player.sendMessage(ColorHelper.translateColors(s));
    }

    public void sendMessage(TextComponent textComponent){
        if(player != null) player.spigot().sendMessage(textComponent);
    }

    public void sendMessage(ChatMessage chatMessage){
        if(player != null) player.spigot().sendMessage(chatMessage.build());
    }

    public long getTurboExp() {
        return turboExp;
    }

    public void setTurboExp(long turboExp) {
        this.turboExp = turboExp;
    }

    public long getTurboDrop() {
        return turboDrop;
    }

    public void setTurboDrop(long turboDrop) {
        this.turboDrop = turboDrop;
    }

    public void giveItem(ItemStack... itemStacks){
        InventoryHelper.giveItem(player, itemStacks, itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
    }

    public void giveItem(Collection<ItemStack> itemStacks){
        giveItem(itemStacks.toArray(new ItemStack[]{}));
    }

    public void removeItem(ItemStack itemStack, int amount){
        InventoryHelper.removeItems(player, itemStack, amount);
    }

    public void removeItem(ItemStack itemStack){
        removeItem(itemStack, itemStack.getAmount());
    }

    public Map<ItemStack, Integer> getDeposit() {
        return deposit;
    }

    public int getAmount(ItemStack depositMaterial){
        ItemStack item = deposit.keySet().stream().filter(itemStack -> itemStack.isSimilar(depositMaterial)).findFirst().orElse(null);
        if (item == null) return 0;
        return deposit.get(item);
    }

    public void deposit(ItemStack itemStack, int amount){
        if(player == null) return;

        int itemAmount = InventoryHelper.getAmountOf(player, itemStack);

        ItemStack rest = new ItemStack(itemStack);
        rest.setAmount(itemAmount - amount);

        removeItem(itemStack, itemAmount - amount);
        giveItem(rest);

        ItemStack cachedItem = deposit.keySet().stream().filter(stack -> stack.isSimilar(itemStack)).findFirst().orElse(null);

        Object o = deposit.get(cachedItem == null ? itemStack : cachedItem);
        if(o == null) deposit.put(cachedItem, amount);
        else {
            deposit.remove(cachedItem);
            deposit.put(cachedItem, (int)o + amount);
        }
    }

    public Optional<ItemStack> withdraw(ItemStack itemStack, int amount){
        if(getAmount(itemStack) >= amount){
            itemStack.setAmount(amount);

            ItemStack cachedItem = deposit.keySet().stream().filter(stack -> stack.isSimilar(itemStack)).findFirst().orElse(null);
            Object o = deposit.get(cachedItem == null ? itemStack : cachedItem);
            if(o == null) return Optional.empty();
            else {
                deposit.remove(cachedItem);
                deposit.put(cachedItem, (int)o - amount);
                giveItem(itemStack);
                return Optional.of(itemStack);
            }
        }
        return Optional.empty();
    }

    public Optional<InventoryHolder> getInventoryHolder() {
        return Optional.ofNullable(inventoryHolder);
    }

    public long addExp() {
        return exp++;
    }

    private static final Class<?> PacketPlayOuTtitleClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutTitle");
    private static final Class<?> EnumTitleActionClass = PacketPlayOuTtitleClass.getDeclaredClasses()[0];

    private static final Enum<?> TITLE = (Enum<?>) EnumTitleActionClass.getEnumConstants()[0];
    private static final Enum<?> SUBTITLE = (Enum<?>) EnumTitleActionClass.getEnumConstants()[1];
    private static final Enum<?> TIMES = (Enum<?>) EnumTitleActionClass.getEnumConstants()[2];

    private static final Optional<Field> titleType = ReflectionHelper.getField(PacketPlayOuTtitleClass, "a");
    private static final Optional<Field> message = ReflectionHelper.getField(PacketPlayOuTtitleClass, "b");
    private static final Optional<Field> fadeIn = ReflectionHelper.getField(PacketPlayOuTtitleClass, "c");
    private static final Optional<Field> stay = ReflectionHelper.getField(PacketPlayOuTtitleClass, "d");
    private static final Optional<Field> fadeOut = ReflectionHelper.getField(PacketPlayOuTtitleClass, "e");

    public void sendTitle(String s, String s1, int i, int i1, int i2) {

        AtomicReference<Object> reference = new AtomicReference<>(ReflectionHelper.newInstance(PacketPlayOuTtitleClass));

        LambdaBypass.invokeOptional(titleType, field -> ReflectionHelper.setValue(field, reference.get(), TIMES));
        LambdaBypass.invokeOptional(fadeIn, field -> ReflectionHelper.setValue(field, reference.get(), i));
        LambdaBypass.invokeOptional(stay, field -> ReflectionHelper.setValue(field, reference.get(), i1));
        LambdaBypass.invokeOptional(fadeOut, field -> ReflectionHelper.setValue(field, reference.get(), i2));

        playerPacket.sendPacket(reference::get);

        reference.set(ReflectionHelper.newInstance(PacketPlayOuTtitleClass));

        LambdaBypass.invokeOptional(titleType, field -> ReflectionHelper.setValue(field, reference.get(), TITLE));
        LambdaBypass.invokeOptional(message, field -> ReflectionHelper.setValue(field, reference.get(), ((Object[]) ChatTransformer.fromString(ColorHelper.translateColors(s)))[0]));

        playerPacket.sendPacket(reference::get);

        reference.set(ReflectionHelper.newInstance(PacketPlayOuTtitleClass));

        LambdaBypass.invokeOptional(titleType, field -> ReflectionHelper.setValue(field, reference.get(), SUBTITLE));
        LambdaBypass.invokeOptional(message, field -> ReflectionHelper.setValue(field, reference.get(), ((Object[]) ChatTransformer.fromString(ColorHelper.translateColors(s1)))[0]));

        playerPacket.sendPacket(reference::get);
    }

    public void setInventoryHolder(InventoryHolder inventoryHolder) {
        this.inventoryHolder = inventoryHolder;
    }

    public JsonObject getProperty() {
        return property;
    }

    public void setProperty(JsonObject property) {
        this.property = property;
    }

    public long getChatDelay() {
        return chatDelay;
    }

    public void setChatDelay(long chatDelay) {
        this.chatDelay = chatDelay;
    }

    public void setAntyLogout(AntiLogout antiLogout) {
        this.antiLogout = antiLogout;
    }

    public Map<Location, Generator> getGeneratorMap() {
        return generatorMap;
    }

    public Material getLastGeneratorMaterial() {
        return lastGeneratorMaterial;
    }

    public void setLastGeneratorMaterial(Material lastGeneratorMaterial) {
        this.lastGeneratorMaterial = lastGeneratorMaterial;
    }

    @Override
    public boolean isEnabled(Class<?> classOption) {
        AtomicBoolean atomicBoolean;
        return (atomicBoolean = optionMap.get(classOption)) == null || atomicBoolean.get();
    }

    @Override
    public void registerOption(Class<?>... classOptions) {
        for (Class<?> classOption : classOptions) {
            optionMap.putIfAbsent(classOption, new AtomicBoolean(true));
        }
    }

    @Override
    public void changeOption(Class<?> classOption, boolean enable) {
        AtomicBoolean atomicBoolean = optionMap.get(classOption);
        if(atomicBoolean == null) return;
        atomicBoolean.set(enable);
    }

    @Override
    public boolean changeOption(Class<?> classOption) {
        AtomicBoolean atomicBoolean = optionMap.get(classOption);
        if(atomicBoolean == null) return false;
        atomicBoolean.set(!atomicBoolean.get());
        return atomicBoolean.get();
    }

    public void chat(String message) {
        if(player == null) return;
        player.chat(message);
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public long getJoin() {
        return join;
    }

    public void setJoin(long join) {
        this.join = join;
    }

    public long getQuit() {
        return quit;
    }

    public void setQuit(long quit) {
        this.quit = quit;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setTimeSpend(long timeSpend) {
        this.timeSpend = timeSpend;
    }

    public void updateTimeSpend(){
        this.timeSpend += (System.currentTimeMillis() - join);
    }

    public long getTimeSpend() {
        return timeSpend + (System.currentTimeMillis() - join);
    }

    public User getLastReceiver() {
        return lastReceiver;
    }

    public void setLastReceiver(User lastReceiver) {
        this.lastReceiver = lastReceiver;
    }

    public List<User> getBlocked() {
        return blocked;
    }

    public boolean isBlocked(User user){
        return blocked.contains(user);
    }

    public boolean block(User user){
        if(isBlocked(user)) return false;
        blocked.add(user);
        return true;
    }

    public boolean unblock(User user){
        if(!isBlocked(user)) return false;
        blocked.add(user);
        return true;
    }

    public boolean isToggle(ToggleType message) {
        return toggleTypes.contains(message);
    }

    public List<ToggleType> getToggleTypes() {
        return toggleTypes;
    }

    public void disableToggle(ToggleType toggleType){
        toggleTypes.remove(toggleType);
    }

    public void enableToggle(ToggleType toggleType){
        toggleTypes.add(toggleType);
    }

    public boolean isVanish() {
        return vanish;
    }

    public void setVanish(boolean vanish) {
        this.vanish = vanish;
    }

    public void updateVisibility(){
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (vanish && !p.hasPermission("sacore.vanish.donthide")) {
                p.hidePlayer(player);
            } else {
                p.showPlayer(player);
            }
        });
    }

    public enum ToggleType {
        MESSAGE,
        CHAT,
        DROP
    }
}