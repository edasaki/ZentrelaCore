package com.edasaki.core.commands.owner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.motd.MotdManager;
import com.edasaki.core.players.Rank;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RMessages;
import com.edasaki.core.utils.RScheduler;

public class MaintenanceCommand extends AbstractCommand {

    public static boolean maintenanceMode = false;

    public static HashSet<String> allowed = new HashSet<String>();

    public MaintenanceCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            maintenanceMode = false;
            allowed.clear();
            RMessages.announce("Maintenance mode disabled.");
        } else {
            allowed.clear();
            try {
                Rank r = Rank.valueOf(args[0].toUpperCase());
                ArrayList<Rank> ranks = new ArrayList<Rank>();
                for (Rank r2 : Rank.values()) {
                    if (r2.checkIsAtLeast(r)) {
                        ranks.add(r2);
                    }
                }
                for (Rank r2 : ranks) {
                    RMessages.announce(ChatColor.GREEN + "Retrieving allowed players of rank " + r2.toString() + ".");
                    RScheduler.scheduleAsync(plugin, () -> {
                        AutoCloseable[] ac_dub = SQLManager.prepare("SELECT name,uuid FROM main WHERE rank = ?");
                        try {
                            PreparedStatement request = (PreparedStatement) ac_dub[0];
                            request.setString(1, r2.toString());

                            AutoCloseable[] ac_trip = SQLManager.executeQuery(request);
                            ResultSet rs = (ResultSet) ac_trip[0];
                            ArrayList<String> names = new ArrayList<String>();
                            ArrayList<String> uuids = new ArrayList<String>();
                            while (rs.next()) {
                                String playername = rs.getString("name");
                                String uuid = rs.getString("uuid");
                                names.add(playername);
                                uuids.add(uuid);
                            }
                            SQLManager.close(ac_dub);
                            SQLManager.close(ac_trip);
                            RScheduler.schedule(plugin, () -> {
                                allowed.addAll(uuids);
                                for (String s : names) {
                                    RMessages.announce("Allowing player " + s);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                for (Player temp : plugin.getServer().getOnlinePlayers()) {
                    PlayerData pd2 = plugin.getPD(temp);
                    if (pd2 == null || !pd2.check(r)) {
                        temp.kickPlayer(ChatColor.RED + "Zentrela is down for maintenance. Please check back later!");
                    }
                }
                RMessages.announce("Maintenance mode enabled (Restrict <" + r.toString() + ").");
                maintenanceMode = true;
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("Invalid rank.");
            }
        }
        if (maintenanceMode) {
            MotdManager.updateMotd(ChatColor.RED + "Zentrela is currently down for maintenance.\nPlease check back later!");
        } else {
            MotdManager.fetchMotd();
        }
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
