package pl.saidora.core.helpers;

import java.util.function.Consumer;

@FunctionalInterface
public interface Overwrite<T> {

    default void apply(Consumer<T> consumer){
        consumer.accept(get());
    }

    T get();
}
