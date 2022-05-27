package pl.saidora.core.model.impl;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.Map;

public class AntiLogout {

    private final User user;

    private final Map<User, AtomicDouble> damageMap = new HashMap<>();
    private long end, start;

    public AntiLogout(User user){
        this.user = user;
    }

    public void damage(User user, double damage){
        AtomicDouble atomicDouble = damageMap.getOrDefault(user, new AtomicDouble());
        atomicDouble.set(atomicDouble.doubleValue() + damage);
        damageMap.putIfAbsent(user, atomicDouble);
    }

    public double getDamage(User user){
        return damageMap.getOrDefault(user, new AtomicDouble()).doubleValue();
    }

    public User getUser() {
        return user;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        start = System.currentTimeMillis();
        this.end = System.currentTimeMillis() + end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
}
