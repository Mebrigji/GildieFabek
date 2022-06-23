package pl.saidora.core.cache;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import net.luckperms.api.model.group.Group;
import pl.saidora.core.model.impl.ChatProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Header("#####################################")
@Header("# Advanced plugin for guild servers #")
@Header("# Author: Saidora#2503              #")
@Header("#####################################")
public class ChatCache extends OkaeriConfig {

    private List<ChatProvider> cache = Arrays.asList(new ChatProvider("default", "&8[&7Gracz&8] &7%playerName%&8: &7%message%"));

    public ChatProvider get(Group group){
        Objects.requireNonNull(group, "Group cannot be null");
        return get(group.getName());
    }

    public ChatProvider get(String groupName){
        return cache.stream().filter(chatProvider -> chatProvider.getGroupName().equals(groupName)).findFirst().orElse(create(groupName, groupName + "&7 %playerName%&8:&f %message%"));
    }

    private ChatProvider create(String groupName, String format){
        ChatProvider provider = new ChatProvider(groupName, format);
        this.cache.add(provider);
        return provider;
    }

}
