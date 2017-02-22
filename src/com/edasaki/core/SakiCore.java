package com.edasaki.core;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.edasaki.core.badges.BadgeManager;
import com.edasaki.core.chat.ChatManager;
import com.edasaki.core.commands.CommandManager;
import com.edasaki.core.menus.MenuManager;
import com.edasaki.core.options.OptionsManager;
import com.edasaki.core.players.LoginLogoutManager;
import com.edasaki.core.punishments.PunishmentManager;
import com.edasaki.core.shield.ShieldManager;
import com.edasaki.core.sql.SQLManager;

public class SakiCore extends JavaPlugin {

    public static SakiCore plugin;

    public static boolean OFFLINE = false;
    public static boolean TEST_REALM = false;

    private final static HashMap<String, PlayerData> playerdatas = new HashMap<String, PlayerData>();

    private static Class<? extends PlayerData> playerdataClass = PlayerData.class;

    public static Class<? extends PlayerData> getPDClass() {
        return playerdataClass;
    }

    @Override
    public void onEnable() {
        plugin = this;
        PlayerData.plugin = this;
        setPlayerdataClass(PlayerData.class);

        File f = getDataFolder();
        if (!f.exists())
            f.mkdirs();

        checkType();

        // Instantiate Managers here
        new SQLManager(this);

        new ChatManager(this);
        new CommandManager(this);
        new LoginLogoutManager(this);
        new BadgeManager(this);
        new ShieldManager(this);
        if (!OFFLINE)
            new PunishmentManager(this);
        new OptionsManager(this);
        new MenuManager(this);

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            getInstance(LoginLogoutManager.class).login(p);
        }

        System.out.println("Enabled SakiCore.");
    }

    private void checkType() {
        for (File f2 : getDataFolder().getParentFile().listFiles()) {
            if (f2.getName().equals("_TEST_REALM_")) {
                TEST_REALM = true;
                System.out.println("TEST REALM ACTIVE");
            }
            if (f2.getName().equals("_OFFLINE_")) {
                OFFLINE = true;
                System.out.println("OFFLINE MODE ACTIVE");
            }
        }
        if (OFFLINE && !TEST_REALM) {
            System.out.println("ERROR: Offline mode without test realm active.");
            plugin.getServer().shutdown();
        }
    }

    @Override
    public void onDisable() {
        try {
            for (PlayerData pd : getAllPlayerData()) {
                try {
                    pd.getPlayer().closeInventory();
                    pd.save(true);
                    pd.unload();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SQLManager.disconnect();
            ManagerInstances.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Disabled SakiCore.");
    }

    protected void setPlayerdataClass(Class<? extends PlayerData> pdClass) {
        SakiCore.playerdataClass = pdClass;
        try {
            Field pluginField = pdClass.getDeclaredField("plugin");
            if (SakiCore.class.isAssignableFrom(pluginField.getType())) {
                if (!pluginField.isAccessible())
                    pluginField.setAccessible(true);
                pluginField.set(null, this);
                System.out.println("Set " + pdClass + " plugin field to " + this + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, PlayerData> temp = new HashMap<String, PlayerData>();
        for (Entry<String, PlayerData> e : playerdatas.entrySet()) {
            Player p = e.getValue().getPlayer();
            if (p == null || !p.isValid())
                continue;
            e.getValue().unload();
            try {
                temp.put(e.getKey(), pdClass.getConstructor(Player.class).newInstance(p));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
                e1.printStackTrace();
            }
        }
        playerdatas.clear();
        playerdatas.putAll(temp);
    }

    public Collection<PlayerData> getAllPlayerdatas() {
        return playerdatas.values();
    }

    protected final void unloadManager(Class<? extends AbstractManager> clazz) {
        ManagerInstances.unloadManager(clazz);
    }

    public final <T> T getInstance(Class<T> clazz) {
        T inst = ManagerInstances.getInstance(clazz);
        if (inst == null) {
            System.out.println("WARNING: " + clazz + " instance is null!");
        }
        return inst;
    }

    protected final Collection<PlayerData> getAllPlayerData() {
        return playerdatas.values();
    }

    public PlayerData getPD(Object o) {
        if (o == null)
            return null;
        String uuid = null;
        if (o instanceof Player) {
            if (((Player) o).isOnline()) {
                uuid = ((Player) o).getUniqueId().toString();
            }
        } else if (o instanceof UUID) {
            uuid = o.toString();
        } else if (o instanceof String) {
            uuid = (String) o;
        }
        if (uuid == null)
            return null;
        return playerdatas.get(uuid);
    }

    public final PlayerData addPD(Player p) {
        try {
            if (playerdatas.containsKey(p.getUniqueId().toString()))
                playerdatas.get(p.getUniqueId().toString()).unload();
            PlayerData pd = playerdataClass.getConstructor(Player.class).newInstance(p);
            playerdatas.put(p.getUniqueId().toString(), pd);
            return pd;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final PlayerData removePD(Player p) {
        PlayerData pd = playerdatas.remove(p.getUniqueId().toString());
        return pd;
    }

}
