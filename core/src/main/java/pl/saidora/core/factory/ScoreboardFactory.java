package pl.saidora.core.factory;

import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ScoreboardFactory {

    private String displayName;
    private List<String> lines = new ArrayList<>();

    private Map<String, Function<User, Object>> replacements = new HashMap<>();
    private Map<String, Function<Guild, Object>> guildReplacements = new HashMap<>();

    public void addReplacement(String key, Function<User, Object> function){
        replacements.put(key, function);
    }

    public void addGuildReplacement(String key, Function<Guild, Object> function){
        guildReplacements.put(key, function);
    }

    public void setScoreboard(User user){
        user.getScoreboard().ifPresent(scoreBoard -> {
            scoreBoard.setDisplayName(displayName);
            scoreBoard.setLines(lines);
            scoreBoard.addGuildReplacements(guildReplacements);
            scoreBoard.addReplacements(replacements);
        });
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public Map<String, Function<User, Object>> getReplacements() {
        return replacements;
    }

    public void setReplacements(Map<String, Function<User, Object>> replacements) {
        this.replacements = replacements;
    }

    public Map<String, Function<Guild, Object>> getGuildReplacements() {
        return guildReplacements;
    }

    public void setGuildReplacements(Map<String, Function<Guild, Object>> guildReplacements) {
        this.guildReplacements = guildReplacements;
    }

}
