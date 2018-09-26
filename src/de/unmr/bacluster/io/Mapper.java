package de.unmr.bacluster.io;

import java.util.ArrayList;
import java.util.List;

public class Mapper {
    private static Mapper instance;

    public static Mapper getInstance() {
        return Mapper.instance;
    }

    private final List<String> mapping = new ArrayList<>();

    public Mapper() {
        super();
        Mapper.instance = this;
    }

    public int getInteger(final String name) {
        int index = mapping.indexOf(name);
        if (index == -1) {
            mapping.add(name);
            index = mapping.size() - 1;
        }
        return index;
    }

    public String toName(final int integer) {
        return mapping.get(integer);
    }
}
