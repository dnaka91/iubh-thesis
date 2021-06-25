package com.github.dnaka91.bender.safer_ffi;

import com.sun.jna.Structure;

import org.jetbrains.annotations.NotNull;

@Structure.FieldOrder({"id", "name"})
public class CPerson extends Structure {
    public long id = 0;
    @NotNull
    public String name = "";

    @NotNull
    @Override
    public String toString() {
        return "CPerson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
