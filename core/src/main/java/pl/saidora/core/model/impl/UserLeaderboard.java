package pl.saidora.core.model.impl;

import pl.saidora.core.model.Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserLeaderboard implements Leaderboard<User> {

    private final List<User> users = new ArrayList<>();

    @Override
    public String getLeaderboardName() {
        return "user tops";
    }

    @Override
    public List<User> getValues() {
        return users;
    }

    @Override
    public void sort() {
        users.sort(Comparator.comparingInt(User::getPoints));
    }

    @Override
    public void addIfAbsent(User user) {
        if(users.contains(user)) return;
        users.add(user);
    }
}
