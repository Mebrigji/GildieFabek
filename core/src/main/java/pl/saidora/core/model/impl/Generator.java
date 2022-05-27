package pl.saidora.core.model.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import pl.saidora.core.Main;

public class Generator {

    private final Material generate;
    private long delay, countdown;

    private double health = 40;

    private Location location;

    private final User owner;

    public Generator(User user, Material material){
        this.owner = user;
        this.generate = material;
    }

    public Material getGenerate() {
        return generate;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getUpgradeCost() {
        return upgradeCost;
    }

    public void setUpgradeCost(long upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    private long upgradeCost;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getCountdown() {
        return countdown;
    }

    public void setCountdown(long countdown) {
        this.countdown = countdown;
    }

    public void regen(){
        this.countdown = delay;
        Main.getInstance().getGeneratorCache().getRegenMap().put(location, this);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public User getOwner() {
        return owner;
    }
}
