package com.github.dnaka91.bender.raw;

import com.sun.jna.Structure;

import java.util.StringJoiner;

@Structure.FieldOrder({"year", "month", "day"})
public class CDate extends Structure {
    public int year = 0;
    public int month = 0;
    public int day = 0;

    @Override
    public String toString() {
        return "CDate{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }
}
