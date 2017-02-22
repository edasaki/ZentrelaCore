package com.edasaki.core.utils;

import java.time.format.DateTimeFormatter;

public class RTime {

    public static final DateTimeFormatter JOIN_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss' on 'yyyy-MM-dd zzz");
    public static final DateTimeFormatter PUNISHMENT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss' on 'yyyy-MM-dd zzz");

    public static String formatMinutes(int timeMinutes) {
        int days = timeMinutes / 60 / 24;
        int hours = timeMinutes / 60 % 24;
        int mins = timeMinutes % 60;
        StringBuilder sb = new StringBuilder();
        boolean hasDays = days > 0;
        boolean hasHours = hours > 0;
        boolean hasMins = mins > 0;
        if (hasDays) {
            sb.append(days);
            sb.append(" day");
            if (days != 1)
                sb.append('s');
        }
        if (hasHours) {
            if (hasDays) {
                if (hasMins)
                    sb.append(", ");
                else
                    sb.append(" and ");
            }
            sb.append(hours);
            sb.append(" hour");
            if (hours != 1)
                sb.append('s');
        }
        if (hasMins) {
            if (hasDays || hasHours)
                sb.append(" and ");
            sb.append(mins);
            sb.append(" minute");
            if (mins != 1)
                sb.append('s');
        } else {
            if (!(hasMins || hasDays || hasHours))
                return "0 minutes";
        }
        return sb.toString();
    }

}
