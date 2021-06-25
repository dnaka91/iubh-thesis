package com.github.dnaka91.bender.raw;

import com.sun.jna.Structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

@Structure.FieldOrder({"title", "first", "middle", "last"})
public class CName extends Structure {
    @Nullable
    public String title = null;
    @NotNull
    public String first = "";
    @Nullable
    public String middle = null;
    @NotNull
    public String last = "";

    @Override
    public String toString() {
        return "CName{" +
                "title='" + title + '\'' +
                ", first='" + first + '\'' +
                ", middle='" + middle + '\'' +
                ", last='" + last + '\'' +
                '}';
    }
}
