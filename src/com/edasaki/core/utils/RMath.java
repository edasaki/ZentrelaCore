package com.edasaki.core.utils;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class RMath {

    /**
     * Generates a random integer in the range [a, b].
     * @param a minimum
     * @param b maximum
     */
    public static int randInt(int a, int b) {
        return (int) (Math.random() * (b - a + 1) + a);
    }

    /**
     * Generates a random long in the range [a, b].
     * @param a minimum
     * @param b maximum
     */
    public static long randLong(long a, long b) {
        return (long) (Math.random() * (b - a + 1) + a);
    }

    /**
     * Generates a random double in the range [a, b].
     * @param a minimum
     * @param b maximum
     */
    public static double randDouble(double a, double b) {
        return Math.random() * (b - a) + a;
    }

    public static <E> E randObject(E[] obj) {
        return obj[(int) (Math.random() * obj.length)];
    }

    /**
     * Returns the Manhattan distance in the x-z plane between
     * locations a and b. 
     * @param a
     * @param b
     * @return distance between a and b.
     */
    public static double flatDistance(Location a, Location b) {
        if (!a.getWorld().equals(b.getWorld()))
            return Double.MAX_VALUE;
        double sum = 0;
        sum += Math.abs(a.getX() - b.getX());
        sum += Math.abs(a.getZ() - b.getZ());
        return sum;
    }

    public static ArrayList<Location> calculateVectorPath(Location start, Vector direction, int length, int spacing) {
        return calculateVectorPath(start, direction, length, spacing, false);
    }

    public static ArrayList<Location> calculateVectorPath(Location start, Vector direction, int length, int spacing, boolean ignoreBlock) {
        ArrayList<Location> locs = new ArrayList<Location>();
        Location loc = start;
        direction = direction.normalize();
        length *= spacing;
        direction = direction.multiply(1.0 / spacing);
        for (int k = 0; k < length; k++) {
            loc = loc.add(direction);
            if (!ignoreBlock && !RParticles.isAirlike(loc.getBlock()))
                break;
            locs.add(loc.clone());
        }
        return locs;
    }

    public static ArrayList<Entity> getNearbyEntities(Location loc, double radius) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
        int baseX, baseZ;
        baseX = loc.getChunk().getX();
        baseZ = loc.getChunk().getZ();
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                chunkList.add(loc.getWorld().getChunkAt(baseX + dx, baseZ + dz));
        for (Chunk chunk : chunkList) {
            for (Entity e : chunk.getEntities()) {
                Location eLoc = e.getLocation();
                if (e instanceof LivingEntity)
                    eLoc = eLoc.add(0, ((LivingEntity) e).getEyeHeight() * 0.75, 0);
                double x1 = eLoc.getX();
                double y1 = eLoc.getY();
                double z1 = eLoc.getZ();
                double x2 = loc.getX();
                double y2 = loc.getY();
                double z2 = loc.getZ();
                double xdiff = x1 - x2;
                double ydiff = Math.abs(y1 - y2) - 0.7;
                if (ydiff < 0)
                    ydiff = 0;
                double zdiff = z1 - z2;
                double sq = xdiff * xdiff + ydiff * ydiff + zdiff * zdiff;
                if (sq <= radius * radius)
                    list.add(e);
            }
        }
        return list;
    }

    public static ArrayList<Entity> getNearbyEntitiesCylinder(Location loc, double radius, double vertical) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
        int baseX, baseZ;
        baseX = loc.getChunk().getX();
        baseZ = loc.getChunk().getZ();
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                chunkList.add(loc.getWorld().getChunkAt(baseX + dx, baseZ + dz));
        for (Chunk chunk : chunkList) {
            for (Entity e : chunk.getEntities()) {
                Location eLoc = e.getLocation();
                if (e instanceof LivingEntity)
                    eLoc = eLoc.add(0, ((LivingEntity) e).getEyeHeight() * 0.75, 0);
                double x1 = eLoc.getX();
                double y1 = eLoc.getY();
                double z1 = eLoc.getZ();
                double x2 = loc.getX();
                double y2 = loc.getY();
                double z2 = loc.getZ();
                double xdiff = x1 - x2;
                double ydiff = Math.abs(y1 - y2);
                if (ydiff > vertical)
                    continue;
                double zdiff = z1 - z2;
                double sq = xdiff * xdiff + zdiff * zdiff;
                if (sq <= radius * radius)
                    list.add(e);
            }
        }
        return list;
    }

}
