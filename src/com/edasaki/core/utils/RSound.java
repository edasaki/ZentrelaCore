package com.edasaki.core.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RSound {
    public static void playSound(Player p, Sound s) {
        p.playSound(p.getLocation(), s, 10, 1);
    }

    public static void playSound(Player p, Sound s, float pitch) {
        p.playSound(p.getLocation(), s, 10, pitch);
    }

    public static void playSound(Player p, Sound s, float volume, float pitch) {
        p.playSound(p.getLocation(), s, volume, pitch);
    }
}