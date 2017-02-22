package com.edasaki.core.utils;

import java.util.ArrayList;

import org.bukkit.Location;

public class RLocation {

    public static ArrayList<Location> findCastableLocs(Location origin, double radius, int count) {
        ArrayList<Location> locs = new ArrayList<Location>();
        if (radius < 0)
            radius = -radius;
        int cap = count * 2; // max number of spots to try to find valid locations at
        for (int k = 0; k < cap; k++) {
            double dx = RMath.randDouble(-radius, radius);
            double dz = RMath.randDouble(-radius, radius);
            origin.add(dx, 0, dz);
            for (int dy = 0; dy <= 3; dy++) {
                Location temp = origin.clone().add(0, dy, 0);
                if (checkLocation(temp)) {
                    locs.add(temp);
                    break;
                } else {
                    if (checkLocation(temp.add(0, -dy * 2, 0))) {
                        locs.add(temp);
                        break;
                    }
                }
            }
            if (locs.size() >= count)
                break;
        }
        if (locs.size() < count) {
            System.out.println("WARNING: findCastableLocs() at " + origin + " failed to find " + count + " locations in a radius of " + radius + ". " + locs.size() + " locations were found.");
        }
        return locs;
    }

    private static boolean checkLocation(Location loc) {
        Location lowerAir = loc.clone();
        Location upperAir = loc.clone().add(0, 1, 0);
        Location ground = loc.clone().add(0, -1, 0);
        return !lowerAir.getBlock().getType().isSolid() && !upperAir.getBlock().getType().isSolid() && ground.getBlock().getType().isSolid() && ground.getBlock().getType().isSolid();
    }

}
