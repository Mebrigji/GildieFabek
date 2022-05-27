package pl.saidora.core.model;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;

public class ChatProvider {

    private final String groupName;

    private Group group;
    private String format;

    public ChatProvider(String groupName, String format){
        this.groupName = groupName;
        this.format = format;
        this.group = LuckPermsProvider.get().getGroupManager().getGroup(groupName);
    }

    private ChatProvider(ChatProvider chatProvider){
        this.groupName = chatProvider.groupName;
        this.group = chatProvider.group;
        this.format = chatProvider.format;
    }

    public String getGroupName() {
        return groupName;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public ChatProvider clone(){
        return new ChatProvider(this);
    }
}
