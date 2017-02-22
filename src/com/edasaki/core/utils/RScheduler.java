package com.edasaki.core.utils;

import org.bukkit.plugin.java.JavaPlugin;

public class RScheduler {

    /**
     * Schedule a repeating task. Halts on plugin disable.
     */
    public static void scheduleRepeating(final JavaPlugin plugin, final Runnable r, final int repeatEveryTicks) {
        Runnable r2 = new Runnable() {
            public void run() {
                if (plugin == null || !plugin.isEnabled())
                    return;
                r.run();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, repeatEveryTicks);
            }
        };
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r2);
    }

    /**
     * Schedule a repeating task. Halts when halter is stopped.
     */
    public static void scheduleRepeating(final JavaPlugin plugin, final Runnable r, final int repeatEvery, final Halter halter) {
        Runnable r2 = new Runnable() {
            public void run() {
                if (plugin == null || !plugin.isEnabled() || halter.halt)
                    return;
                r.run();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, repeatEvery);
            }
        };
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r2);
    }

    /**
     * Used to stop rpeating tasks.
     */
    public static class Halter {
        public boolean halt = false;
    }

    /**
     * Schedule a single task.
     */
    public static void schedule(JavaPlugin plugin, Runnable r, int runInTicks) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r, runInTicks);
    }

    /**
     * Schedule a single task to run immediately.
     */
    public static void schedule(JavaPlugin plugin, Runnable r) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r, 0);
    }

    /**
     * Schedule an async task to run immediately.
     */
    public static void scheduleAsync(JavaPlugin plugin, Runnable r) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, r);
    }

    /**
     * Schedule an async task.
     */
    public static void scheduleAsync(JavaPlugin plugin, Runnable r, int runInTicks) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, r, runInTicks);
    }
    
}
