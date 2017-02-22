package com.edasaki.core.utils;

import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.ChatColor;

public class RFormatter {

    public static String tierToRoman(int tier) {
        switch (tier) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "??";
        }
        return "??";
    }

    private static final int MAX_LENGTH = 32;

    /**
     * Automatically fetches the last color code used at the end of a line
     * when switching to a new line.
     */
    public static ArrayList<String> stringToLore(String s) {
        ArrayList<String> list = new ArrayList<String>();
        if (s == null) {
            list.add("");
            return list;
        }
        final int index = MAX_LENGTH;
        s = s.trim();
        while (true) {
            if (index >= s.length()) {
                list.add(s);
                break;
            } else {
                if (s.substring(0, index).contains(" ")) {
                    String strip = s.substring(0, s.substring(0, index).lastIndexOf(' '));
                    s = s.substring(s.substring(0, index).lastIndexOf(' ') + 1);
                    String lastColors = ChatColor.getLastColors(strip);
                    s = lastColors + s;
                    list.add(strip);
                } else {
                    String strip = s.substring(0, index - 1) + "-";
                    s = s.substring(index);
                    String lastColors = ChatColor.getLastColors(strip);
                    s = lastColors + s;
                    list.add(strip);
                }
            }
        }
        if (list.size() == 0)
            list.add("");
        return list;
    }

    public static ArrayList<String> stringToLore(String s, Object prefix) {
        ArrayList<String> lines = new ArrayList<String>();
        Scanner scan = new Scanner(s);
        StringBuilder temp = new StringBuilder("");
        boolean empty = true;
        while (scan.hasNext()) {
            empty = false;
            String toAppend = scan.next();
            if (toAppend.contains("@@")) {
                String[] data = toAppend.split("@@");
                if (data.length > 0) {
                    toAppend = data[0];
                    if (temp.length() + (toAppend + " ").length() > MAX_LENGTH) {
                        lines.add(prefix.toString() + temp.toString());
                        temp.setLength(0);
                        temp.append(toAppend + " ");
                    } else {
                        temp.append(toAppend + " ");
                    }
                    lines.add(prefix.toString() + temp.toString());
                    lines.add("");
                    temp.setLength(0);
                    if (data.length > 1)
                        temp.append(data[1] + " ");
                } else {
                    lines.add(prefix.toString() + temp.toString());
                    lines.add("");
                    temp.setLength(0);
                }
            } else {
                if (temp.length() + (toAppend + " ").length() > MAX_LENGTH) {
                    lines.add(prefix.toString() + temp.toString());
                    temp.setLength(0);
                    temp.append(toAppend + " ");
                } else {
                    temp.append(toAppend + " ");
                }
            }
        }
        if (empty)
            lines.add("");
        if (temp.length() > 0)
            lines.add(prefix.toString() + temp.toString());
        scan.close();
        return lines;
    }

}
