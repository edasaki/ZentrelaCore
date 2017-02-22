package com.edasaki.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import de.slikey.effectlib.util.ParticleEffect;
import de.slikey.effectlib.util.ParticleEffect.ParticleData;

public class RParticles {

    public static void spawnParticle(Location loc, Particle particle, double x, double y, double z, int count) {
        loc.getWorld().spawnParticle(particle, x, y, z, count);
    }

    public static void sendLightning(Location loc) {
        sendLightning(null, loc);
    }

    public static void sendLightning(Player p, Location loc) {
        Class<?> light = getNMSClass("EntityLightning");
        try {
            Constructor<?> constu = light.getConstructor(getNMSClass("World"), double.class, double.class, double.class, boolean.class, boolean.class);
            Object wh = loc.getWorld().getClass().getMethod("getHandle").invoke(loc.getWorld());
            Object lighobj = constu.newInstance(wh, loc.getX(), loc.getY(), loc.getZ(), false, false);

            Object obj = getNMSClass("PacketPlayOutSpawnEntityWeather").getConstructor(getNMSClass("Entity")).newInstance(lighobj);
            if (p != null) {
                sendPacket(p, obj);
                p.playSound(loc, Sound.ENTITY_LIGHTNING_THUNDER, 25, 1);
            }

            for (Entity e : RMath.getNearbyEntities(loc, 25)) {
                if (e instanceof Player) {
                    if (p != null && e == p)
                        continue;
                    sendPacket((Player) e, obj);
                    ((Player) e).playSound(loc, Sound.ENTITY_LIGHTNING_THUNDER, 35, 1);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the specified ParticleEffect at the location.
     * @param effect
     * @param loc
     */
    public static void show(ParticleEffect effect, Location loc) {
        effect.display(0, 0, 0, 0, 3, loc, 100);
    }

    public static void show(ParticleEffect effect, Location loc, int amount) {
        effect.display(0, 0, 0, 0, amount, loc, 100);
    }

    public static boolean isAirlike(Block b) {
        if (b == null)
            return false;
        return isAirlike(b.getType());
    }

    public static boolean isAirlike(Material m) {
        switch (m.getId()) {
            case 0: //air
            case 78: //snow layer
            case 51: //fire
            case 50: //torch
            case 59: //wheat
            case 39: //mushroom
            case 40: //mushroom
            case 55: //redstone wire
            case 70: //stone pressure
            case 72: //wood pressure
            case 106: //vines
            case 141: //carrots
            case 142: //potatoes
            case 175: //flowers
            case 31: //grasses
            case 32: //dead shrub
            case 323: //sign (item, just in case)
            case 63: //standing sign
            case 68: //wall-mounted sign
            case 69: //lever
            case 77: //stone button
            case 143: //wood button
            case 75: //redstone torch on
            case 76: //redstone torch off
            case 65: //ladder
            case 66: //rail
            case 83: //sugar cane
            case 171: // carpet
                return true;
            default:
                return false;
        }
    }

    /**
     * Show the specified amount of ParticleEffect at the location with 
     * random x, y, and z offsets in the range (0.5, 0.5).
     * @param effect
     * @param loc
     */
    public static void showWithOffset(ParticleEffect effect, Location loc, double offsetMultiplier, int amount) {
        float x = (float) (Math.random() * (Math.random() < 0.5 ? -offsetMultiplier : offsetMultiplier) * 0.5f);
        float y = (float) (Math.random() * (Math.random() < 0.5 ? -offsetMultiplier : offsetMultiplier) * 0.5f);
        float z = (float) (Math.random() * (Math.random() < 0.5 ? -offsetMultiplier : offsetMultiplier) * 0.5f);
        effect.display(x, y, z, 0, amount, loc, 100);
    }

    public static void showWithOffsetPositiveY(ParticleEffect effect, Location loc, double offsetMultiplier, int amount) {
        float x = (float) (Math.random() * (Math.random() < 0.5 ? -offsetMultiplier : offsetMultiplier) * 0.5f);
        float y = (float) (Math.random() * offsetMultiplier * 0.5f);
        float z = (float) (Math.random() * (Math.random() < 0.5 ? -offsetMultiplier : offsetMultiplier) * 0.5f);
        effect.display(x, y, z, 0, amount, loc, 100);
    }

    public static void showWithSpeed(ParticleEffect effect, Location loc, float speed, int amount) {
        effect.display(0, 0, 0, speed, amount, loc, 100);
    }

    public static void showWithData(ParticleEffect effect, Location loc, ParticleData data, int amount) {
        effect.display(data, 0, 0, 0, 0, amount, loc, 100);
    }

    public static void showWithDataAndOffset(ParticleEffect effect, Location loc, ParticleData data, int amount, double offsetX, double offsetY, double offsetZ) {
        effect.display(data, (float) offsetX, (float) offsetY, (float) offsetZ, 0, amount, loc, 100);
    }

    /**
     * Spawn a firework with a randomized meta at the location.
     * @param loc The location to spawn at.
     */
    public static Firework spawnRandomFirework(Location loc) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.values()[(int) (Math.random() * FireworkEffect.Type.values().length - 1)];
        Color c1 = Color.fromRGB(RMath.randInt(0, 255), RMath.randInt(0, 255), RMath.randInt(0, 255));
        Color c2 = Color.fromRGB(RMath.randInt(0, 255), RMath.randInt(0, 255), RMath.randInt(0, 255));
        FireworkEffect effect = FireworkEffect.builder().flicker(Math.random() > 0.5).withColor(c1).withFade(c2).with(type).trail(Math.random() > 0.5).build();
        fwm.addEffect(effect);
        fwm.setPower(RMath.randInt(1, 2));
        fw.setFireworkMeta(fwm);
        return fw;
    }

}
