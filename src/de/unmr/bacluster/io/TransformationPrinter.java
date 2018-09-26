package de.unmr.bacluster.io;

import java.util.Collection;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Result;
import de.unmr.bacluster.util.Debug;

public class TransformationPrinter {
    private final Mapper mapper;

    public TransformationPrinter(final Mapper mapper) {
        super();
        this.mapper = mapper;
    }

    public void outputTransformation(final Result result) {
        if (!result.getList().isPresent()) {
            return;
        }

        final StringBuilder builder = new StringBuilder();

        for (final Edge edge : result.getList().get()) {
            builder.append(mapper.toName(edge.getNodeA()));
            builder.append(" ");
            builder.append(mapper.toName(edge.getNodeB()));
            builder.append("\n");
        }
        builder.append("#recursive steps: ");
        builder.append(result.getRecursiveSteps());
        builder.append("\n");
        Debug.log("#size of solution: " + result.getList().get().size());

        System.out.println(builder.toString());
    }

    public void printEdgeList(final Collection<Edge> list) {
        final StringBuilder builder = new StringBuilder();

        for (final Edge edge : list) {
            builder.append(edge.getNodeA());
            builder.append(" ");
            builder.append(edge.getNodeB());
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }
}
