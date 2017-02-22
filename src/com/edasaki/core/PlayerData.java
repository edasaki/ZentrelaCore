package com.edasaki.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.PermissionAttachment;

import com.edasaki.core.badges.Badge;
import com.edasaki.core.menus.MenuGeneralRunnable;
import com.edasaki.core.options.OptionsList;
import com.edasaki.core.options.SakiOption;
import com.edasaki.core.pets.PetAI;
import com.edasaki.core.pets.PetType;
import com.edasaki.core.players.PlayerDataFile;
import com.edasaki.core.players.Rank;
import com.edasaki.core.punishments.PunishmentManager;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.unlocks.Unlock;
import com.edasaki.core.utils.RMessages;
import com.edasaki.core.utils.RScheduler;
import com.edasaki.core.utils.RScheduler.Halter;
import com.edasaki.core.utils.RSound;
import com.edasaki.core.utils.RTicks;
import com.edasaki.core.utils.fanciful.FancyMessage;
import com.edasaki.core.utils.gson.RGson;
import com.google.gson.reflect.TypeToken;

public class PlayerData {

    public static SakiCore plugin;

    // Common info
    protected String name;
    protected UUID uuid;
    protected Rank rank;

    // Has this object been unloaded?
    private boolean unloaded = false;

    // Whether SQL select has been completed for this player and data loaded.
    public volatile boolean loadedSQL = false;

    // Badges owned by this player
    public HashSet<Badge> badges = new HashSet<Badge>();

    // Associated permissions
    protected PermissionAttachment perms;

    // Known IPs
    public HashSet<String> knownIPs = new HashSet<String>();

    // Options
    private OptionsList optionsList;

    // Pets owned by the player and how much EXP they have for the pet
    public HashMap<PetType, Long> ownedPets = new HashMap<PetType, Long>();
    public LinkedHashMap<PetType, PetAI> activePets = new LinkedHashMap<PetType, PetAI>();

    // Ignored people (not persistent)
    public ArrayList<String> ignored = new ArrayList<String>();

    // Menu variables
    public Inventory currentModifiableInventory;
    public MenuGeneralRunnable<?> currentMenuClickRunnable;

    // Prevent doing post-load more than once
    private boolean finishedLoad = false;

    // Boolean unlocks (no associated data)
    public HashSet<Unlock> unlocks = new HashSet<Unlock>();

    // Various binary states associated with a player. These are like unlocks, but less well enumerated (often written in configs).
    private HashSet<String> states = new HashSet<String>();

    public PlayerData(Player p) {
        name = p.getName();
        uuid = p.getUniqueId();
        loadedSQL = false;
        perms = p.addAttachment(plugin);
        load(p);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    /*
     * Loading
     */

    public final void load(final Player p) {
        halters = new ArrayList<Halter>();
        loadFromDatabase(p);
        PlayerData me = this;
        if (me instanceof ExtraSaveable) {
            ((ExtraSaveable) me).preLoad(p);
        }
        RScheduler.schedule(plugin, new Runnable() {
            private int counter = 0;

            public void run() {
                if (unloaded)
                    return;
                if (loadedSQL) {
                    if (finishedLoad)
                        return;
                    finishedLoad = true;
                    sendWelcome(p);
                    p.setGliding(false);
                    RMessages.sendTabTitle(getPlayer(), ChatColor.AQUA + ChatColor.BOLD.toString() + "=== " + ChatColor.YELLOW + ChatColor.BOLD + "Zentrela" + ChatColor.AQUA + ChatColor.BOLD + " ===", ChatColor.GOLD + "www.zentrela.net");
                    startLoadedPets();
                    if (me instanceof ExtraSaveable) {
                        ((ExtraSaveable) me).postLoad(p);
                    }
                } else {
                    RScheduler.schedule(plugin, this, RTicks.seconds(1));
                    counter++;
                    // Print seconds counter with reducing frequency
                    if (counter <= 5 || (counter <= 10 && counter % 2 == 0) || counter % 5 == 0)
                        p.sendMessage(ChatColor.RED + "Loading your save data. Please wait... [" + counter + "]");
                    if (counter >= 20 && counter % 20 == 0) {
                        p.sendMessage(ChatColor.RED + "There may be a problem with loading your save data. This is most likely due to a connection problem on our host. Don't worry, you almost certainly haven't lost any progress. Come back in a couple minutes!");
                    }
                }
            }
        }, RTicks.seconds(1));
    }

    /**
     * Runs async and updates PlayerData.loadedSQL when complete
     */
    public final void loadFromDatabase(final Player p) {
        final UUID uuid_obj = p.getUniqueId();
        final String uuid = p.getUniqueId().toString();
        final String name = p.getName();
        System.out.println("loading " + p + " from db");
        RScheduler.schedule(plugin, new Runnable() {
            public void run() {
                // don't load if it's still saving
                if (PlayerDataFile.currentlySaving.contains(uuid_obj)) {
                    RScheduler.schedule(plugin, this, RTicks.seconds(1));
                } else {
                    RScheduler.scheduleAsync(plugin, new Runnable() {
                        public void run() {
                            boolean kick = false;
                            boolean isNew = false;
                            PlayerDataFile pdf = new PlayerDataFile();
                            if (SakiCore.OFFLINE) {
                                File dir = new File(plugin.getDataFolder().getParentFile(), "offlinesaves");
                                if (!dir.exists())
                                    dir.mkdirs();
                                File f = new File(dir, uuid);
                                if (f.exists()) {
                                    try (Scanner scan = new Scanner(f)) {
                                        while (scan.hasNextLine()) {
                                            String line = scan.nextLine();
                                            String key = line.substring(0, line.indexOf(":"));
                                            String val = line.substring(line.indexOf(":") + 1);
                                            pdf.put(key, val);
                                        }
                                        System.out.println("Loaded offline data for player " + name + ".");
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                        kick = true;
                                    }
                                } else {
                                    try {
                                        f.createNewFile();
                                        System.out.println("Created new offline data for player " + name + ".");
                                        isNew = true;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        kick = true;
                                    }
                                }
                            } else {
                                AutoCloseable[] ac_dub = SQLManager.prepare("SELECT * FROM main WHERE uuid = ?");
                                try {
                                    PreparedStatement request_player_data = (PreparedStatement) ac_dub[0];
                                    request_player_data.setString(1, uuid);
                                    AutoCloseable[] ac_trip = SQLManager.executeQuery(request_player_data);
                                    ResultSet rs = (ResultSet) ac_trip[0];
                                    if (rs.next()) {
                                        // Has existing data
                                        ResultSetMetaData rsmd = rs.getMetaData();
                                        int columnCount = rsmd.getColumnCount();
                                        // The column count starts from 1
                                        for (int i = 1; i <= columnCount; i++) {
                                            String colName = rsmd.getColumnName(i);
                                            String value = rs.getString(colName);
                                            if (value == null) {
                                                value = "";
                                            }
                                            pdf.put(colName, value);
                                        }
                                        System.out.println("Loaded SQL data for player " + name + ".");
                                    } else {
                                        // New player
                                        AutoCloseable[] ac_dub2 = SQLManager.prepare("INSERT INTO main (name, uuid) VALUES (?, ?)");
                                        PreparedStatement create_new_player = (PreparedStatement) ac_dub2[0];
                                        create_new_player.setString(1, name);
                                        create_new_player.setString(2, uuid);
                                        SQLManager.execute(ac_dub2);
                                        SQLManager.close(ac_dub2);
                                        System.out.println("Creating new entry for player " + name + ".");
                                        isNew = true;
                                    }
                                    // Be sure to cleanup and close all connections
                                    SQLManager.close(ac_dub);
                                    SQLManager.close(ac_trip);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    kick = true;
                                }
                            }
                            if (kick) {
                                RScheduler.schedule(plugin, () -> {
                                    p.kickPlayer("Error loading your save data! Error Code 121");
                                });
                            } else {
                                final boolean fnew = isNew;
                                // Finished loading from file/SQL
                                RScheduler.schedule(plugin, () -> {
                                    setLoadedData(fnew, pdf);
                                    loadedSQL = true;
                                });
                            }
                        }
                    });
                }

            }
        });
    }

    public void setLoadedData(boolean isNew, PlayerDataFile pdf) {
        rank = Rank.valueOf(pdf.get("rank"));
        optionsList = deserializeOptions(pdf.get("options"));
        // ownedPets
        HashMap<PetType, Long> ownedPetsMap = RGson.getGson().<HashMap<PetType, Long>> fromJson(pdf.get("ownedPets"), new TypeToken<HashMap<PetType, Long>>() {
        }.getType());
        if (ownedPetsMap != null)
            ownedPets.putAll(ownedPetsMap);
        // unlocks
        HashSet<Unlock> unlocksSet = RGson.getGson().<HashSet<Unlock>> fromJson(pdf.get("unlocks"), new TypeToken<HashSet<Unlock>>() {
        }.getType());
        if (unlocksSet != null)
            unlocks.addAll(unlocksSet);
        // states
        HashSet<String> statesSet = RGson.getGson().<HashSet<String>> fromJson(pdf.get("states"), new TypeToken<HashSet<String>>() {
        }.getType());
        if (statesSet != null)
            states.addAll(statesSet);
        // activePets
        ArrayList<PetType> activePetsTemp = RGson.getGson().<ArrayList<PetType>> fromJson(pdf.get("activePets"), new TypeToken<ArrayList<PetType>>() {
        }.getType());
        if (activePetsTemp != null)
            for (PetType pt : activePetsTemp)
                if (!activePets.containsKey(pt))
                    activePets.put(pt, new PetAI(pt, getPlayer()));

        // set any extra data
        if (this instanceof ExtraSaveable) {
            ((ExtraSaveable) this).setExtraLoadedData(isNew, pdf);
        }
    }

    public final void save() {
        save(false);
    }

    public final void save(boolean onMainThread) {
        if (!loadedSQL) {
            if (!onMainThread) {
                RScheduler.schedule(plugin, () -> {
                    save(false);
                }, RTicks.seconds(2));
            }
            return;
        }
        if (isValid() && PunishmentManager.ips_byUUID.containsKey(getPlayer())) {
            knownIPs.add(PunishmentManager.ips_byUUID.get(getPlayer()));
        }
        final PlayerDataFile pdf = new PlayerDataFile();
        pdf.put("name", name);
        pdf.put("rank", rank == null ? Rank.MEMBER.toString() : rank.toString());
        pdf.put("options", serializeOptions());
        pdf.put("ownedPets", RGson.getConciseGson().toJson(ownedPets));
        pdf.put("unlocks", RGson.getConciseGson().toJson(unlocks));
        pdf.put("states", RGson.getConciseGson().toJson(states));
        ArrayList<PetType> temp = new ArrayList<PetType>();
        for (Entry<PetType, PetAI> e : activePets.entrySet()) {
            temp.add(e.getKey());
        }
        pdf.put("activePets", RGson.getConciseGson().toJson(temp));
        if (this instanceof ExtraSaveable) {
            ((ExtraSaveable) this).extraSave(pdf);
        }
        pdf.save(uuid, onMainThread);
    }

    public void unload() {
        unloaded = true;
        if (uuid != null) {
            PunishmentManager.muted_byUUID.remove(uuid);
            PunishmentManager.ips_byUUID.remove(uuid);
        }
        for (PetAI p : activePets.values()) {
            p.halt();
        }
        activePets.clear();
        name = null;
        uuid = null;
        for (Halter h : halters)
            h.halt = true;
    }

    public void quit() {
        save();
        unload();
    }

    /*
     * Temp States
     */

    public void addState(Object o) {
        states.add(o.toString());
    }

    public boolean hasState(Object o) {
        return states.contains(o.toString());
    }

    public void removeState(Object o) {
        states.remove(o.toString());
    }

    /*
     * Unlocks
     */

    public void addUnlock(Unlock u) {
        if (isValid())
            RSound.playSound(getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        unlocks.add(u);
    }

    public void removeUnlock(Unlock u) {
        System.out.println("removing " + u + " from " + unlocks);
        unlocks.remove(u);
        System.out.println("result: " + unlocks);
    }

    public boolean unlocked(Unlock u) {
        return unlocks.contains(u);
    }

    /*
     * Options
     */

    public boolean getOption(SakiOption so) {
        if (this.optionsList == null) {
            return so.getDefault();
        }
        return this.optionsList.get(so);
    }

    public boolean toggleOption(SakiOption so) {
        if (this.optionsList == null)
            return so.getDefault();
        boolean ret = this.optionsList.toggle(so);
        sendMessage(ChatColor.GRAY + "> " + ChatColor.WHITE + "Toggled " + ChatColor.YELLOW + so.getDisplay() + ChatColor.WHITE + ". " + ChatColor.AQUA + so.getDesc(ret));
        return ret;
    }

    /*
     * Halters
     */

    protected List<Halter> halters;

    protected Halter halter() {
        Halter h = new Halter();
        halters.add(h);
        return h;
    }

    /*
     * Ranks
     */

    public Rank getRank() {
        return rank;
    }

    public boolean check(Rank other) {
        return rank.checkIsAtLeast(other);
    }

    public void setRank(String s) {
        try {
            setRank(Rank.valueOf(s.toUpperCase()));
        } catch (Exception e) {
            Log.error("Could not find rank corresponding to '" + s + "'");
            sendMessage(ChatColor.RED + "Could not find rank corresponding to '" + s + "'");
        }
    }

    public void setRank(Rank r) {
        rank = r;
        save();
    }

    /*
     * Pets
     */

    private void startLoadedPets() {
        System.out.println("startLoadedPets() " + this);
        for (Entry<PetType, PetAI> e : activePets.entrySet()) {
            e.getValue().start();
        }
    }

    public void spawnNewPet(PetType pt) {
        System.out.println("spawning " + pt);
        if (!activePets.containsKey(pt)) {
            PetAI ai = new PetAI(pt, getPlayer());
            activePets.put(pt, ai);
            ai.start();
        }
    }

    /*
     * Utils
     */

    public Player getPlayer() {
        return SakiCore.plugin.getServer().getPlayer(uuid);
    }

    public void sendMessage(Object o) {
        if (o != null)
            sendMessage(o.toString());
    }

    public void sendMessage(String s) {
        if (isValid())
            getPlayer().sendMessage(s);
    }

    public boolean isValid() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    /*
     * Chat stuff
     */

    public ChatColor getChatNameColor() {
        if (rank == null)
            return ChatColor.GRAY;
        return rank.nameColor;
    }

    public String getChatRankPrefix() {
        if (rank == null)
            return "";
        return rank.getChatRankDisplay();
    }

    public String getChatRankPrefixNoColor() {
        if (rank == null)
            return "";
        return rank.chatPrefix;
    }

    public String getFullRankClean() {
        if (rank == null)
            return "";
        return rank.rankDisplayName;
    }

    public ChatColor getChatColor() {
        if (rank == null)
            return ChatColor.GRAY;
        return rank.chatColor;
    }

    public void addBadgesSuffix(FancyMessage fm) {
        if (badges.size() == 0)
            return;
        fm.then(" ");
        for (Badge b : badges) {
            fm.then(b.getDisplay());
            fm.tooltip(b.getTooltip());
        }
    }

    public boolean isIgnoring(PlayerData other) {
        String name = other.getName();
        return ignored.contains(name.toLowerCase());
    }

    public boolean isIgnoring(Player other) {
        String name = other.getName();
        return ignored.contains(name.toLowerCase());
    }

    private void sendWelcome(final Player p) {
        RScheduler.schedule(plugin, new Runnable() {
            public void run() {
                String c;
                if (p == null || !p.isValid() || !p.isOnline())
                    return;
                for (int k = 0; k < 20; k++)
                    p.sendMessage(ChatColor.RESET + "");
                p.sendMessage("");
                c = ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome to Zentrela!";
                RMessages.sendCenteredMessage(p, c);
                p.sendMessage("");
                c = ChatColor.AQUA + "Please join the community at";
                RMessages.sendCenteredMessage(p, c);
                c = ChatColor.AQUA + "www.Zentrela.net";
                RMessages.sendCenteredMessage(p, c);
                p.sendMessage("");
                if (plugin.getServer().getOnlinePlayers().size() > 1) {
                    c = ChatColor.GREEN + "There are currently " + ChatColor.BOLD + plugin.getServer().getOnlinePlayers().size() + ChatColor.GREEN + " players online!";
                    RMessages.sendCenteredMessage(p, c);
                } else {
                    c = ChatColor.GREEN + "There is currently " + ChatColor.BOLD + "1" + ChatColor.GREEN + " player online!";
                    RMessages.sendCenteredMessage(p, c);
                }
                p.sendMessage("");
                c = "Click HERE to see recent updates!";
                FancyMessage fm = new FancyMessage(RMessages.getCenteredMessage(c));
                fm.color(ChatColor.GRAY);
                fm.link("http://zentrela.net/p");
                fm.send(p);
                p.sendMessage("");
            }
        });
    }

    /*
     * Serialization
     */

    private OptionsList deserializeOptions(String temp) {
        return new OptionsList(temp);
    }

    private String serializeOptions() {
        if (this.optionsList == null)
            return "";
        return this.optionsList.toString();
    }

}
