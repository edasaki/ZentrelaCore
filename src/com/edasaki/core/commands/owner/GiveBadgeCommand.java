package com.edasaki.core.commands.owner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.badges.Badge;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.utils.RMessages;

public class GiveBadgeCommand extends AbstractCommand {

    public GiveBadgeCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length != 2) {
            p.sendMessage(ChatColor.RED + "Use as /givebadge <name> <badge>");
        } else if (args.length == 2) {
            Badge badge = Badge.valueOf(args[1].toUpperCase());
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                PlayerData pd2 = plugin.getPD(p2);
                pd2.badges.add(badge);
                p.sendMessage(ChatColor.GREEN + p2.getName() + "'s was given the badge " + badge.getDisplayName() + ChatColor.GREEN + ".");
                RMessages.announce(ChatColor.translateAlternateColorCodes('&', "&7> &e" + pd2.getName() + "&a just received a new badge: &b" + badge.getDisplayName()));
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
                pd2.badges.add(badge);
                sender.sendMessage(ChatColor.GREEN + p2.getName() + "'s was given the badge " + badge.getDisplayName() + ChatColor.GREEN + ".");
                RMessages.announce(ChatColor.translateAlternateColorCodes('&', "&7> &e" + pd2.getName() + "&a just received a new badge: &b" + badge.getDisplayName()));
            } else {
                sender.sendMessage("User is not online.");
            }
        }
    }

}
