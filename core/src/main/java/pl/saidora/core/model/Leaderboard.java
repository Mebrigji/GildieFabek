package pl.saidora.core.model;

import java.util.List;

public interface Leaderboard<V> {

    String getLeaderboardName();

    List<V> getValues();

    Class<V> getType();

    void sort();

    void addIfAbsent(V v);

    default V getByPosition(int position){
        return getValues().size() > position ? getValues().get(position) : null;
    }

    default int getPosition(V v){
        int pos = 0;
        for (V value : getValues()) {
            if(value.equals(v)) return ++pos;
            else ++pos;
        }
        return ++pos;
    }

}
