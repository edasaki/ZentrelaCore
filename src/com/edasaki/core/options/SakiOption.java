package com.edasaki.core.options;

public enum SakiOption {

    DAMAGE_MESSAGES("Damage Messages", false, "Show messages about damage dealt and received.", "Hide all damage messages."),
    EXP_MESSAGES("EXP Messages", false, "Show messages about how much EXP you receive.", "Hide all EXP messages"),
    SET_NOTIFICATION("Set Notification", true, "Receive a notification when you have a set equipped.", "Don't notify when a set is equipped."),
    CHAT_FILTER("Chat Filter", true, "Filter inapproriate messages from other players, replacing words with * symbols.", "Don't filter chat."),
    SAKIBOT_TIPS("Rensa's Tips", true, "Receive tips from Rensa the chatbot every few minutes.", "Hide automatic tips from Rensa."),
    COMPACT_REGION_MESSAGES("Compact Region Messages", false, "Use compact one-line region messages.", "Use detailed region messages."),
    HEAL_MESSAGES("Heal Messages", true, "Receive a message whenever you regain health.", "Hide all healing messages."),
    PERM_COORDS("Permanent Coordinates", true, "Display coordinates in your HP bar.", "Don't show coordinates in your HP bar."),
    MANA_MESSAGES("Mana Messages", true, "Show spell mana consumption messages.", "Hide spell mana consumption messages."),
    SPELL_MESSAGES("Spell Messages", true, "Show spell casting messages.", "Hide spell casting messages."),
    LEVEL_ANNOUNCEMENTS("Level Up Announcements", true, "See an announcement whenever a player levels up past level 50.", "Don't see level up announcements."),
    LOCAL_CHAT("Local Chat Mode", true, "Only see chat from players within 50 blocks of you.", "See chat from all players, regardless of how far away they are."),
    GLOBAL_CHAT("See Global Chat", true, "See Global Chat messages. (sent by using # before chat message)", "Don't see Global Chat messages. (Highly not recommended! You won't be able to see any messages from staff, etc.)"),
    ITEM_PROTECT("Item Protection Messages", true, "Receive a message when you try to loot an item that is protected.", "Hide item protection messages."),
    EQUIP_LEVEL("Level Restriction Messages", true, "Receive a message when you wear equipment that is too high-tiered for you.", "Hide level restriction messages."),
    DEATH_ALERTS("Death Notifications", true, "Receive a message whenever someone dies.", "Don't receive death notifications."),
    TRINKET_TIMER("Trinket Timer", false, "Show trinket timer in HP bar.", "Don't show trinket timer in HP bar."),
    SOFT_LAUNCH("Soft Launch Message", true, "Receive the kinda spammy message about how this is still a soft launch.", "Hide the soft launch message."),
    LOCAL_CHAT_WARNING("Local Chat Warning", true, "Receive an info message when you chat in local chat and no one sees it.", "Hide the local chat warning."),
    FORCE_GLOBAL_CHAT("Force Global Chat", false, "Talk in Global by default, and Local when you use #.", "Talk in Local by default, and Global when you use #.")
    //    TRADE_CHAT("See Trade Chat", true, "See Trade Chat messages. (sent by using a $ before chat message)", "Hide Trade Chat messages."),
    ;

    private String display;
    private boolean defaultVal;
    private String t, f;

    SakiOption(String display, boolean def, String t, String f) {
        this.display = display;
        this.defaultVal = def;
        this.t = t;
        this.f = f;
    }

    public String getDesc(boolean status) {
        return status ? t : f;
    }

    public String getDisplay() {
        return display;
    }

    public boolean getDefault() {
        return defaultVal;
    }

    public static SakiOption get(String s) {
        for (SakiOption so : SakiOption.values())
            if (so.toString().equalsIgnoreCase(s))
                return so;
        return null;
    }
}
