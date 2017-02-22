package com.edasaki.core.commands.owner;

import java.sql.PreparedStatement;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RScheduler;
import com.edasaki.core.utils.RSerializer;

public class SetInventoryCommand extends AbstractCommand {

    public SetInventoryCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target != null && target.isOnline() && plugin.getPD(target) != null) {
            target.getInventory().setContents(p.getInventory().getContents());
            pd.sendMessage("Set " + target.getName() + "'s inventory to your inventory.");
            target.sendMessage("Your inventory was modified by " + pd.getName() + ".");
        } else {
            p.sendMessage(ChatColor.RED + "Could not find online player '" + args[0] + "'.");
            p.sendMessage(ChatColor.RED + "Attempting offline update...");
            String serInv = serializeInventory(p.getInventory().getContents());
            RScheduler.scheduleAsync(plugin, () -> {
                p.sendMessage("Running query...");
                String query = "update main set inventory = ? where name = ?";
                AutoCloseable[] ac_dub2 = SQLManager.prepare(query);
                PreparedStatement concat_reward = (PreparedStatement) ac_dub2[0];
                try {
                    concat_reward.setString(1, serInv);
                    concat_reward.setString(2, args[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SQLManager.execute(ac_dub2);
                SQLManager.close(ac_dub2);
                p.sendMessage("Modified " + args[0] + "'s inventory");
            });
            return;
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

    private String serializeInventory(ItemStack[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < arr.length; k++) {
            if (arr[k] != null) {
                sb.append(k);
                sb.append("::");
                sb.append(RSerializer.serializeItemStack(arr[k]));
                sb.append("@");
            }
        }
        String s = sb.toString().trim();
        if (s.endsWith("@"))
            s = s.substring(0, s.length() - 1);
        return s;
    }
}
