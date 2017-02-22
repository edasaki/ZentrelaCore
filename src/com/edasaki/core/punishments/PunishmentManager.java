package com.edasaki.core.punishments;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.PlayerData;
import com.edasaki.core.SakiCore;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RScheduler;
import com.edasaki.core.utils.RTicks;

public class PunishmentManager extends AbstractManagerCore {

    public static ConcurrentHashMap<String, Punishment> muted_byUUID = new ConcurrentHashMap<String, Punishment>();

    public static ConcurrentHashMap<String, String> ips_byUUID = new ConcurrentHashMap<String, String>();

    private static final String IP_REGEX = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);

    public PunishmentManager(SakiCore plugin) {
        super(plugin);
    }

    /**
     * @return IP Address as a string
     */
    public static String parseIP(InetAddress add) {
        Matcher matcher = IP_PATTERN.matcher(add.toString());
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    /**
     * @return IP Address as a string
     */
    public static String parseIP(InetSocketAddress add) {
        Matcher matcher = IP_PATTERN.matcher(add.toString());
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static boolean isMuted(Player p) {
        return muted_byUUID.containsKey(p.getUniqueId().toString()) && muted_byUUID.get(p.getUniqueId().toString()).isValid();
    }

    public static String getMuteReason(Player p) {
        if (!isMuted(p))
            return "";
        return muted_byUUID.get(p.getUniqueId().toString()).getReason();
    }

    /**
     * Can be run on async threads
     */
    public static void registerIP(UUID uuid, String ip) {
        // Key may already exist, but it's not a big deal since it must've been registered already
        System.out.println("Registered IP " + ip + " for " + uuid);
        ips_byUUID.put(uuid.toString(), ip);
        RScheduler.schedule(plugin, () -> {
            if (plugin.getServer().getPlayer(uuid) != null && plugin.getServer().getPlayer(uuid).isOnline()) {
                PlayerData pd = plugin.getPD(plugin.getServer().getPlayer(uuid));
                if (pd != null) {
                    // Check if IP is known, if not then register and save
                    if (!pd.knownIPs.contains(ip)) {
                        pd.knownIPs.add(ip);
                        pd.save();
                    }
                }
            }
        });
    }

    @Override
    public void initialize() {
        /*
         * Store IPs of all currently online players (mostly for reload purposes)
         */
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            checkMute(p.getUniqueId().toString());
        }
        RScheduler.schedule(plugin, () -> {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                String ip = PunishmentManager.parseIP(p.getAddress());
                PunishmentManager.registerIP(p.getUniqueId(), ip);
                checkMute(p.getUniqueId().toString());
            }
        }, RTicks.seconds(2));
    }

    public static void checkMute(String uuid) {
        RScheduler.scheduleAsync(plugin, () -> {
            AutoCloseable[] ac_dub = SQLManager.prepare("SELECT * FROM punishments WHERE uuid = ? AND type = ?");
            try {
                PreparedStatement request_punishment_status = (PreparedStatement) ac_dub[0];
                request_punishment_status.setString(1, uuid);
                request_punishment_status.setString(2, PunishmentType.MUTE.toString());
                AutoCloseable[] ac_trip = SQLManager.executeQuery(request_punishment_status);
                ResultSet rs = (ResultSet) ac_trip[0];
                while (rs.next()) {
                    Punishment pun = new Punishment();
                    pun.load(rs);
                    if (pun.isValid()) {
                        muted_byUUID.put(uuid, pun);
                    }
                }
                SQLManager.close(ac_dub);
                SQLManager.close(ac_trip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onAsyncPrelogin(AsyncPlayerPreLoginEvent event) {
        InetAddress address = event.getAddress();
        String name = event.getName();
        String uuid = event.getUniqueId().toString();
        String ip = PunishmentManager.parseIP(address);
        PunishmentManager.registerIP(event.getUniqueId(), ip);
        /*
         * Check if user has a ban in effect matching either UUID or IP
         */
        AutoCloseable[] ac_dub = SQLManager.prepare("SELECT * FROM punishments WHERE uuid = ? OR ip = ?");
        try {
            PreparedStatement request_punishment_status = (PreparedStatement) ac_dub[0];
            request_punishment_status.setString(1, uuid);
            request_punishment_status.setString(2, ip);
            AutoCloseable[] ac_trip = SQLManager.executeQuery(request_punishment_status);
            ResultSet rs = (ResultSet) ac_trip[0];
            while (rs.next()) {
                Punishment pun = new Punishment();
                pun.load(rs);
                if (pun.isValid()) {
                    if (pun.type == PunishmentType.BAN || pun.type == PunishmentType.IPBAN) {
                        System.out.println("Kicking " + name + " for active ban.");
                        event.disallow(Result.KICK_OTHER, pun.getReason());
                    } else if (pun.type == PunishmentType.MUTE) {
                        muted_byUUID.put(uuid, pun);
                    }
                }
            }
            SQLManager.close(ac_dub);
            SQLManager.close(ac_trip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
