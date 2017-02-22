package com.edasaki.core.utils;

public class RTicks {

    private static final int HOURS = 20 * 60 * 60;
    private static final int MINUTES = 20 * 60;
    private static final int SECONDS = 20;

    public static int fromMS(long millis) {
        return (int) (Math.ceil(millis / 50));
    }

    /*
     * int converters for ticks
     */

    public static int hms(int h, int m, int s) {
        return hours(h) + minutes(m) + seconds(s);
    }

    public static int hours(int n) {
        return HOURS * n;
    }

    public static int minutes(int n) {
        return MINUTES * n;
    }

    public static int seconds(int n) {
        return SECONDS * n;
    }

    /*
     * double converters for ticks
     */

    public static int hms(double h, double m, double s) {
        return hours(h) + minutes(m) + seconds(s);
    }

    public static int hours(double n) {
        return (int) (HOURS * n);
    }

    public static int minutes(double n) {
        return (int) (MINUTES * n);
    }

    public static int seconds(double n) {
        return (int) (SECONDS * n);
    }
}
