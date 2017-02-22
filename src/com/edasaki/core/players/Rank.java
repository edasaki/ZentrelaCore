package com.edasaki.core.players;

import org.bukkit.ChatColor;

public enum Rank {
    MEMBER(1, "", "Member", ChatColor.WHITE, ChatColor.GRAY),
    BETA(2, "Beta", "Beta", ChatColor.RED, ChatColor.WHITE),
    VIP(3, "VIP", "VIP", ChatColor.GOLD, ChatColor.WHITE),
    KNIGHT(3, "Knight", "Knight", ChatColor.GOLD, ChatColor.WHITE),
    LORD(4, "Lord", "Lord", ChatColor.GOLD, ChatColor.WHITE),
    PRINCE(5, "Prince", "Prince", ChatColor.GOLD, ChatColor.WHITE),
    PRINCESS(5, "Princess", "Princess", ChatColor.GOLD, ChatColor.WHITE),
    HELPER(6, "Helper", "Helper", ChatColor.AQUA, ChatColor.WHITE),
    GAMEMASTER(7, "GM", "Gamemaster", ChatColor.BLUE, ChatColor.WHITE),
    BUILDER(8, "Builder", "Builder", ChatColor.DARK_AQUA, ChatColor.WHITE),
    MOD(9, "Mod", "Moderator", ChatColor.LIGHT_PURPLE, ChatColor.WHITE),
    ADMIN(10, "Admin", "Administrator", ChatColor.GREEN, ChatColor.WHITE),
    OWNER(11, "Owner", "Owner", ChatColor.YELLOW, ChatColor.WHITE);

    public final int power;
    public final String chatPrefix, rankDisplayName;
    public final ChatColor nameColor;
    public final ChatColor chatColor;

    private String chatRankDisplay = null;
    private String fullRankDisplay = null;

    Rank(int i, String chat, String display, ChatColor c1, ChatColor c2) {
        this.power = i;
        this.chatPrefix = chat;
        this.rankDisplayName = display;
        this.nameColor = c1;
        this.chatColor = c2;
    }

    public String getChatRankDisplay() {
        if (chatRankDisplay != null)
            return chatRankDisplay;
        if (chatPrefix.length() == 0)
            return chatRankDisplay = nameColor + "";
        return chatRankDisplay = nameColor + "" + ChatColor.BOLD + chatPrefix + " " + ChatColor.WHITE;
    }

    public String getFullRankDisplay() {
        if (fullRankDisplay != null)
            return fullRankDisplay;
        return fullRankDisplay = nameColor + "" + ChatColor.BOLD + rankDisplayName + " " + ChatColor.WHITE;
    }

    public boolean checkIsAtLeast(Rank other) {
        return this.power >= other.power;
    }
}
