package pl.saidora.core.model;

import pl.saidora.core.model.impl.User;

public interface GuildHistory {

    long getDate();

    User who();

    String description();

}
