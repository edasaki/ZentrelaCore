package com.edasaki.core.commands.owner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.unlocks.Unlock;

public class GiveUnlockCommand extends AbstractCommand {

    public GiveUnlockCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        if (args.length != 2) {
            p.sendMessage(ChatColor.RED + "Use as /giveunlock <name> <unlock>");
        } else if (args.length == 2) {
            Unlock unlock = Unlock.valueOf(args[1].toUpperCase());
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                PlayerData pd2 = plugin.getPD(p2);
                pd2.addUnlock(unlock);
                unlock.sendMessage(pd2);
                p.sendMessage("Gave unlock " + unlock + " to " + pd2.getName());
            } else {
                p.sendMessage("User is not online.");
            }
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Use as /giveunlock <name> <unlock>");
        } else if (args.length == 2) {
            Unlock unlock = Unlock.valueOf(args[1].toUpperCase());
            Player p2 = plugin.getServer().getPlayer(args[0]);
            if (p2 != null && p2.isValid() && p2.isOnline()) {
                PlayerData pd2 = plugin.getPD(p2);
                pd2.addUnlock(unlock);
                unlock.sendMessage(pd2);
                sender.sendMessage("Gave unlock " + unlock + " to " + pd2.getName());
            } else {
                sender.sendMessage("User is not online.");
            }
        }
    }

}
