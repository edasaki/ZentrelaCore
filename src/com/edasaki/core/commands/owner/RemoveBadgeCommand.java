package com.edasaki.core.commands.owner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.badges.Badge;
import com.edasaki.core.commands.AbstractCommand;

public class RemoveBadgeCommand extends AbstractCommand {

    public RemoveBadgeCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length != 2) {
            p.sendMessage(ChatColor.RED + "Use as /removebadge <name> <badge>");
        } else if (args.length == 2) {
            Badge badge = Badge.valueOf(args[1].toUpperCase());
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                PlayerData pd2 = plugin.getPD(p2);
                pd2.badges.remove(badge);
                p.sendMessage(ChatColor.GREEN + p2.getName() + "'s badge " + badge.getDisplayName() + ChatColor.GREEN + " was removed.");
            } else {
                p.sendMessage("User is not online.");
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Badge badge = Badge.valueOf(args[1].toUpperCase());
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                PlayerData pd2 = plugin.getPD(p2);
                pd2.badges.remove(badge);
                sender.sendMessage(ChatColor.GREEN + p2.getName() + "'s badge " + badge.getDisplayName() + ChatColor.GREEN + " was removed.");
            } else {
                sender.sendMessage("User is not online.");
            }
        }
    }

}
