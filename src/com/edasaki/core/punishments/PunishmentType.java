package com.edasaki.core.punishments;

public enum PunishmentType {

    MUTE("mute", "muted", "Mute"), BAN("ban", "banned", "Ban"), IPBAN("IP ban", "IP banned", "IP Ban");
    
    private String text;
    private String past;
    private String cap;
    
    private PunishmentType(String text, String past, String cap) {
        this.text = text;
        this.past = past;
        this.cap = cap;
    }
    
    public String getText() {
        return text;
    }

    public String getTextPast() {
        return past;
    }
    
    public String getCap() {
        return cap;
    }
}
