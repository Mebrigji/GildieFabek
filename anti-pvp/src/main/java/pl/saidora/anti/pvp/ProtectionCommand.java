package pl.saidora.anti.pvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import pl.saidora.api.helpers.ColorHelper;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProtectionCommand extends Command {

    public ProtectionCommand(){
        super("protection");
        setAliases(Arrays.asList("ochrona"));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof ConsoleCommandSender){
            commandSender.sendMessage("Command is only useful for player.");
            return false;
        }
        AtomicBoolean atomicBoolean = Main.getProtectionMap().get(commandSender.getName());
        if(atomicBoolean == null) {
            atomicBoolean = new AtomicBoolean(true);
            Main.getProtectionMap().put(commandSender.getName(), atomicBoolean);
        }

        atomicBoolean.set(!atomicBoolean.get());
        if(atomicBoolean.get()) commandSender.sendMessage(ColorHelper.translateColors(Main.getInstance().getConfiguration().MESSAGE_ENABLE));
        else commandSender.sendMessage(ColorHelper.translateColors(Main.getInstance().getConfiguration().MESSAGE_DISABLE));
        return false;
    }
}
