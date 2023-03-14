package pl.saidora.core.commands;

import pl.saidora.core.Main;
import pl.saidora.core.cache.AbyssCache;
import pl.saidora.core.commands.system.Command;
import pl.saidora.core.commands.system.CommandInfo;
import pl.saidora.core.commands.system.Executor;
import pl.saidora.core.commands.system.ExecutorType;
import pl.saidora.core.events.UserInventoryClickEvent;
import pl.saidora.core.model.impl.Abyss;
import pl.saidora.core.model.impl.CachedInventory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@CommandInfo(name = "abyss", usage = "/abyss", permission = "", aliases = "otchlan", executors = ExecutorType.PLAYER)
public class AbyssCommand implements Command {
    public AbyssCommand() {
    }

    @Override
    public void run(Executor executor) {
        Main.getInstance().getUserCache().findByName(executor.getName(), true).ifPresent(user -> {
            AbyssCache cache = Main.getInstance().getAbyssCache();
            if(!cache.isOpened()){
                user.sendMessage(Main.getInstance().getConfiguration().ABYSS_CLOSED);
                return;
            }
            AtomicReference<Abyss> page = new AtomicReference<>(cache.getPage(0));
            AtomicReference<CachedInventory> inventory = new AtomicReference<>(new CachedInventory(user, cache.prepareInventory(0)));
            inventory.get().whenClick(event -> {
                if(event.getSlot() >= 4*9 && event.getSlot() <= 5*9-1){
                    event.setBlockClick(true);
                    if(event.getSlot() == 4*9+3){
                        page.get().getPrevious().ifPresent(abyss -> {
                            page.set(abyss);
                            Consumer<UserInventoryClickEvent> clickEventConsumer = inventory.get().getEventConsumer();
                            inventory.set(new CachedInventory(user, cache.prepareInventory(abyss)));
                            inventory.get().whenClick(clickEventConsumer);
                            inventory.get().open();
                        });
                    } else if(event.getSlot() == 4*9+5){
                        page.get().getNext().ifPresent(abyss -> {
                            page.set(abyss);
                            Consumer<UserInventoryClickEvent> clickEventConsumer = inventory.get().getEventConsumer();
                            inventory.set(new CachedInventory(user, cache.prepareInventory(abyss)));
                            inventory.get().whenClick(clickEventConsumer);
                            inventory.get().open();
                        });
                    }
                }
            });
            inventory.get().open();
        });
    }
}
