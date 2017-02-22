package com.edasaki.core.commands.mod;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;

public class TeleportCommand extends AbstractCommand {
    
    public static HashSet<String> noTP = new HashSet<String>();

    public TeleportCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(final Player p, PlayerData pd, String[] args) {
        if (args.length != 1 && args.length != 3) {
            p.sendMessage(ChatColor.RED + "Incorrect command format!");
            p.sendMessage(ChatColor.RED + ">> /tp <name> or /tp <x> <y> <z>");
            p.sendMessage(ChatColor.RED + ">> /tp Misaka");
            p.sendMessage(ChatColor.RED + ">> /tp 97 128 123");
        } else if (args.length == 1) {
            String s = args[0];
            Player target = plugin.getServer().getPlayerExact(s);
            if (target == p) {
                p.sendMessage(ChatColor.RED + "You can't teleport to yourself, silly!");
            } else {
                if (target != null && target.isOnline()) {
                    if(noTP.contains(target.getName())) {
                        target.sendMessage(p.getName() + " tried to teleport to you!");
                        p.sendMessage(ChatColor.RED + "You can't teleport to that person right now!");
                    } else {
                        p.teleport(target);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Could not find online player '" + s + "'.");
                }
            }
        } else if (args.length == 3) {
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                Location loc = new Location(p.getWorld(), x, y, z);
                p.teleport(loc);
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Incorrect command format!");
                p.sendMessage(ChatColor.RED + ">> /tp <name> or /tp <x> <y> <z>");
                p.sendMessage(ChatColor.RED + ">> /tp Misaka");
                p.sendMessage(ChatColor.RED + ">> /tp 97 128 123");
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
