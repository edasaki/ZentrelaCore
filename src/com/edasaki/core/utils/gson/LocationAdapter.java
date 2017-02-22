package com.edasaki.core.utils.gson;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;

import com.edasaki.core.SakiCore;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocationAdapter extends TypeAdapter<Location> {

    @Override
    public Location read(JsonReader in) throws IOException {
        World w = null;
        String worldString = "";
        double x = 0, y = 0, z = 0;
        float yaw = 0, pitch = 0;
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "x":
                    x = in.nextDouble();
                    break;
                case "y":
                    y = in.nextDouble();
                    break;
                case "z":
                    z = in.nextDouble();
                    break;
                case "yaw":
                    yaw = (float) in.nextDouble();
                    break;
                case "pitch":
                    pitch = (float) in.nextDouble();
                    break;
                case "worldString":
                    worldString = in.nextString();
                    break;
            }
        }
        in.endObject();
        w = SakiCore.plugin.getServer().getWorld(worldString);
        if (w == null)
            throw new IOException("Invalid world in JSON: " + worldString + ".");
        return new Location(w, x, y, z, yaw, pitch);
    }

    @Override
    public void write(JsonWriter out, Location loc) throws IOException {
        out.beginObject();
        out.name("x").value(loc.getX());
        out.name("y").value(loc.getY());
        out.name("z").value(loc.getZ());
        out.name("yaw").value(loc.getYaw());
        out.name("pitch").value(loc.getPitch());
        out.name("worldString").value(loc.getWorld().getName());
        out.endObject();
    }

}
