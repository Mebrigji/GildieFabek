package pl.saidora.core.model.leaderboard;

import pl.saidora.core.model.Leaderboard;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GuildLeaderboard implements Leaderboard<Guild> {

    private final List<Guild> guilds = new ArrayList<>();

    @Override
    public String getLeaderboardName() {
        return "guildPoints";
    }

    @Override
    public List<Guild> getValues() {
        return guilds;
    }

    @Override
    public Class<Guild> getType() {
        return Guild.class;
    }

    @Override
    public void sort() {
        guilds.sort(Comparator.comparingInt(Guild::getPoints));
        Collections.reverse(guilds);
    }

    @Override
    public void addIfAbsent(Guild guild) {
        if(guilds.contains(guild)) return;
        guilds.add(guild);
    }
}
