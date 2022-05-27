package pl.saidora.core.handlers;

import pl.saidora.core.model.impl.User;

public interface TeleportHandler {

    User getTarget();

    User getUser();

    long getExpireDate();

}
