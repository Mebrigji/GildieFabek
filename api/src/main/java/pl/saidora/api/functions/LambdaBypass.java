package pl.saidora.api.functions;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface LambdaBypass<T> {

    static <T, K> LambdaBypass<K> get(T t, Function<T, K> function){
        return () -> function.apply(t);
    }

    static <T, K> LambdaBypass<K> getOptional(Optional<T> optional, Function<T, K> function){
        return () -> function.apply(optional.orElse(null));
    }

    static <T> void invokeOptional(Optional<T> optional, Consumer<T> consumer){
        consumer.accept(optional.orElse(null));
    }

    static <T, K> void invoke(T t, Function<T, K> function){
        function.apply(t);
    }

    T getObject();

}
