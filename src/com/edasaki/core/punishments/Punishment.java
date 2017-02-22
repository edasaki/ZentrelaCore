package com.edasaki.core.punishments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.ChatColor;

import com.edasaki.core.utils.RTime;

public class Punishment {
    public int id;
    public String name;
    public String uuid;
    public String ip;
    public PunishmentType type;
    public ZonedDateTime endTime;
    public String reason;
    public String giver;
    public ZonedDateTime startTime;
    public boolean cancelled;
    public String canceller;
    public ZonedDateTime cancelTime;

    public void load(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.uuid = rs.getString("uuid");
        this.ip = rs.getString("ip");
        this.type = PunishmentType.valueOf(rs.getString("type"));
        this.endTime = ZonedDateTime.parse(rs.getString("endTime"));
        this.reason = rs.getString("reason");
        this.giver = rs.getString("giver");
        this.startTime = ZonedDateTime.parse(rs.getString("startTime"));
        this.cancelled = Boolean.parseBoolean(rs.getString("cancelled"));
        if (this.cancelled) {
            this.canceller = rs.getString("canceller");
            this.cancelTime = ZonedDateTime.parse(rs.getString("cancelTime"));
        }
    }
    
    /**
     * Check to see if punishment was cancelled or is over
     * @return true if punishment is still in effect
     */
    public boolean isValid() {
        if(cancelled)
            return false;
        ZonedDateTime now = ZonedDateTime.now();
        return endTime.isAfter(now);
    }
    
    public String getDisplay() {
        StringBuilder sb = new StringBuilder("PID: ");
        sb.append(id);
        sb.append(". ");
        if(cancelled)
            sb.append(ChatColor.STRIKETHROUGH);
        sb.append(type.getCap());
        sb.append(". ");
        if(endTime.isAfter(ZonedDateTime.now())) {
            sb.append("Ends at ");
        } else {
            sb.append("Ended at ");
        }
        sb.append(endTime.format(RTime.PUNISHMENT_FORMATTER));
        sb.append(". Given by ");
        sb.append(giver);
        sb.append(" for: ");
        sb.append(reason);
        sb.append('.');
        if(cancelled) {
            sb.append(ChatColor.RESET);
            sb.append(" Pardoned by ");
            sb.append(canceller);
            sb.append('.');
        }
        return sb.toString();
    }
    
    public String getReason() {
        StringBuilder sb = new StringBuilder("You were ");
        sb.append(type.getTextPast());
        sb.append(" at ");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss' on 'yyyy-MM-dd zzz");
        sb.append(startTime.format(dtf));
        sb.append(" by ");
        sb.append(giver);
        sb.append(" for: ");
        sb.append(reason);
        sb.append(". Your ");
        sb.append(type.getText());
        sb.append(" expires at ");
        sb.append(endTime.format(dtf));
        sb.append('.');
        return sb.toString();
    }
}
