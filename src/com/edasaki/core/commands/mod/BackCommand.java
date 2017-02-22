package com.edasaki.core.commands.mod;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;

public class BackCommand extends AbstractCommand {
    
    public static HashMap<String, Location> lastLoc = new HashMap<String, Location>();
    
    public BackCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(final Player p, PlayerData pd, String[] args) {
        if(lastLoc.containsKey(p.getName())) {
            Location dest = lastLoc.get(p.getName());
            p.teleport(dest);
            p.sendMessage("Warped you to your last position.");
        } else {
            p.sendMessage("You have not teleported yet.");
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

}
