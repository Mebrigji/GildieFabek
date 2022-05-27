package pl.saidora.core.commands.guild;

import pl.saidora.api.helpers.TimeHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@CommandInfo(name = "info", usage = "/g info [tag]", permission = "", aliases = "informacje")
public class GuildInfoCommand implements Command {

    @Override
    public void run(Executor executor) {
        AtomicReference<Guild> reference = new AtomicReference<>();
        String[] args = executor.getCommandArguments();
        if (args.length == 2) {
            reference.set(Main.getInstance().getGuildCache().getGuildByTag(args[1]).orElse(null));
        } else if(executor.isPlayer()){
            executor.asPlayer().ifPresent(player -> reference.set(Main.getInstance().getGuildCache().getGuildByLocation(player.getLocation()).orElse(null)));
        } else {
            executor.sendMessage("G-INFO: Please set the guild tag.");
            return;
        }
        Guild guild = reference.get();
        if(guild != null) {
            executor.prepareMessage(Main.getInstance().getConfiguration().GUILD_INFO)
                    .with("tag", guild.getTag())
                    .with("name", guild.getName())
                    .with("points", guild.getPoints())
                    .with("kills", guild.getKills())
                    .with("deaths", guild.getDeaths())
                    .with("position", guild.getPosition())
                    .with("kdr", guild.getKDR())
                    .with("lives", guild.getLives())
                    .with("expire", new TimeHelper(guild.getExpireGuild()).secondsToString())
                    .with("war", guild.getWar() == null ? "Brak wojny" : Main.getInstance().getConfiguration().GUILD_INFO_WAR)
                    .with("online", guild.getOnlineUsers().size())
                    .with("total", guild.getUsers().size())
                    .with("users", guild.getUsers().stream().map(user -> user.isOnline() ? "&a" + user.getName() : "&c" + user.getName()).collect(Collectors.joining(", ")))
                    .with("attacker", guild.getWar() != null ? guild.getWar().getAttacker().getTag() : "---")
                    .with("defender", guild.getWar() != null ? guild.getWar().getDefenders().getWar() : "---")
                    .with("attacker-points", guild.getWar() != null ? guild.getWar().getAttacker_points() : 0)
                    .with("defender-points", guild.getWar() != null ? guild.getWar().getDefenders_points() : 0)
                    .with("start", guild.getWar() != null ? new TimeHelper(guild.getWar().getStart()).toString() : "---")
                    .with("end", guild.getWar() != null ? new TimeHelper(guild.getWar().getEnd()).toString() : "---")
                    .with("size", guild.getSize())
                    .send();
        } else {
            executor.sendMessage(Main.getInstance().getConfiguration().GUILD_INFO_EXISTS);
        }
    }
}
