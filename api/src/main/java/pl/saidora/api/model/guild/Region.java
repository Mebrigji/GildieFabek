package pl.saidora.api.model.guild;

import org.bukkit.Location;

public interface Region {

    double getDistance(Location location);

    boolean isIn(Location location);

    Location getCenter();

    void updateCorners();

    void setSize(int size);

    int getSize();

}
