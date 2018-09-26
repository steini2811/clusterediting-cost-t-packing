package de.unmr.bacluster.searchtree;

import java.util.ArrayList;
import java.util.Optional;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.P3;
import de.unmr.bacluster.models.Result;
import de.unmr.bacluster.models.modifications.Modification;

public class ClusterEditingSimple extends ClusterEditing {

    @Override
    protected Result ceBranch(final Graph graph, final int k) {
        final Optional<P3> p3 = graph.getP3();

        if (!p3.isPresent()) {
            return new Result(Optional.of(new ArrayList<>()), recursiveSteps);
        }

        if (k < 1) {
            return new Result(Optional.empty(), recursiveSteps);
        }

        final P3 p3Object = p3.get();

        final int u = p3Object.getU();
        final int v = p3Object.getV();
        final int w = p3Object.getW();

        final ArrayList<Modification> modifications = new ArrayList<>();
        modifications.add(new Modification(true, new Edge(u, v)));
        modifications.add(new Modification(true, new Edge(v, w)));
        modifications.add(new Modification(false, new Edge(u, w)));

        for (final Modification mod : modifications) {
            recursiveSteps++;

            if (graph.modify(mod)) {
                if (!graph.hasP3()) {
                    return new Result(Optional.of(new ArrayList<>()), recursiveSteps);
                }

                final Result result = ceBranch(graph, k - 1);

                if (result.getList().isPresent()) {
                    return result;
                }

                graph.rollback();
            }
        }

        return new Result(Optional.empty(), recursiveSteps);
    }

}
