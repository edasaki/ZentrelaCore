package com.edasaki.core.commands.helper;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.players.Rank;
import com.edasaki.core.utils.RMessages;

public class KickCommand extends AbstractCommand {

    public KickCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (pd.getRank() == Rank.BUILDER)
            return;
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "/kick <player> <reason>");
            p.sendMessage(ChatColor.RED + "WARNING: Do not abuse staff privileges.");
        } else if (args.length == 1) {
            p.sendMessage(ChatColor.RED + "You must give a reason for kicking!");
            p.sendMessage(ChatColor.RED + "/kick <player> <reason>");
        } else if (args.length >= 2) {
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                StringBuilder sb = new StringBuilder();
                for (int k = 1; k < args.length; k++) {
                    sb.append(args[k]);
                    sb.append(' ');
                }
                String reason = sb.toString().trim();
                String announce = ChatColor.YELLOW + p2.getName() + ChatColor.RED + " was kicked by " + ChatColor.YELLOW + p.getName() + ChatColor.RED + " for: " + ChatColor.WHITE + ChatColor.BOLD + reason + ".";
                RMessages.announce(ChatColor.GRAY + "> " + announce);
                p2.kickPlayer("You were kicked by " + p.getName() + " for: " + reason + ".");
            } else {
                p.sendMessage("User is not online.");
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
