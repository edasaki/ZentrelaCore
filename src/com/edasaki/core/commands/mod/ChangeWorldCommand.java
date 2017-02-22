package com.edasaki.core.commands.mod;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;

public class ChangeWorldCommand extends AbstractCommand {

    public ChangeWorldCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length != 1) {
            p.sendMessage("Loaded worlds:");
            for (World w : plugin.getServer().getWorlds())
                p.sendMessage(w.getName());
        } else {
            World w = plugin.getServer().getWorld(args[0]);
            if (w == null) {
                p.sendMessage(ChatColor.RED + "Could not find world " + args[0] + ".");
            } else {
                p.teleport(w.getSpawnLocation());
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
