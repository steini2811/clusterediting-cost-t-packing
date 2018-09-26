package de.unmr.bacluster;

import java.io.IOException;
import java.util.List;

import de.unmr.bacluster.exceptions.ParseException;
import de.unmr.bacluster.io.GraphParser;
import de.unmr.bacluster.io.Mapper;
import de.unmr.bacluster.io.TransformationPrinter;
import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Result;
import de.unmr.bacluster.searchtree.ClusterEditing;
import de.unmr.bacluster.searchtree.ClusterEditingRefined;
import de.unmr.bacluster.searchtree.ClusterEditingSimple;
import de.unmr.bacluster.util.Config;
import de.unmr.bacluster.util.Debug;

public class Main {

    public static void main(final String[] args) {
        Debug.setEclipseDebug(false);
        Debug.setFileName("data/example2.txt");

        if (Debug.isEclipseDebug()) {
            Config.init(new String[] {});
        } else {
            Config.init(args);
        }

        final Mapper mapper = new Mapper();
        final GraphParser parser = new GraphParser(mapper);
        final ClusterEditing clusterEditing = Config.useSimpleBranching() ? new ClusterEditingSimple()
                : new ClusterEditingRefined();
        final TransformationPrinter printer = new TransformationPrinter(mapper);

        List<Edge> edges;
        try {
            edges = parser.parseInput();
        } catch (final ParseException | IOException e) {
            e.printStackTrace();
            return;
        }

        final Result result = clusterEditing.ce(edges, Config.isCalc());

        printer.outputTransformation(result);
    }

}
