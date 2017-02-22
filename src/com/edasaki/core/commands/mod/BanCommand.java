package com.edasaki.core.commands.mod;

import java.sql.PreparedStatement;
import java.time.ZonedDateTime;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.punishments.PunishmentManager;
import com.edasaki.core.punishments.PunishmentType;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RMessages;
import com.edasaki.core.utils.RScheduler;

public class BanCommand extends AbstractCommand {

    public BanCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length < 3) {
            p.sendMessage(ChatColor.RED + "/ban <player> <time> <reason>");
            p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
        } else if (args.length >= 3) {
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                if (!executeBan(p2.getName(), p2.getUniqueId().toString(), args, p.getName(), p2)) {
                    p.sendMessage(ChatColor.RED + "/ban <player> <time> <reason>");
                    p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
                }
            } else {
                RScheduler.scheduleAsync(plugin, () -> {
                    OfflinePlayer offline = plugin.getServer().getOfflinePlayer(args[0]);
                    String name = offline.getName();
                    String uuid = offline.getUniqueId().toString();
                    RScheduler.schedule(plugin, () -> {
                        if (!executeBan(name, uuid, args, p.getName(), null) && p.isOnline()) {
                            p.sendMessage(ChatColor.RED + "/ban <player> <time> <reason>");
                            p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
                        }
                    });
                });
                p.sendMessage("Player is not online, executing offline ban.");
            }
        }
    }

    public boolean executeBan(String name, String uuid, String[] args, String banner, Player banned) {
        String time = args[1].toLowerCase();
        ZonedDateTime endTime = ZonedDateTime.now();
        String lengthDisplay = "";
        if (args[1].startsWith("perm")) {
            endTime = endTime.plusYears(1000);
            lengthDisplay = ChatColor.YELLOW + "permanently" + ChatColor.RED + " banned";
        } else {
            int amount = Integer.parseInt(time.replaceAll("[^0-9]", ""));
            if (time.endsWith("m")) {
                endTime = endTime.plusMinutes(amount);
                lengthDisplay = "banned for " + ChatColor.YELLOW + amount + " minute" + (amount != 1 ? "s" : "");
            } else if (time.endsWith("h")) {
                endTime = endTime.plusHours(amount);
                lengthDisplay = "banned for " + ChatColor.YELLOW + amount + " hour" + (amount != 1 ? "s" : "");
            } else if (time.endsWith("d")) {
                endTime = endTime.plusDays(amount);
                lengthDisplay = "banned for " + ChatColor.YELLOW + amount + " day" + (amount != 1 ? "s" : "");
            } else if (time.endsWith("w")) {
                endTime = endTime.plusWeeks(amount);
                lengthDisplay = "banned for " + ChatColor.YELLOW + amount + " week" + (amount != 1 ? "s" : "");
            } else {
                return false;
            }
        }
        lengthDisplay += ChatColor.RESET;
        StringBuilder sb = new StringBuilder();
        for (int k = 2; k < args.length; k++) {
            sb.append(args[k]);
            sb.append(' ');
        }
        final ZonedDateTime fEndTime = endTime;
        String reason = sb.toString().trim();
        RScheduler.scheduleAsync(plugin, () -> {
            AutoCloseable[] ac_dub2 = SQLManager.prepare("INSERT INTO punishments (name, uuid, ip, type, endTime, reason, giver, startTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");
            PreparedStatement insert_ban = (PreparedStatement) ac_dub2[0];
            try {
                insert_ban.setString(1, name);
                insert_ban.setString(2, uuid);
                insert_ban.setString(3, PunishmentManager.ips_byUUID.containsKey(uuid) ? PunishmentManager.ips_byUUID.get(uuid) : "");
                insert_ban.setString(4, PunishmentType.BAN.toString());
                insert_ban.setString(5, fEndTime.toString());
                insert_ban.setString(6, reason);
                insert_ban.setString(7, banner);
                insert_ban.setString(8, ZonedDateTime.now().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SQLManager.execute(ac_dub2);
            SQLManager.close(ac_dub2);
            System.out.println("Executed ban for " + name + ".");
        });
        String announce = ChatColor.YELLOW + name + ChatColor.RED + " was " + lengthDisplay + ChatColor.RED + " by " + ChatColor.YELLOW + banner + ChatColor.RED + " for: " + ChatColor.WHITE + ChatColor.BOLD + reason + ChatColor.RED + ".";
        RMessages.announce(ChatColor.GRAY + "> " + announce);
        if (banned != null && banned.isOnline())
            banned.kickPlayer("You were " + lengthDisplay + " by " + banner + " for: " + reason + ".");
        return true;
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
