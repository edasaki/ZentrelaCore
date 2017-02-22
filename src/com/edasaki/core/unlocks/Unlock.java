package com.edasaki.core.unlocks;

import org.bukkit.ChatColor;

import com.edasaki.core.PlayerData;
import com.google.gson.annotations.SerializedName;

public enum Unlock {
    /*
     * Flight stuff 
     */
    // Speeds
    @SerializedName("SOARING_SPEED_BASIC") SOARING_SPEED_BASIC("Basic Soaring", ChatColor.GRAY + "> " + ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/soar" + ChatColor.GRAY + " to begin Soaring!"),
    @SerializedName("SOARING_SPEED_INTERMEDIATE") SOARING_SPEED_INTERMEDIATE("Intermediate Soaring", ChatColor.GRAY + "> " + ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/soar" + ChatColor.GRAY + " to begin Soaring!"),

    @SerializedName("SOARING_SPEED_ADVANCED") SOARING_SPEED_ADVANCED("Advanced Soaring", ChatColor.GRAY + "> " + ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/soar" + ChatColor.GRAY + " to begin Soaring!"),

    @SerializedName("SOARING_SPEED_EXPERT") SOARING_SPEED_EXPERT("Expert Soaring", ChatColor.GRAY + "> " + ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/soar" + ChatColor.GRAY + " to begin Soaring!"),

    @SerializedName("SOARING_SPEED_MASTER") SOARING_SPEED_MASTER("Master Soaring", ChatColor.GRAY + "> " + ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/soar" + ChatColor.GRAY + " to begin Soaring!"),
    // Particles
    @SerializedName("SOARING_EFFECT_RAINBOW") SOARING_EFFECT_RAINBOW("Rainbow Trail", null),
    @SerializedName("SOARING_EFFECT_SHADOW") SOARING_EFFECT_SHADOW("Shadow Trail", null),
    ;
    private String display, additional;

    Unlock(String a, String b) {
        this.display = a;
        this.additional = b;
    }

    public void sendMessage(PlayerData pd) {
        pd.sendMessage(ChatColor.GRAY + "> " + ChatColor.GREEN + "You have unlocked " + ChatColor.YELLOW + this.display + ChatColor.GREEN + "!");
        if (this.additional != null)
            pd.sendMessage(additional);
    }
}
