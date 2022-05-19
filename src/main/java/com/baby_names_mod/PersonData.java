package com.baby_names_mod;

import java.io.Serializable;
import java.util.List;

public class PersonData implements Serializable {
    public final String name;
    public final List<Integer> years;

    public PersonData(String name, List<Integer> years) {
        this.name = name;
        this.years = years;
    }
}