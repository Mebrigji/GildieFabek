package pl.saidora.core.commands;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.api.helpers.TimeHelper;
import pl.saidora.core.Main;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;

import java.util.Date;

@CommandInfo(name = "info", usage = "/info [playerName]", permission = "sacore.info")
public class InfoCommand implements Command {
    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if(args.length == 0) executor.sendMessage(usage());
        else Main.getInstance().getUserCache().findByName(args[0], false).ifPresentOrElse(user -> {
            executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_INFO)
                    .with("userName", user.getName())
                    .with("ip", user.getInetSocketAddress().getHostString())
                    .with("last-join", new Date(user.getJoin()))
                    .with("last-quit", new Date(user.getQuit()))
                    .with("guild", user.hasGuild() ? user.getGuild().get().getTag() : "Brak")
                    .with("money", user.getMoney())
                    .with("groupName", user.getGroupName())
                    .with("time", new TimeHelper(user.getTimeSpend()).simpleFormat())
                    .withPreparingComponent("ip-hidden", textComponent -> {
                        textComponent.setText("[IP-ADRESS]");
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColorHelper.translateColors("&7IP Adress: &d" + user.getInetSocketAddress().getHostString()))));
                    })
                    .send();
        }, () -> executor.prepareMessage(Main.getInstance().getConfiguration().COMMAND_INFO_INVALID).with("userName", args[0]).send());
    }
}
