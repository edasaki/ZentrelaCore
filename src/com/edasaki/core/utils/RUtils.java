package com.edasaki.core.utils;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RUtils {

    public static boolean hasInventorySpace(Player p, ItemStack item) {
        int free = 0;
        for (int k = 0; k < 36; k++) {
            ItemStack i = p.getInventory().getItem(k);
            if (i == null) {
                free += item.getMaxStackSize();
            } else if (i.isSimilar(item)) {
                free += item.getMaxStackSize() - i.getAmount();
            }
        }
        return free >= item.getAmount();
    }

    public static boolean hasEmptySpaces(Player p, int count) {
        int empty = 0;
        for (int k = 0; k < 36; k++) {
            if (p.getInventory().getItem(k) == null)
                empty++;
        }
        // code below is WRONG
        //        for (ItemStack i : p.getInventory().getContents()) {
        //            if (i == null) {
        //                empty++;
        //            }
        //        }
        return empty >= count;
    }

    private static final Color[] colors = {
            Color.AQUA,
            Color.BLACK,
            Color.BLUE,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GREEN,
            Color.LIME,
            Color.MAROON,
            Color.NAVY,
            Color.OLIVE,
            Color.ORANGE,
            Color.PURPLE,
            Color.RED,
            Color.SILVER,
            Color.TEAL,
            Color.WHITE,
            Color.YELLOW
    };

    public static Color randomColor() {
        return colors[(int) (Math.random() * colors.length)];
    }

}
