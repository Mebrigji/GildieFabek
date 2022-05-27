package pl.saidora.core.model.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.ReflectionHelper;
import pl.saidora.core.model.impl.guild.Guild;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TabList {

    protected static final String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";

    private static final Class<?> IChatBaseComponentClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "IChatBaseComponent");
    private static final Class<?> ChatSerializerClass = IChatBaseComponentClass.getDeclaredClasses()[0];
    private static final Class<?> PacketPlayOutPlayerListHeaderFooterClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutPlayerListHeaderFooter");
    private static final Class<?> PacketPlayOutPlayerInfoClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutPlayerInfo");
    private static final Class<?> PlayerInfoDataClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutPlayerInfo$PlayerInfoData");
    private static final Class<?> EnumPlayerInfoActionClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
    private static final Class<?> EnumGamemodeClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "WorldSettings$EnumGamemode");

    private static final Enum<?> ADD_PLAYER = (Enum<?>) EnumPlayerInfoActionClass.getEnumConstants()[0];
    private static final Enum<?> UPDATE_PLAYER = (Enum<?>) EnumPlayerInfoActionClass.getEnumConstants()[3];
    private static final Enum<?> REMOVE_PLAYER = (Enum<?>) EnumPlayerInfoActionClass.getEnumConstants()[4];
    private static final Enum<?> ENUMGAMEMODE = (Enum<?>) EnumGamemodeClass.getEnumConstants()[2];

    private static Constructor<?> PlayerInfoDataConstructor = PlayerInfoDataClass.getDeclaredConstructors()[0];

    private static final Optional<Method> serializeMethod = ReflectionHelper.getMethod(ChatSerializerClass, "a", String.class);


    private final User user;

    private boolean first;

    private GameProfile[] gameProfiles = new GameProfile[80];
    private List<String> header = new ArrayList<>(), footer = new ArrayList<>();

    private Function<User, Integer> pingFunction;

    private String signature = "fOIxZ2hxGiHVLEo3SWknEWWpgxfYG2TVvhuYHyGNQJcIo/MUxTAQDADat9+U3dRF3pf6iw1Xotv2hdn/4xSDRwltlTQ1ZMfCIVlUFrHBIEZrbwLqjx26YnRfkgFgw2XNsccrq09NgmvvO3H1Jd44wZ8JTMWSXyvgs6zBOx+yfPiQAln4zLVLspAzYdRcZ6SbsvNpJok8qJb5WG1z8H14JwAbyyMRXVzSy2dV5jgZZfaWHMYpBOVWJQGCmeRLFN0la6Lrqu4+xFbRXWzSGjrY8WSmze5nhxbeThscn3BTRBiwPGrhp5bjpZ20rwgQXrGi5aFEUwmcTetNfXUw5qpJZTbguGlMOogin8Wdsj3JMuPGbh24VwgPWpX8Mmc2+LZw2uGoD333VtOgj4oz1ZM9dP7ZtlokRZ9XYZUT0LwJiLqhjEPBqPGNEzQh8iX3aNimQ6+AknHdC47FGA9IhVkFcqgIKY/4S9E1IpuQpRwMhTWYwyWntLLdCHQTkHQkybKbIIMSbA5mddsyJaRDIvoD9htanH3JsykrMThTiFdJXjaCGqxS9Eq01UfsQGPArivM1QjikKI/6Ii9pLXKh0vP1OwIhjaVThYf+Bq2j6XkSVay4Gu73Q4jM8GVgvq1iO8cBRLSbNCDuEOGWTG7GEuMCt9e9PqPJInstI3k2CNcyvE=",
            texture = "ewogICJ0aW1lc3RhbXAiIDogMTYzMDc5MDU4MTY5NCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmODMxMGYxMTBmOWI2ZDgzNTE5ZmZlY2YwMzg4MWM5NDQ1NDhhYjQ3ZGE2YTBiYTk1MzZjNDY5MmYzZjYzIgogICAgfQogIH0KfQ==";

    private Map<Integer, String> rows = new HashMap<>(), safed = new HashMap<>();

    private Map<String, Function<User, Object>> replacements = new HashMap<>();
    private Map<String, Function<Guild, Object>> guildReplacements = new HashMap<>();

    public TabList(User user){
        this.user = user;
    }


    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return first;
    }

    private String replace(String old){
        AtomicReference<String> text = new AtomicReference<>(old);
        replacements.forEach((k, v) -> text.set(text.get().replace("%" + k + "%", v.apply(user).toString())));
        guildReplacements.forEach((k, v) -> text.set(text.get().replace("%" + k + "%", v.apply(user.getGuild().orElse(null)).toString())));
        return ColorHelper.translateColors(text.get());
    }

    private String getRowText(int row, boolean safed){
        return replace(safed ? this.safed.getOrDefault(row, "") : rows.getOrDefault(row, ""));
    }

    public void create() {
        if(!first) {
            update();
            return;
        }
        safed.clear();

        user.getPlayerPacket().ifPresent(playerPacket -> {
                    Object headerFooterPacket = ReflectionHelper.newInstance(PacketPlayOutPlayerListHeaderFooterClass);
                    ReflectionHelper.setValue("a", headerFooterPacket, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, null, String.format("{\"text\":\"%s\"}", LambdaBypass.get(header, o1 -> new StringBuilder(o1.stream().map(this::replace).collect(Collectors.joining("\n")))).getObject().toString()))).getObject());
                    ReflectionHelper.setValue("b", headerFooterPacket, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, null, String.format("{\"text\":\"%s\"}", LambdaBypass.get(footer, o1 -> new StringBuilder(o1.stream().map(this::replace).collect(Collectors.joining("\n")))).getObject().toString()))).getObject());

            Object packetPlayOutInfoPacket = ReflectionHelper.newInstance(PacketPlayOutPlayerInfoClass);
            List<Object> infoDataList = new ArrayList<>();

            for (int i = 0; i < 80; i++) {
                GameProfile profile = gameProfiles[i];
                if(profile == null){
                    profile = new GameProfile(UUID.fromString(String.format(UUID_PATTERN, StringUtils.leftPad(String.valueOf(i), 2, '0'))), "");
                    if(!signature.isEmpty() && !texture.isEmpty()){
                        profile.getProperties().removeAll("textures");
                        profile.getProperties().put("textures", new Property("textures", texture, signature));
                    }
                    gameProfiles[i] = profile;
                }

                String textLine = getRowText(i, false);
                safed.put(i, textLine);

                infoDataList.add(ReflectionHelper.invoke(PlayerInfoDataConstructor, packetPlayOutInfoPacket, profile, getPing(), ENUMGAMEMODE, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, null, String.format("{\"text\":\"%s\"}", textLine))).getObject()));
            }

            ReflectionHelper.setValue("a", packetPlayOutInfoPacket, ADD_PLAYER);
            first = false;

            ReflectionHelper.setValue("b", packetPlayOutInfoPacket, infoDataList);
            playerPacket.addPacket(() -> headerFooterPacket);
            playerPacket.addPacket(() -> packetPlayOutInfoPacket);
            playerPacket.send();
        });
    }

    public void update() {
        if(first) {
            create();
            return;
        }
        safed.clear();

        user.getPlayerPacket().ifPresent(playerPacket -> {
            Object headerFooterPacket = ReflectionHelper.newInstance(PacketPlayOutPlayerListHeaderFooterClass);

            ReflectionHelper.setValue("a", headerFooterPacket, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, null, String.format("{\"text\":\"%s\"}", LambdaBypass.get(header, o1 -> new StringBuilder(o1.stream().map(this::replace).collect(Collectors.joining("\n")))).getObject().toString()))).getObject());
            ReflectionHelper.setValue("b", headerFooterPacket, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, null, String.format("{\"text\":\"%s\"}", LambdaBypass.get(footer, o1 -> new StringBuilder(o1.stream().map(this::replace).collect(Collectors.joining("\n")))).getObject().toString()))).getObject());

            Object packetPlayOutInfoPacket = ReflectionHelper.newInstance(PacketPlayOutPlayerInfoClass);
            List<Object> infoDataList = new ArrayList<>();

            for (int i = 0; i < 80; i++) {
                GameProfile profile = gameProfiles[i];
                if(profile == null){
                    profile = new GameProfile(UUID.fromString(String.format(UUID_PATTERN, StringUtils.leftPad(String.valueOf(i), 2, '0'))), "");
                    if(!signature.isEmpty() && !texture.isEmpty()){
                        profile.getProperties().removeAll("textures");
                        profile.getProperties().put("textures", new Property("textures", texture, signature));
                    }
                    gameProfiles[i] = profile;
                }

                String textLine = getRowText(i, false);
                safed.put(i, textLine);

                infoDataList.add(ReflectionHelper.invoke(PlayerInfoDataConstructor, packetPlayOutInfoPacket, profile, getPing(), ENUMGAMEMODE, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, IChatBaseComponentClass, String.format("{\"text\":\"%s\"}", textLine))).getObject()));
            }

            ReflectionHelper.setValue("a", packetPlayOutInfoPacket, UPDATE_PLAYER);

            ReflectionHelper.setValue("b", packetPlayOutInfoPacket, infoDataList);
            playerPacket.addPacket(() -> headerFooterPacket);
            playerPacket.addPacket(() -> packetPlayOutInfoPacket);
            playerPacket.send();
        });
    }

    public void delete() {
        if(!first) {
            update();
            return;
        }
        safed.clear();

        user.getPlayerPacket().ifPresent(playerPacket -> {
            Object packetPlayOutInfoPacket = ReflectionHelper.newInstance(PacketPlayOutPlayerInfoClass);
            List<Object> infoDataList = new ArrayList<>();

            for (int i = 0; i < 80; i++) {
                String textLine = getRowText(i, true);
                infoDataList.add(ReflectionHelper.invoke(PlayerInfoDataConstructor, packetPlayOutInfoPacket, gameProfiles[i], getPing(), ENUMGAMEMODE, LambdaBypass.getOptional(serializeMethod, (o) -> ReflectionHelper.invoke(o, null, String.format("{\"text\":\"%s\"}", textLine))).getObject()));
            }

            ReflectionHelper.setValue("a", packetPlayOutInfoPacket, REMOVE_PLAYER);
            first = true;

            ReflectionHelper.setValue("b", packetPlayOutInfoPacket, infoDataList);
            playerPacket.addPacket(() -> ReflectionHelper.newInstance(PacketPlayOutPlayerListHeaderFooterClass));
            playerPacket.addPacket(() -> packetPlayOutInfoPacket);

            safed.clear();
        });
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getFooter() {
        return footer;
    }

    public Map<Integer, String> getRows() {
        return rows;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public void setFooter(List<String> footer) {
        this.footer = footer;
    }

    public void setRows(Map<Integer, String> rows) {
        this.rows = rows;
    }

    public void addReplacement(String key, Function<User, Object> function) {
        this.replacements.put(key, function);
    }

    public void addGuildReplacement(String key, Function<Guild, Object> function){
        this.guildReplacements.put(key, function);
    }

    public void addGuildReplacements(Map<String, Function<Guild, Object>> guildReplacements){
        this.guildReplacements.putAll(guildReplacements);
    }

    public void addReplacements(Map<String, Function<User, Object>> replacements){
        this.replacements.putAll(replacements);
    }

    public Map<String, Function<User, Object>> getReplacements() {
        return replacements;
    }

    public void setPing(Function<User, Integer> function) {
        this.pingFunction = function;
    }

    public int getPing() {
        return pingFunction.apply(user);
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
