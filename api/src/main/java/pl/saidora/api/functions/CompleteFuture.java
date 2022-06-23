package pl.saidora.api.functions;

import lombok.SneakyThrows;

import java.util.function.Consumer;

public interface CompleteFuture<E> {

    E getElement();

    default CompleteFuture<E> check(boolean b){
        return b ? this : () -> null;
    }

    default CompleteFuture<E> check(boolean b, Runnable runnable){
        if(!b) runnable.run();
        return check(b);
    }

    default CompleteFuture<E> invoke(Consumer<E> e){
        if(getElement() == null) return this;
        e.accept(getElement());
        return this;
    }

    @SneakyThrows
    default CompleteFuture<E> await(long millis){
        Thread.sleep(millis);
        return this;
    }

    default void then(Runnable runnable){
        if(getElement() != null) runnable.run();
    }

}
