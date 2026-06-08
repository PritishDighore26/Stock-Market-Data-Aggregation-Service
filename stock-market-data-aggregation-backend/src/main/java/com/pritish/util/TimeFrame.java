package com.pritish.util;

public enum TimeFrame {

    M1(1),
    M5(5),
    M15(15),
    M30(30),
    H1(60),
    D1(1440);

    private final int minutes;

    TimeFrame(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
}