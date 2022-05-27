package pl.saidora.api.model.guild;

import org.bukkit.Location;

public interface GuildHearth {


    Location getCenter();

    Location getUp();

    Location getDown();


    void setCenter(Location location);

    void setUp(Location location);

    void setDown(Location location);


    void setSize(int size);

    int getSize();


    void updateHearthCorners();

    boolean isIn(Location location);

    double getDistanceToBorder(Location location);

}
