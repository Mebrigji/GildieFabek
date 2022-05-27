package pl.saidora.core.model.impl;

import java.util.function.Function;

public class TimedMessage {
    private long expire;

    private Function<User, String> updateFunction;

    public TimedMessage(long expire, Function<User, String> updateFunction) {
        this.expire = expire == -1 ? -1 : System.currentTimeMillis() + expire;
        this.updateFunction = updateFunction;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public Function<User, String> getUpdateFunction() {
        return updateFunction;
    }

    public void setUpdateFunction(Function<User, String> updateFunction) {
        this.updateFunction = updateFunction;
    }
}