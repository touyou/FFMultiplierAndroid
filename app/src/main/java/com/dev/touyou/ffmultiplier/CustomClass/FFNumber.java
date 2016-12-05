package com.dev.touyou.ffmultiplier.CustomClass;

/**
 * Created by touyou on 2016/11/10.
 */
public enum FFNumber {
    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), ANUM(10), BNUM(11), CNUM(12), DNUM(13), ENUM(14), FNUM(15);

    private final int id;

    private FFNumber(final int id) {
        this.id = id;
    }

    public static FFNumber valueOf(int id) {
        for (FFNumber fn: values()) {
            if (fn.getId() == id) {
                return fn;
            }
        }
        throw new IllegalArgumentException("it's not hex number.");
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        if (id < 10) {
            return String.valueOf(id);
        } else {
            switch (id) {
                case 10:
                    return "A";
                case 11:
                    return "B";
                case 12:
                    return "C";
                case 13:
                    return "D";
                case 14:
                    return "E";
                case 15:
                    return "F";
                default:
                    return "UNKNOWN";
            }
        }
    }
}
