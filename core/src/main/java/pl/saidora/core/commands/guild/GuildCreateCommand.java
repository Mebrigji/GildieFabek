package pl.saidora.core.commands.guild;

import pl.saidora.api.helpers.TimeHelper;
import pl.saidora.core.Main;
import pl.saidora.core.builder.MessageBuilder;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.model.impl.guild.Guild;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandInfo(name = "create", usage = "/g create [tag] [name]", permission = "", aliases = "zaloz")
public class GuildCreateCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        Main.getInstance().getUserCache().lookByName(executor.getName(), true).ifPresent(user -> {
            if(user.hasGuild()) {
                user.sendMessage("Has guild");
                return;
            }
            if(args.length == 3) {
                String tag = "", name = "";

                Matcher matcher;

                if (!(matcher = Pattern.compile("[A-Z]+").matcher(args[1])).matches() || matcher.group().length() > 4 || matcher.group().length() < 2) user.sendMessage(Main.getInstance().getConfiguration().GUILD_CREATE_WRONG_TAG);
                else tag = matcher.group();

                if (!(matcher = Pattern.compile("[a-zA-Z]+").matcher(args[2])).matches() || matcher.group().length() > 24 || matcher.group().length() < 6) user.sendMessage(Main.getInstance().getConfiguration().GUILD_CREATE_WRONG_NAME);
                else name = matcher.group();

                if (tag.isEmpty() || name.isEmpty()) return;

                Guild guild = Main.getInstance().getGuildCache().getGuildByTag(tag).orElse(null);
                if (guild != null) {
                    user.prepareMessage(Main.getInstance().getConfiguration().GUILD_CREATE_EXISTS).with("tag", tag).send();
                    return;
                }

                guild = Main.getInstance().getGuildCache().createGuild(tag, executor.asPlayer().get().getLocation(), user).join();
                guild.setName(name);
                Main.getInstance().getGuildCache().register(guild);

                user.prepareMessage(Main.getInstance().getConfiguration().GUILD_CREATE_EXECUTOR)
                        .with("tag", guild.getTag())
                        .with("name", guild.getName())
                        .with("membersMax", guild.getMaxMembers())
                        .with("expire", new TimeHelper(guild.getExpireGuild()).secondsToString())
                        .with("guilds", Main.getInstance().getGuildCache().getCache().size()).send();

                MessageBuilder builder = new MessageBuilder((Executor) null, Main.getInstance().getConfiguration().GUILD_CREATE_ALL);

                builder.with("playerName", user.getName());
                builder.with("tag", guild.getTag());
                builder.with("name", guild.getName());

                Main.getInstance().getOnlineUsers().forEach((userName, onlineUser) -> {
                    if(userName.equalsIgnoreCase(user.getName())) return;
                    builder.setUser(onlineUser);
                    builder.send();
                });
            } else {
                user.sendMessage("/g zaloz [tag] [nazwa-bez spacji]");
            }
        });
    }
}
