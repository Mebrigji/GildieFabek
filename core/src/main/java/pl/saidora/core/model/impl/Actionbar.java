package pl.saidora.core.model.impl;

import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Actionbar {

    private static final Class<?> PacketPlayOutChatClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PacketPlayOutChat");
    private static final Class<?> IChatBaseComponentClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "IChatBaseComponent");
    private static final Class<?> ChatSerializerClass = IChatBaseComponentClass.getDeclaredClasses()[0];

    private static final Optional<Constructor<?>> PacketPlayOutChatConstructor = ReflectionHelper.getConstructor(PacketPlayOutChatClass, IChatBaseComponentClass, byte.class);

    private static final Optional<Method> serializeMethod = ReflectionHelper.getMethod(ChatSerializerClass, "a", String.class);

    private final User user;

    public Actionbar(User user){
        this.user = user;
    }

    private final Map<String, TimedMessage> messageMap = new HashMap<>();

    public void addMessage(String id, TimedMessage message){
        messageMap.remove(id);
        messageMap.put(id, message);
    }

    public void send(){
        if(messageMap.isEmpty()) return;

        user.getPlayerPacket().ifPresent(playerPacket -> {
            StringBuilder builder = new StringBuilder();
            Map<String, TimedMessage> toRemove = new HashMap<>();

            messageMap.forEach((id, message) -> {
                if(message.getExpire() != -1 && message.getExpire() < System.currentTimeMillis()) {
                    toRemove.put(id, message);
                    return;
                }

                if(builder.toString().isEmpty()) builder.append(message.getUpdateFunction().apply(user));
                else builder.append(" &8:|:&r ").append(message.getUpdateFunction().apply(user));

            });

            toRemove.forEach((id, message) -> messageMap.remove(id));

            String text = ColorHelper.translateColors(builder.toString());

            playerPacket.addPacket(() -> LambdaBypass.getOptional(PacketPlayOutChatConstructor, constructor ->
                    ReflectionHelper.invoke(constructor,
                            LambdaBypass.getOptional(serializeMethod,
                                    method -> ReflectionHelper.invoke(method, ChatSerializerClass, String.format("{\"text\":\"%s\"}", text)))
                                    .getObject(), (byte)2)
                            )
                    .getObject());
            playerPacket.send();
        });
    }
}
