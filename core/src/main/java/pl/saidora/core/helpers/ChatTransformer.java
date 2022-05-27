package pl.saidora.core.helpers;

import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.Optional;

public class ChatTransformer {

    private static final Class<?> IChatBaseComponentClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "IChatBaseComponent");
    private static final Class<?> CraftChatMessageClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.CRAFT_BUKKIT, "util.CraftChatMessage");

    private static final Optional<Method> fromString = ReflectionHelper.getMethod(CraftChatMessageClass, "fromString", String.class, boolean.class);


    public static Object fromString(String text, boolean keepNewLines){
        return LambdaBypass.getOptional(fromString, method -> ReflectionHelper.invoke(method, CraftChatMessageClass, text, keepNewLines)).getObject();
    }

    public static Object fromString(String text){
        return fromString(text, false);
    }

}
