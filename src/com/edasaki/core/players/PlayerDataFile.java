package com.edasaki.core.players;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.edasaki.core.SakiCore;
import com.edasaki.core.sql.SQLManager;
import com.edasaki.core.utils.RScheduler;
import com.edasaki.core.utils.RSerializer;

public class PlayerDataFile {

    private static int performanceCounter = (int) (Math.random() * 10);;

    public static Set<UUID> currentlySaving = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());

    public static HashMap<String, Object> defaults = new HashMap<String, Object>();

    static {
        defaults.put("rank", Rank.MEMBER);
        defaults.put("level", 1);
        defaults.put("hp", 100);
        defaults.put("mana", 10);
        defaults.put("classType", "VILLAGER");
        defaults.put("knownIPs", "");
        defaults.put("timePlayed", 0);
        defaults.put("joinDate", "");
        defaults.put("lastSeen", "");
        defaults.put("spell_RLL", "");
        defaults.put("spell_RLR", "");
        defaults.put("spell_RRL", "");
        defaults.put("spell_RRR", "");
        defaults.put("spAllocation", "");
        defaults.put("questProgress", "");
        defaults.put("exp", 0);
        defaults.put("trinket", "");
        defaults.put("trinketExp", "");
        defaults.put("location", "");
        defaults.put("gamemode", "ADVENTURE");
        defaults.put("inventory", "");
        defaults.put("options", "");
        defaults.put("buycraft", "");
        defaults.put("bank", "");
        defaults.put("horseSpeed", 0);
        defaults.put("horseJump", 0);
        defaults.put("horseBaby", 0);
        defaults.put("horseStyle", "");
        defaults.put("horseColor", "");
        defaults.put("horseVariant", "");
        defaults.put("horseArmor", "");
        defaults.put("badges", "");
        defaults.put("skillEXP", "");
        defaults.put("ownedPets", "");
        defaults.put("unlocks", "");
        defaults.put("activeSoaring", "");
        defaults.put("maxSoaringStamina", 3);
        defaults.put("states", "");
        defaults.put("mobCounter", "");
        defaults.put("activePets", "");
        defaults.put("lastShardCount", 0);
        defaults.put("mobKills", 0);
        defaults.put("playerKills", 0);
        defaults.put("bossKills", 0);
        defaults.put("deaths", 0);
        defaults.put("xmasPoints", 0);
    }

    private LinkedHashMap<String, String> values;

    /*
     * Gets
     */

    public String get(String key) {
        if (!values.containsKey(key) || values.get(key) == null) {
            if (defaults.containsKey(key))
                return defaults.get(key).toString();
            else {
                try {
                    throw new Exception("No default for key " + key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }
        }
        return values.get(key);
    }

    public int getInt(String key) {
        try {
            if (!values.containsKey(key))
                return Integer.parseInt(defaults.get(key).toString());
            return Integer.parseInt(values.get(key));
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.parseInt(defaults.get(key).toString());
        }
    }

    public long getLong(String key) {
        try {
            if (!values.containsKey(key))
                return Long.parseLong(defaults.get(key).toString());
            return Long.parseLong(values.get(key));
        } catch (Exception e) {
            e.printStackTrace();
            return Long.parseLong(defaults.get(key).toString());
        }
    }

    public double getDouble(String key) {
        try {
            if (!values.containsKey(key))
                return Double.parseDouble(defaults.get(key).toString());
            return Double.parseDouble(values.get(key));
        } catch (Exception e) {
            e.printStackTrace();
            return Double.parseDouble(defaults.get(key).toString());
        }
    }

    public String[] getStringArray(String key) {
        if (!values.containsKey(key))
            return (String[]) RSerializer.deserializeArray((defaults.get(key).toString()));
        String s = values.get(key);
        return (String[]) RSerializer.deserializeArray(s);
    }

    /*
     * Puts
     */

    public void put(String key, String value) {
        values.put(key, value);
    }

    public void put(String key, int value) {
        values.put(key, Integer.toString(value));
    }

    public void put(String key, long value) {
        values.put(key, Long.toString(value));
    }

    public void put(String key, double value) {
        values.put(key, Double.toString(value));
    }

    public void put(String key, String[] value) {
        String s = RSerializer.serialize(value);
        values.put(key, s);
    }

    public void save(final UUID uuid, boolean saveOnMainThread) {
        if (currentlySaving.contains(uuid)) {
            System.out.println("==========");
            System.out.println("WARNING: TRYING TO SAVE AGAIN WHILE A SAVE IS IN PROGRESS FOR UUID " + uuid);
            System.out.println("==========");
        }
        if (SakiCore.OFFLINE) { // always save on main thread with filesystem saves, just cuz i'm too lazy to code it otherwise
            File dir = new File(SakiCore.plugin.getDataFolder().getParentFile(), "offlinesaves");
            if (!dir.exists())
                dir.mkdirs();
            File f = new File(dir, uuid.toString());
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)))) {
                for (Entry<String, String> e : values.entrySet()) {
                    out.println(e.getKey() + ":" + e.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        currentlySaving.add(uuid);
        final long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE main SET ");
        int len = values.size();
        int curr = 0;
        final String[] vals = new String[len];
        for (Entry<String, String> e : values.entrySet()) {
            sb.append(e.getKey());
            sb.append(" = ?");
            vals[curr] = e.getValue();
            if (curr < len - 1)
                sb.append(", ");
            curr++;
        }
        sb.append(" WHERE uuid = ?");
        final String query = sb.toString();
        Runnable r = new Runnable() {
            public void run() {
                AutoCloseable[] ac_dub = SQLManager.prepare(query);
                PreparedStatement save_statement = (PreparedStatement) ac_dub[0];
                for (int k = 0; k < vals.length; k++) {
                    try {
                        save_statement.setString((k + 1), vals[k]);
                    } catch (SQLException e) {
                        System.out.println("ERROR BUILDING SAVE QUERY AT COLINDEX " + (k + 1));
                        e.printStackTrace();
                    }
                }
                try {
                    save_statement.setString(vals.length + 1, uuid.toString());
                } catch (SQLException e) {
                    System.out.println("ERROR BUILDING SAVE QUERY AT UUID");
                    e.printStackTrace();
                }
                SQLManager.execute(ac_dub);
                SQLManager.close(ac_dub);
                currentlySaving.remove(uuid);
                if (performanceCounter++ % 10 == 0)
                    System.out.println("Ran full save cycle for " + uuid + " in " + (System.currentTimeMillis() - start) + "ms");
            }
        };
        if (saveOnMainThread) {
            System.out.println("Saving " + uuid.toString() + " on main thread.");
            r.run();
        } else {
            RScheduler.scheduleAsync(SakiCore.plugin, r);
        }

    }

    @Override
    public String toString() {
        return values.toString();
    }

    public PlayerDataFile() {
        values = new LinkedHashMap<String, String>();
    }

}
