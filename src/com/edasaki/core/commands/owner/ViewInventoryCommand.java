package com.edasaki.core.commands.owner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.edasaki.core.PlayerData;
import com.edasaki.core.commands.AbstractCommand;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RMessages;
import com.edasaki.core.utils.RScheduler;
import com.edasaki.core.utils.RSerializer;

public class ViewInventoryCommand extends AbstractCommand {

    public ViewInventoryCommand(String... commandNames) {
        super(commandNames);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
    }

    @Override
    public void executePlayer(Player p, PlayerData pd, String[] args) {
        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target != null && target.isOnline() && plugin.getPD(target) != null) {
            p.getInventory().setContents(target.getInventory().getContents());
            p.sendMessage("Set inventory to " + target.getName() + "'s");
        } else {
            p.sendMessage(ChatColor.RED + "Could not find online player '" + args[0] + "'.");
            p.sendMessage(ChatColor.RED + "Attempting offline lookup...");
            RScheduler.scheduleAsync(plugin, () -> {
                AutoCloseable[] ac_dub = SQLManager.prepare("select name, inventory from main where name = ?");
                try {
                    PreparedStatement request_items = (PreparedStatement) ac_dub[0];
                    request_items.setString(1, args[0]);
                    AutoCloseable[] ac_trip = SQLManager.executeQuery(request_items);
                    ResultSet rs = (ResultSet) ac_trip[0];
                    int count = 0;
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String inventory = rs.getString("inventory");
                        RScheduler.schedule(plugin, () -> {
                            p.getInventory().clear();
                            if (inventory != null) {
                                HashMap<Integer, ItemStack> dInv = deserializeInventory(inventory);
                                if (dInv != null) {
                                    for (Entry<Integer, ItemStack> e : dInv.entrySet()) {
                                        p.getInventory().setItem(e.getKey(), e.getValue());
                                    }
                                }
                            }
                            RMessages.announce("set inventory to " + name);
                        }, count++);
                    }
                    SQLManager.close(ac_dub);
                    SQLManager.close(ac_trip);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
    }

    private HashMap<Integer, ItemStack> deserializeInventory(String s) {
        HashMap<Integer, ItemStack> map = new HashMap<Integer, ItemStack>();
        if (s == null)
            return map;
        String[] data = s.split("@");
        if (data.length == 0 || (data.length == 1 && data[0].equals("")))
            return map;
        for (String temp : data) {
            try {
                // don't use split in case item serialization contains ::
                String a = temp.substring(0, temp.indexOf("::"));
                String b = temp.substring(temp.indexOf("::") + "::".length());
                int k = Integer.parseInt(a);
                ItemStack item = RSerializer.deserializeItemStack(b);
                map.put(k, item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
