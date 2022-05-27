package pl.saidora.core.commands;

import org.bukkit.Bukkit;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.api.helpers.SystemHelper;
import pl.saidora.core.Main;
import pl.saidora.core.configuration.Configuration;
import pl.saidora.core.factory.TabFactory;

@CommandInfo(name = "core", usage = "" +
        "/core reload - przeladuj wszystkie configi\n" +
        "/core info - statystyki dotyczace serwera\n" +
        "/core license - informacje dotyczace licencji" +
        "", permission = "sacore")
public class CoreCommand implements Command {
    public CoreCommand() {
    }

    @Override
    public void run(Executor executor) {
        String[] args = executor.getCommandArguments();
        if (args.length == 0) {
            executor.prepareMessage(commandInfo().usage()).send();
        } else if (args[0].equalsIgnoreCase("reload")) {

            Main.getInstance().reload();

            Configuration configuration = Main.getInstance().getConfiguration();
            TabFactory tabFactory = Main.getInstance().tabFactory;

            tabFactory.setHeader(configuration.TABLIST_HEADER);
            tabFactory.setFooter(configuration.TABLIST_FOOTER);
            tabFactory.setPing(configuration.TABLIST_PING);

            for (int i = 0; i < configuration.TABLIST_ROWS.size(); i++) {
                tabFactory.getRows().put(i, configuration.TABLIST_ROWS.get(i));
            }

            Main.getInstance().getOnlineUsers().values().forEach(tabFactory::setTab);
            executor.prepareMessage("&7Pliki konfiguracyjne zostaly przeladowane.").send();
        } else if (args[0].equalsIgnoreCase("info")) {
            executor.prepareMessage("&8[!] &7Serwer:\n" +
                            "&8* &7Pamiec ram:\n" +
                            " &8- &7Wolna: &d%freeRam% mb.\n" +
                            " &8- &7Calkowita: &d%totalRam% mb.\n" +
                            " &8- &7Uzywana: &d%usedRam% mb.\n" +
                            " &8- &7Maksymalna: &d%maxRam% mb.\n" +
                            "&8* &7Uzycie procesora: &d%cpuUsed%%\n" +
                            "&8* Statystyki:\n" +
                            " &8- &7Gracze: &d%players%/%maxPlayers%\n" +
                            " &8- &7Gildie: &d%guilds%")
                    .with("freeRam", SystemHelper.getFreeRam())
                    .with("totalRam", SystemHelper.getTotalRam())
                    .with("usedRam", SystemHelper.getUsageRam())
                    .with("maxRam", SystemHelper.getMaxRam())
                    .with("cpuUsed", SystemHelper.getCpuUsage())
                    .with("players", Main.getInstance().getOnlineUsers().size())
                    .with("maxPlayers", Bukkit.getMaxPlayers())
                    .with("guilds", 0)
                    .send();
        } else if (args[0].equalsIgnoreCase("license")) {
            executor.prepareMessage("&8[!] &7Stan licencji: &d%bought%\n" +
                            "&7Wygasa: &dNigdy")
                    .with("bought", Main.getInstance().getCl().isValidSimple() ? "Zakupiono" : "Brak licencji")
                    .send();
        }
    }
}
