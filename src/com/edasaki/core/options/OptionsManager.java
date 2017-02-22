package com.edasaki.core.options;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.PlayerData;
import com.edasaki.core.SakiCore;
import com.edasaki.core.menus.MenuManager;

public class OptionsManager extends AbstractManagerCore {

    public OptionsManager(SakiCore plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
    }

    public static void msgDamage(Player p, PlayerData pd, String msg) {
        if (pd.getOption(SakiOption.DAMAGE_MESSAGES))
            p.sendMessage(msg);
    }

    public static void openMenu(Player p, PlayerData pd) {
        Inventory inventory = MenuManager.createMenu(p, "Zentrela Game Options", 6, new Object[][] {});
        displayOptions(p, pd, inventory);
        p.openInventory(inventory);
    }

    public static void displayOptions(Player p, PlayerData pd, Inventory inventory) {
        int index = 0;
        int col = 0;
        int row = 0;
        for (SakiOption so : SakiOption.values()) {
            if (index > 8) {
                index = 0;
                row++;
            }
            col = index;
            index++;
            ItemStack item;
            boolean status;
            if (status = pd.getOption(so)) {
                item = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getWoolData());
            } else {
                item = new ItemStack(Material.WOOL, 1, DyeColor.RED.getWoolData());
            }
            MenuManager.modifyMenu(p, inventory, new Object[][] {
                    {
                            row,
                            col,
                            item,
                            (status ? ChatColor.YELLOW : ChatColor.GRAY) + so.getDisplay(),
                            new Object[] {
                                    status ? ChatColor.GREEN : ChatColor.RED,
                                    so.getDesc(status),
                                    null,
                                    "",
                                    ChatColor.GRAY,
                                    "Click to toggle this option."
                            },
                            new Runnable() {
                                public void run() {
                                    if (pd.isValid()) {
                                        pd.toggleOption(so);
                                        displayOptions(p, pd, inventory);
                                    }
                                }
                            }
                    }
            });
        }
    }
}