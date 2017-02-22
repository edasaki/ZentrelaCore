package com.edasaki.core.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.SakiCore;
import com.edasaki.core.utils.RFormatter;
import com.edasaki.core.utils.RMessages;

public class MenuManager extends AbstractManagerCore {

    //[player uuid -> [inventory name -> [item name -> runnable]]
    private static HashMap<UUID, HashMap<String, HashMap<String, Runnable>>> invClickables = new HashMap<UUID, HashMap<String, HashMap<String, Runnable>>>();

    private static HashMap<UUID, MenuGeneralRunnable<?>> generalClickables = new HashMap<UUID, MenuGeneralRunnable<?>>();

    public MenuManager(SakiCore plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {

    }

    private static String serializeForMenu(ItemStack item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getType());
        sb.append('$');
        sb.append(item.getAmount());
        if (item.hasItemMeta()) {
            ItemMeta im = item.getItemMeta();
            if (im.hasDisplayName()) {
                sb.append('#');
                sb.append(im.getDisplayName());
            }
            if (im.hasLore()) {
                sb.append('#');
                sb.append(im.getLore().toString());
            }
            if (im.hasEnchants()) {
                sb.append('#');
                sb.append(im.getEnchants().toString());
            }
        }
        return sb.toString();
    }

    public static void registerListener(Player p, ItemStack item, Inventory inventory, Runnable runnable) {
        if (!invClickables.containsKey(p.getUniqueId()))
            invClickables.put(p.getUniqueId(), new HashMap<String, HashMap<String, Runnable>>());
        HashMap<String, HashMap<String, Runnable>> inventories = invClickables.get(p.getUniqueId());
        if (!inventories.containsKey(inventory.getName()))
            inventories.put(inventory.getName(), new HashMap<String, Runnable>());
        HashMap<String, Runnable> thisInv = inventories.get(inventory.getName());
        thisInv.put(serializeForMenu(item), runnable);
    }

    public static void registerGeneral(Player p, MenuGeneralRunnable<?> r) {
        generalClickables.put(p.getUniqueId(), r);
    }

    public static void clear(UUID uuid) {
        if (invClickables.containsKey(uuid))
            invClickables.remove(uuid).clear();
        if (generalClickables.containsKey(uuid))
            generalClickables.remove(uuid);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (invClickables.containsKey(uuid)) {
            HashMap<String, HashMap<String, Runnable>> invs = invClickables.get(uuid);
            String invName = event.getInventory().getName();
            if (invName != null && invs.containsKey(invName) && event.getCurrentItem() != null) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                ItemStack item = event.getCurrentItem();
                String serialized = serializeForMenu(item);
                //                System.out.println("checking " + invs.get(invName) + " for " + serialized);
                if (invs.get(invName).containsKey(serialized)) {
                    Runnable r = invs.get(invName).get(serialized);
                    r.run();
                }
            }
        }
        if (event.getView() != null && event.getView().getBottomInventory() != null && event.getView().getBottomInventory() instanceof CraftInventoryPlayer) {
            if (event.getRawSlot() >= event.getView().getTopInventory().getSize() && generalClickables.containsKey(uuid)) {
                Player p = (Player) event.getWhoClicked();
                generalClickables.get(uuid).onClick(p, event.getCurrentItem(), event.getSlot());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (invClickables.containsKey(uuid)) {
            HashMap<String, HashMap<String, Runnable>> invs = invClickables.get(uuid);
            String invName = event.getInventory().getName();
            if (invName != null && invs.containsKey(invName)) {
                HashMap<String, Runnable> map = invs.remove(invName);
                map.clear();
                map = null;
            }
            generalClickables.remove(uuid);
        }
        if (plugin.getPD((Player) event.getPlayer()) != null)
            plugin.getPD((Player) event.getPlayer()).currentModifiableInventory = null;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (invClickables.containsKey(uuid)) {
            HashMap<String, HashMap<String, Runnable>> invs = invClickables.get(uuid);
            if (event.getInventory().getName() != null && invs.containsKey(event.getInventory().getName())) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
            }
        }
    }

    /*
     * Format for info is:
     * {
     *   {row, col, Material.WHATEVER, ChatColor.WHITE + "first slot title", new Object[] {color, "description here", null, "", color2, "lol"}, new Runnable() {}}
     * }
     */
    public static Inventory createMenu(Player p, String title, int rows, Object[][] info) {
        Inventory inventory = Bukkit.createInventory(null, 9 * rows, title);
        modifyMenu(p, inventory, info);
        return inventory;
    }

    public static Inventory createMenu(Player p, String title, int rows, MenuItem[] info) {
        Inventory inventory = Bukkit.createInventory(null, 9 * rows, title);
        modifyMenu(p, inventory, info);
        return inventory;
    }

    public static Inventory createMenu(Player p, String title, int rows, List<MenuItem> info) {
        Inventory inventory = Bukkit.createInventory(null, 9 * rows, title);
        modifyMenu(p, inventory, info.toArray(new MenuItem[info.size()]));
        return inventory;
    }

    /*
     * Must be called AFTER openInventory for a new inv b/c of closing old inventories
     */
    public static void addMenuGeneralClick(Player p, MenuGeneralRunnable<?> r) {
        MenuManager.registerGeneral(p, r);
    }

    public static Inventory modifyMenu(Player p, Inventory inventory, MenuItem[] info) {
        for (MenuItem mi : info) {
            int row = mi.row;
            int col = mi.col;
            int slot = row * 9 + col;
            ItemStack item = mi.item;
            if (item == null)
                item = new ItemStack(Material.AIR);
            ItemMeta im = item.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            item.setItemMeta(im);
            inventory.setItem(slot, item);
            Runnable r = mi.runnable;
            if (r != null)
                MenuManager.registerListener(p, item, inventory, r);
        }
        return inventory;
    }

    @SuppressWarnings("unchecked")
    public static Inventory modifyMenu(Player p, Inventory inventory, Object[][] info) {
        for (Object[] data : info) {
            int row = (int) data[0];
            int col = (int) data[1];
            int slot = row * 9 + col;
            ItemStack item;
            if (data[2] == null) {
                item = null;
                inventory.setItem(slot, new ItemStack(Material.AIR));
                continue;
            } else if (data[2] instanceof Material) {
                item = new ItemStack((Material) data[2]);
            } else if (data[2] instanceof ItemStack) {
                item = (ItemStack) data[2];
            } else {
                item = null;
                RMessages.announce(ChatColor.RED + "Error 100. Please report to Edasaki or Misaka!");
                return inventory;
            }
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(data[3].toString());
            if (data[4] instanceof Object[]) {
                Object[] desc = (Object[]) data[4];
                ArrayList<String> lore = new ArrayList<String>();
                for (int k = 0; k < desc.length; k += 2) {
                    lore.addAll(RFormatter.stringToLore(desc[k + 1].toString(), desc[k] != null ? desc[k].toString() : ChatColor.GRAY));
                }
                im.setLore(lore);
            } else if (data[4] instanceof ArrayList) {
                im.setLore((ArrayList<String>) data[4]);
            }
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            item.setItemMeta(im);
            inventory.setItem(slot, item);
            Runnable r = (Runnable) data[5];
            if (r != null)
                MenuManager.registerListener(p, item, inventory, r);
        }
        return inventory;
    }
}
