package pl.saidora.core.helpers;

import org.bukkit.Location;
import pl.saidora.core.model.impl.guild.Guild;

public class RegionHelper {

    public static double getDistance(Location location, Guild guild){
        return getDistance(location, guild.getMinX(), guild.getMinZ(), guild.getMaxX(), guild.getMaxZ());
    }

    public static double getDistance(Location location, double minX, double minZ, double maxX, double maxZ){
        double x = location.getX();
        double z = location.getZ();

        double distWest = Math.abs(minX - x);
        double distEast = Math.abs(maxX - x);

        double distNorth = Math.abs(minZ - z);
        double distSouth = Math.abs(maxZ - z);

        double distX = Math.min(distWest, distEast);
        double distZ = Math.min(distNorth, distSouth);

        return Math.min(distX, distZ);
    }

    public static boolean isIn(Location location, Guild guild){
        return guild.getMinX() < location.getX() && guild.getMinZ() < location.getZ() && guild.getMaxX() > location.getX() && guild.getMaxZ() > location.getZ();
    }
}
