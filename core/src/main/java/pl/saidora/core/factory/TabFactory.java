package pl.saidora.core.factory;

import pl.saidora.core.model.impl.User;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.*;
import java.util.function.Function;

public class TabFactory {

    private List<String> header = new ArrayList<>(), footer = new ArrayList<>();
    private Map<Integer, String> rows = new HashMap<>();

    private Map<String, Function<User, Object>> replacements = new HashMap<>();
    private Map<String, Function<Guild, Object>> guildReplacements = new HashMap<>();

    private int ping;

    public Map<String, Function<User, Object>> getReplacements() {
        return replacements;
    }

    public Map<String, Function<Guild, Object>> getGuildReplacements() {
        return guildReplacements;
    }

    public void addReplacement(String name, Function<User, Object> function){
        this.replacements.put(name, function);
    }

    public void addGuildReplacement(String name, Function<Guild, Object> function){
        this.guildReplacements.put(name, function);
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<String> getFooter() {
        return footer;
    }

    public void setFooter(List<String> footer) {
        this.footer = footer;
    }

    public Map<Integer, String> getRows() {
        return rows;
    }

    public void setRows(Map<Integer, String> rows) {
        this.rows = rows;
    }

    public void setTab(User user){
        user.getTabList().ifPresent(tabList -> {
            tabList.setHeader(header);
            tabList.setFooter(footer);
            tabList.setRows(rows);
            tabList.setPing(u -> ping == -1 ? u.getPing() : ping);
            tabList.setFirst(true);
            tabList.addGuildReplacements(guildReplacements);
            tabList.addReplacements(replacements);
        });
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }
}
