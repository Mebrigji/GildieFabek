package pl.saidora.core.model;

import java.util.List;

public interface Leaderboard<V> {

    String getLeaderboardName();

    List<V> getValues();

    void sort();

    void addIfAbsent(V v);

    default V getByPosition(int position){
        return getValues().size() > position ? getValues().get(position) : null;
    }

    default int getPosition(V v){
        for (int i = 0; i < getValues().size(); i++) {
            if(getValues().get(i).equals(v))
                return i;
        }
        return 0;
    }

}
