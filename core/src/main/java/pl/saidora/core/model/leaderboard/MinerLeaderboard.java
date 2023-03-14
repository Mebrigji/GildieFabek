package pl.saidora.core.model.leaderboard;

import pl.saidora.core.model.Leaderboard;
import pl.saidora.core.model.impl.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MinerLeaderboard implements Leaderboard<User> {

    private final List<User> users = new ArrayList<>();

    @Override
    public String getLeaderboardName() {
        return "userLevel";
    }

    @Override
    public List<User> getValues() {
        return users;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

    @Override
    public void sort() {
        users.sort(Comparator.comparingInt(User::getLevel));
        Collections.reverse(users);
    }

    @Override
    public void addIfAbsent(User user) {
        if(!users.contains(user)) users.add(user);
    }
}
