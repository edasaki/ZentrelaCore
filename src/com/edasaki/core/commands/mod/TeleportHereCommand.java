package com.edasaki.core.commands.mod;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;

public class TeleportHereCommand extends AbstractCommand {

    public TeleportHereCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(final Player p, PlayerData pd, String[] args) {
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "Incorrect command format!");
            p.sendMessage(ChatColor.RED + ">> /tphere <name>");
        } else if (args.length == 1) {
            String s = args[0];
            Player target = plugin.getServer().getPlayerExact(s);
            if (target == p) {
                p.sendMessage(ChatColor.RED + "You can't teleport yourself, silly!");
            } else {
                if (target != null && target.isOnline()) {
                    target.teleport(p);
                    target.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + ".");
                } else {
                    p.sendMessage(ChatColor.RED + "Could not find online player '" + s + "'.");
                }
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
