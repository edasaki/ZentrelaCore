package com.edasaki.core.utils;

import org.bukkit.Chunk;

public final class ChunkWrapper {
    public int x, z;
    public String world;

    public ChunkWrapper(Chunk c) {
        this.x = c.getX();
        this.z = c.getZ();
        this.world = c.getWorld().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof ChunkWrapper))
            return false;
        return ((ChunkWrapper) o).x == this.x && ((ChunkWrapper) o).z == this.z && ((ChunkWrapper) o).world.equals(this.world);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + x;
        hash = hash * 31 + z;
        hash = hash * 31 + world.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + z + ") [" + world + "]";
    }
}