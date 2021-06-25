package com.github.dnaka91.bender.raw;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.StringJoiner;

@Structure.FieldOrder({"street", "houseNumber", "city", "country", "postalCode", "details", "detailsLength"})
public class CAddress extends Structure {
    @NotNull
    public String street = "";
    @NotNull
    public String houseNumber = "";
    @NotNull
    public String city = "";
    @NotNull
    public String country = "";
    @NotNull
    public String postalCode = "";
    @NotNull
    public Pointer details = Pointer.NULL;
    public int detailsLength = 0;

    @Override
    public String toString() {
        return "CAddress{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", details=" + details +
                ", detailsLength=" + detailsLength +
                '}';
    }
}
