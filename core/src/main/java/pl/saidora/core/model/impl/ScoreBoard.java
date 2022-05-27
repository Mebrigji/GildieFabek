package pl.saidora.core.model.impl;

import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.ReflectionHelper;
import pl.saidora.api.helpers.StringHelper;
import pl.saidora.core.events.ScoreboardUpdateEvent;
import pl.saidora.core.model.impl.guild.Guild;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ScoreBoard {

    private static final Class<?> ScoreboardClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "Scoreboard");
    private static final Class<?> ScoreboardObjectiveClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "ScoreboardObjective");
    private static final Class<?> PacketPlayOutScoreboardObjectiveClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutScoreboardObjective");
    private static final Class<?> PacketPlayOutScoreboardDisplayObjectiveClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutScoreboardDisplayObjective");
    private static final Class<?> PacketPlayOutScoreboardScoreClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutScoreboardScore");
    private static final Class<?> IScoreboardCriteriaClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "IScoreboardCriteria");
    private static final Class<?> IScoreboardCriteriaClass_EnumScoreboardHealthDisplayClass = IScoreboardCriteriaClass.getDeclaredClasses()[0];
    private static final Class<?> ScoreboardServerClass_ActionClass = PacketPlayOutScoreboardScoreClass.getDeclaredClasses()[0];
    private static final Class<?> ScoreboardScoreClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "ScoreboardScore");

    private static final Optional<Method> setScore = ReflectionHelper.getMethod(ScoreboardScoreClass, "setScore", int.class);

    private static final Optional<Method> setDisplayName = ReflectionHelper.getMethod(ScoreboardObjectiveClass, "setDisplayName", String.class);

    private static final Optional<Method> getObjective = ReflectionHelper.getMethod(ScoreboardClass, "getObjective", String.class);
    private static final Optional<Method> registerObjective = ReflectionHelper.getMethod(ScoreboardClass, "registerObjective",
            String.class,
            IScoreboardCriteriaClass);

    private static final Object DUMMY = ReflectionHelper.getValue(ReflectionHelper.getField(IScoreboardCriteriaClass, "b").get(), null).get();

    private static final Optional<Constructor<?>> PacketPlayOutScoreboardObjectiveConstructor =
            ReflectionHelper.getConstructor(PacketPlayOutScoreboardObjectiveClass, ScoreboardObjectiveClass, int.class);

    private static final Optional<Constructor<?>> PacketPlayOutScoreboardDisplayObjectiveConstructor =
            ReflectionHelper.getConstructor(PacketPlayOutScoreboardDisplayObjectiveClass, int.class, ScoreboardObjectiveClass);

    private static final Optional<Constructor<?>> PacketPlayOutScoreboardScoreConstructor =
            ReflectionHelper.getConstructor(PacketPlayOutScoreboardScoreClass, ScoreboardScoreClass);

    private static final Optional<Constructor<?>> ScoreboardScoreConstructor = ReflectionHelper.getConstructor(ScoreboardScoreClass, ScoreboardClass, ScoreboardObjectiveClass, String.class);


    private final User user;

    private String displayName;
    private List<String> lines = new ArrayList<>();

    private Map<String, Function<User, Object>> replacements = new HashMap<>();
    private Map<String, Function<Guild, Object>> guildReplacements = new HashMap<>();

    private List<String> safedLines = new ArrayList<>();

    private Map<String, TimedMessage> messages = new HashMap<>();

    private Object minecraftScoreboard, scoreboardObjective;


    public ScoreBoard(User user){
        this.user = user;
    }


    private String translate(String old){
        AtomicReference<String> text = new AtomicReference<>(old);
        replacements.forEach((k, v) -> text.set(text.get().replace("%" + k + "%", v.apply(user).toString())));
        guildReplacements.forEach((k, v) -> text.set(text.get().replace("%" + k + "%", v.apply(user.getGuild().orElse(null)).toString())));
        return ColorHelper.translateColors(text.get());
    }

    public void addTimedMessage(String key, TimedMessage message){
        messages.remove(key);
        this.messages.put(key, message);
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


    public User getUser() {
        return user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    protected void prepare(){
        if(minecraftScoreboard == null)
            minecraftScoreboard = ReflectionHelper.newInstance(ScoreboardClass);

        scoreboardObjective = LambdaBypass.getOptional(getObjective, method -> ReflectionHelper.invoke(method, minecraftScoreboard, "sa_core")).getObject();

        if(scoreboardObjective == null)
            scoreboardObjective = LambdaBypass.getOptional(registerObjective, method -> ReflectionHelper.invoke(method, minecraftScoreboard, "sa_core", DUMMY)).getObject();

        if(displayName == null || displayName.isEmpty() || lines == null || lines.isEmpty()) return;

        LambdaBypass.invokeOptional(setDisplayName, method -> ReflectionHelper.invoke(method, scoreboardObjective, ColorHelper.translateColors(translate(displayName))));
    }

    public void send() {
        if(displayName == null || displayName.isEmpty()) displayName = " ";
        if(lines.isEmpty() && messages.isEmpty()) return;

        user.getPlayerPacket().ifPresent(playerPacket -> {
            ScoreboardUpdateEvent event = new ScoreboardUpdateEvent(user);
            event.call();

            if(event.isCancelled()) return;

            prepare();

            PacketPlayOutScoreboardObjectiveConstructor.ifPresent(constructor -> playerPacket.addPacket(() -> ReflectionHelper.invoke(constructor, scoreboardObjective, 1)));

            if(!event.isRemovePacket()){
                PacketPlayOutScoreboardObjectiveConstructor.ifPresent(constructor -> playerPacket.addPacket(() -> ReflectionHelper.invoke(constructor, scoreboardObjective, 0)));
                PacketPlayOutScoreboardDisplayObjectiveConstructor.ifPresent(constructor -> playerPacket.addPacket(() -> ReflectionHelper.invoke(constructor, 1, scoreboardObjective)));
                AtomicInteger lastI = new AtomicInteger();
                for (int i = 0; i < lines.size(); i++) {
                    int finalI = i;
                    lastI.set(i);
                    Object score = LambdaBypass.getOptional(ScoreboardScoreConstructor,
                            o -> ReflectionHelper
                                    .invoke(o, minecraftScoreboard, scoreboardObjective, StringHelper.setMaxSize(ColorHelper.translateColors(translate(lines.get(finalI))), 40)))
                            .getObject();
                    setScore.ifPresent(method -> ReflectionHelper.invoke(method, score, -finalI));
                    PacketPlayOutScoreboardScoreConstructor.ifPresent(constructor -> playerPacket.addPacket(() -> ReflectionHelper.invoke(constructor, score)));
                }

                new HashMap<>(messages).forEach((key, message) -> {
                    if(message.getExpire() > System.currentTimeMillis()){
                        Object score = LambdaBypass.getOptional(ScoreboardScoreConstructor,
                                        o -> ReflectionHelper
                                                .invoke(o, minecraftScoreboard, scoreboardObjective, StringHelper.setMaxSize(ColorHelper.translateColors(message.getUpdateFunction().apply(user)), 40)))
                                .getObject();
                        setScore.ifPresent(method -> ReflectionHelper.invoke(method, score, -lastI.getAndIncrement()));
                        PacketPlayOutScoreboardScoreConstructor.ifPresent(constructor -> playerPacket.addPacket(() -> ReflectionHelper.invoke(constructor, score)));
                    } else messages.remove(key);
                });

            }

            playerPacket.send();
        });
    }
}