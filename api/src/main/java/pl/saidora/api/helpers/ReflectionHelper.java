package pl.saidora.api.helpers;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class ReflectionHelper {

    public static String getServerVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public enum PATH_FINDER {

        CRAFT_BUKKIT("org.bukkit.craftbukkit.%s."),
        MINECRAFT("net.minecraft.server.%s.");

        private String path;

        PATH_FINDER(String path){
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        private void setVersion(String version){
            this.path = String.format(path, version);
        }

        public static void setVersion(String version, PATH_FINDER path_finder){
            path_finder.setVersion(version);
        }
    }

    @SneakyThrows
    public static Class<?> getClass(PATH_FINDER path_finder, String name){
        return Class.forName(path_finder.path + name);
    }

    @SneakyThrows
    public static Optional<Method> getMethod(Class<?> methodClass, String methodName, Class<?>... params){
        Method method = null;
        for (Method declaredMethod : methodClass.getDeclaredMethods()) {
            declaredMethod.setAccessible(true);
            if(Arrays.equals(declaredMethod.getParameterTypes(), params) && methodName.equals(declaredMethod.getName()))
                method = declaredMethod;
            declaredMethod.setAccessible(false);
        }
        return Optional.ofNullable(method);
    }

    public static Optional<Field> getField(Class<?> clazz, String fieldName) {
        return getField(clazz, fieldName, null);
    }

    public static Optional<Field> getField(Class<?> fieldClass, String fieldName, Class<?> fieldType){
        Field field = null;
        for (Field declaredField : fieldClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if(declaredField.getName().equals(fieldName)){
                if(fieldType == null) {
                    field = declaredField;
                } else if(fieldType.equals(declaredField.getType()))
                    field = declaredField;
            }
            declaredField.setAccessible(false);
        }
        return Optional.ofNullable(field);
    }

    public static Optional<Constructor<?>> getConstructor(Class<?> constructorClass, Class<?>... params){
        Constructor<?> constructor = null;
        for (Constructor<?> declaredConstructor : constructorClass.getDeclaredConstructors()) {
            declaredConstructor.setAccessible(true);
            if(Arrays.equals(declaredConstructor.getParameterTypes(), params))
                constructor = declaredConstructor;
            declaredConstructor.setAccessible(false);
        }
        return Optional.ofNullable(constructor);
    }

    @SneakyThrows
    public static Optional<Object> getValue(Field field, Object value){
        return Optional.ofNullable(field.get(value));
    }

    @SneakyThrows
    public static void setValue(Field field, Object object, Object value){
        if(field.isAccessible())
            field.set(object, value);
        else {
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        }
    }

    public static void setValue(String fieldName, Object object, Object value){
        getField(object.getClass(), fieldName).ifPresent(field -> setValue(field, object, value));
    }

    @SneakyThrows
    public static <T> T invoke(Constructor<T> constructor, Object... params){
        return constructor.newInstance(params);
    }

    @SneakyThrows
    public static Object invoke(Method method, Object methodClass, Object... params){
        return method.invoke(methodClass, params);
    }

    @SneakyThrows
    public static Object newInstance(Class<?> clazz){
        return clazz.newInstance();
    }

}
