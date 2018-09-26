package de.unmr.bacluster.models.datareduction;

import java.util.HashSet;
import java.util.Set;

import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.modifications.Modification;

public abstract class DataReduction {
    protected Graph graph;
    protected Set<Modification> modifications;

    protected DataReduction(final Graph graph) {
        super();
        this.graph = graph;
        this.modifications = new HashSet<>();
    }

    /**
     * Returns how much not-free modifications were made while reducing data.
     *
     * @return costs
     */
    public int getCosts() {
        return modifications.size();
    }

    /**
     * Executes the data reduction rule that the implementing class declares.
     * Stores every change in the object to be able the rollback its changes.
     *
     * @return Returns {@code true} if the graph was changed after executing the
     *         reduction rule
     */
    public abstract boolean reduce();

    /**
     * Rollbacks all of the changes caused by the execution of the data
     * reduction rule.
     */
    public void rollback() {
        graph.rollback(getCosts());
        modifications.clear();
    }
}
