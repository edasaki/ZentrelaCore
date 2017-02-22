package com.edasaki.core.motd;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.SakiCore;
import com.edasaki.core.utils.RScheduler;

public class MotdManager extends AbstractManagerCore {

    private static String DEFAULT_MOTD;

    private static String TEST_REALM_MOTD = ChatColor.AQUA + "Zentrela Private Test Realm";

    private static String motd = null;
    private static String translatedMotd = null;

    public MotdManager(SakiCore plugin) {
        super(plugin);
    }

    public static String getMotd() {
        return translatedMotd;
    }

    public static void updateMotd(String s) {
        motd = s;
        translatedMotd = ChatColor.translateAlternateColorCodes('&', motd);
    }

    public static void fetchMotd() {
        RScheduler.scheduleAsync(plugin, new Runnable() {
            public void run() {
                Scanner scan = null;
                InputStream stream = null;
                try {
                    scan = new Scanner(stream = new URL("http://www.zentrela.net/motd.txt").openStream());
                    String val = "";
                    while (scan.hasNextLine()) {
                        String temp = scan.nextLine();
                        if (temp.endsWith("###"))
                            continue;
                        val += temp;
                        val += "\n";
                    }
                    val = val.trim();
                    final String fVal = val;
                    RScheduler.schedule(plugin, new Runnable() {
                        public void run() {
                            System.out.println("Fetched motd from site:");
                            System.out.println(fVal);
                            updateMotd(fVal);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (scan != null) {
                        try {
                            scan.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void initialize() {
        DEFAULT_MOTD = ChatColor.RED + "Loading...";
        DEFAULT_MOTD = ChatColor.translateAlternateColorCodes('&', DEFAULT_MOTD);
        fetchMotd();
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (SakiCore.TEST_REALM) {
            event.setMotd(TEST_REALM_MOTD);
            return;
        }
        if (motd != null) {
            if (translatedMotd == null) { // shouldn't ever happen but just in case
                translatedMotd = ChatColor.translateAlternateColorCodes('&', motd);
            }
            event.setMotd(translatedMotd);
        } else {
            event.setMotd(DEFAULT_MOTD);
        }
    }

}
