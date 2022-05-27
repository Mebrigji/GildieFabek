package pl.saidora.core.events.engine;

import java.util.Set;
import java.util.function.Consumer;

public interface CallbackEvent<U> {

    U getEvent();

    Set<Consumer<U>> getCache();

    default void call(){
        getCache().forEach(consumer -> consumer.accept(getEvent()));
    }

}
