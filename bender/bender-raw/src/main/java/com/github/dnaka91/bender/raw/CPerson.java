package com.github.dnaka91.bender.raw;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import org.jetbrains.annotations.NotNull;

@Structure.FieldOrder({"id", "name", "gender", "birthday", "addresses", "addressesLength", "weight", "totalSteps"})
public class CPerson extends Structure {
    public long id = 0;
    @NotNull
    public CName name = new CName();
    public int gender = 0;
    @NotNull
    public CDate birthday = new CDate();
    @NotNull
    public Pointer addresses = Pointer.NULL;
    public int addressesLength = 0;
    public double weight = 0.0;
    public long totalSteps = 0;

    @Override
    public String toString() {
        return "CPerson{" +
                "id=" + id +
                ", name=" + name +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", addresses=" + addresses +
                ", addressesLength=" + addressesLength +
                ", weight=" + weight +
                ", totalSteps=" + totalSteps +
                '}';
    }
}
