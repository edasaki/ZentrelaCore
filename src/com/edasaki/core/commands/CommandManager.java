package com.edasaki.core.commands;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.SakiCore;
import com.edasaki.core.commands.admin.BanIPCommand;
import com.edasaki.core.commands.general.PetCommand;
import com.edasaki.core.commands.general.TestCommand;
import com.edasaki.core.commands.helper.KickCommand;
import com.edasaki.core.commands.helper.MuteCommand;
import com.edasaki.core.commands.mod.BackCommand;
import com.edasaki.core.commands.mod.BanCommand;
import com.edasaki.core.commands.mod.ChangeWorldCommand;
import com.edasaki.core.commands.mod.FlyCommand;
import com.edasaki.core.commands.mod.FlySpeedCommand;
import com.edasaki.core.commands.mod.GetIPCommand;
import com.edasaki.core.commands.mod.PardonCommand;
import com.edasaki.core.commands.mod.TeleportCommand;
import com.edasaki.core.commands.mod.TeleportHereCommand;
import com.edasaki.core.commands.owner.GiveBadgeCommand;
import com.edasaki.core.commands.owner.GiveUnlockCommand;
import com.edasaki.core.commands.owner.RemoveBadgeCommand;
import com.edasaki.core.commands.owner.RemoveUnlockCommand;
import com.edasaki.core.commands.owner.SetInventoryCommand;
import com.edasaki.core.commands.owner.SetRankCommand;
import com.edasaki.core.commands.owner.ViewInventoryCommand;
import com.edasaki.core.players.Rank;

public class CommandManager extends AbstractManagerCore {

    public static CommandMap cmap = null;

    public CommandManager(SakiCore plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().split(" ")[0].contains(":")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Hidden syntax is disabled.");
        }
    }

    @Override
    public void initialize() {
        try {
            Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            cmap = (CommandMap) f.get(plugin.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cmap == null) {
            Log.error("FATAL ERROR: COULD NOT RETRIEVE COMMAND MAP.");
            plugin.getServer().shutdown();
            return;
        }
        AbstractCommand.plugin = plugin;

        // Member
        register(Rank.MEMBER, new TestCommand("testcommand"));
        register(Rank.MEMBER, new PetCommand("pet", "pets"));
        // Mod
        register(SakiCore.TEST_REALM ? Rank.MEMBER : Rank.MOD, new ChangeWorldCommand("changeworld", "cw"));
        register(SakiCore.TEST_REALM ? Rank.MEMBER : Rank.MOD, new TeleportCommand("teleport", "tp"));
        register(Rank.MOD, new TeleportHereCommand("tphere", "teleporthere"));
        register(Rank.MOD, new BanCommand("ban"));
        register(Rank.MOD, new GetIPCommand("getip"));
        register(Rank.MOD, new PardonCommand("pardon"));
        register(Rank.HELPER, new KickCommand("kick")); // manually disable for Builder rank
        register(Rank.HELPER, new MuteCommand("mute")); // manually disable for Builder rank
        register(SakiCore.TEST_REALM ? Rank.MEMBER : Rank.MOD, new BackCommand("back"));
        register(SakiCore.TEST_REALM ? Rank.MEMBER : Rank.MOD, new FlyCommand("fly"));
        register(SakiCore.TEST_REALM ? Rank.MEMBER : Rank.MOD, new FlySpeedCommand("flyspeed"));

        // Admin
        register(Rank.ADMIN, new BanIPCommand("banip", "ipban"));
        // Owner
        register(Rank.OWNER, new SetRankCommand("setrank", "setrankalias"));
        register(Rank.OWNER, new GiveBadgeCommand("givebadge"));
        register(Rank.OWNER, new RemoveBadgeCommand("removebadge"));
        register(Rank.OWNER, new SetInventoryCommand("setinv", "setinventory"));
        register(Rank.OWNER, new ViewInventoryCommand("viewinventory", "checkinventory", "seeinv", "seeinventory"));
        register(Rank.OWNER, new GiveUnlockCommand("giveunlock"));
        register(Rank.OWNER, new RemoveUnlockCommand("removeunlock"));

    }

    protected void register(Rank rank, AbstractCommand command) {
        command.requiredRank = rank;
        cmap.register("", command);
        plugin.getServer().getPluginManager().registerEvents(command, plugin);
    }
}
