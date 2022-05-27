package pl.saidora.api.functions;

public interface BiReplaceFunction<K, V> {

    Object accept(K key, V value);

}
