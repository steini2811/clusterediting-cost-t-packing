package de.unmr.bacluster.models;

import java.util.Collection;
import java.util.Optional;

public class Result {
    private final Optional<Collection<Edge>> list;
    private final int recursiveSteps;

    public Result(final Optional<Collection<Edge>> list, final int recursiveSteps) {
        super();
        this.list = list;
        this.recursiveSteps = recursiveSteps;
    }

    public Optional<Collection<Edge>> getList() {
        return list;
    }

    public int getRecursiveSteps() {
        return recursiveSteps;
    }
}
