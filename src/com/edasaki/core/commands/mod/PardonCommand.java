package com.edasaki.core.commands.mod;

import java.sql.PreparedStatement;
import java.time.ZonedDateTime;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.punishments.Punishment;
import com.edasaki.core.punishments.PunishmentManager;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RMessages;
import com.edasaki.core.utils.RScheduler;

public class PardonCommand extends AbstractCommand {

    public PardonCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "/pardon <pid>");
            p.sendMessage(ChatColor.RED + "PID is displayed by /lookup, it's a number.");
            p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
        } else {
            if (!pardon(args, p.getName())) {
                p.sendMessage(ChatColor.RED + "/pardon <pid>");
                p.sendMessage(ChatColor.RED + "PID is displayed by /lookup, it's a number.");
                p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
            } else {
                RMessages.announce(ChatColor.GRAY + "> " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + " issued a pardon for " + ChatColor.YELLOW + "Punishment ID " + args[0] + org.bukkit.ChatColor.GREEN + ".");
                p.sendMessage("Issued pardon for Punishment ID " + args[0]);
            }
        }
    }

    public boolean pardon(String[] args, String canceller) {
        int id = -1;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int k = 1; k < args.length; k++) {
            sb.append(args[k]);
            sb.append(' ');
        }
        final int fid = id;
        RScheduler.scheduleAsync(plugin, () -> {
            AutoCloseable[] ac_dub2 = SQLManager.prepare("UPDATE punishments SET cancelled = ?, canceller = ?, cancelTime = ? WHERE id = ?");
            PreparedStatement insert_ban = (PreparedStatement) ac_dub2[0];
            try {
                insert_ban.setString(1, "true");
                insert_ban.setString(2, canceller);
                insert_ban.setString(3, ZonedDateTime.now().toString());
                insert_ban.setString(4, fid + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            SQLManager.execute(ac_dub2);
            SQLManager.close(ac_dub2);
            System.out.println("Executed pardon for " + fid + ".");
        });
        String toRemove = null;
        for (Entry<String, Punishment> e : PunishmentManager.muted_byUUID.entrySet()) {
            if (e.getValue().id == fid) {
                toRemove = e.getKey();
                break;
            }
        }
        if (toRemove != null) {
            Punishment p = PunishmentManager.muted_byUUID.remove(toRemove);
            PunishmentManager.checkMute(p.uuid);
            if (plugin.getPD(p.uuid) != null) {
                plugin.getPD(p.uuid).sendMessage(ChatColor.GREEN + "Your mute (PID " + fid + ") has been pardoned.");
                plugin.getPD(p.uuid).sendMessage(ChatColor.GREEN + "You can see your punishment history with " + ChatColor.YELLOW + "/lookup " + p.name + ChatColor.GREEN + ".");
            }
        }
        return true;
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
