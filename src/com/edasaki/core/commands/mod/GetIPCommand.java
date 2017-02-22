package com.edasaki.core.commands.mod;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.punishments.PunishmentManager;

public class GetIPCommand extends AbstractCommand {

    public GetIPCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "/getip <player>");
            p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
        } else {
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline() && plugin.getPD(p2) != null && PunishmentManager.ips_byUUID.containsKey(p2.getUniqueId().toString())) {
                if (plugin.getPD(p2).check(pd.getRank())) {
                    p.sendMessage("You can only check the IP of players lower rank than you ;)");
                } else {
                    String ip = PunishmentManager.ips_byUUID.get(p2.getUniqueId().toString());
                    p.sendMessage(p2.getName() + "'s last logged IP Address is " + ip + ".");
                    p.sendMessage("Contact Misaka if you need a list of all known IPs for that player.");
                }
            } else {
                p.sendMessage("User is not online.");
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
