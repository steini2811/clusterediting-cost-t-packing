package de.unmr.bacluster.searchtree;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.models.Graph;
import de.unmr.bacluster.models.P3;
import de.unmr.bacluster.models.Result;
import de.unmr.bacluster.models.modifications.Modification;
import de.unmr.bacluster.models.modifications.ModificationGroup;

public class ClusterEditingRefined extends ClusterEditing {

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

        final Set<Edge> editableEdges = p3Object.getEditableEdges(graph);

        final ArrayList<ModificationGroup> groups = new ArrayList<>();

        if (editableEdges.size() == 1) {
            final Edge edge = editableEdges.iterator().next();
            groups.add(new ModificationGroup(1, asList(new Modification(graph.getState(edge).isConnected(), edge)),
                    asList(), asList()));
        } else {
            final int u = p3Object.getU();
            final int v = p3Object.getV();
            final int w = p3Object.getW();

            final Optional<Integer> optX = graph.getCommonNeighbor(u, w, v);

            if (!optX.isPresent()) {
                // C1
                // @formatter:off
                groups.add(new ModificationGroup(
                        11,
                        asList(
                                new Modification(true, new Edge(u, v))
                        ),
                        asList(
                                new Edge(u, w)
                        ),
                        asList()
                ));
                groups.add(new ModificationGroup(
                        12,
                        asList(
                                new Modification(true, new Edge(v, w))
                        ),
                        asList(
                                new Edge(u, w)
                        ),
                        asList()
                ));
                // @formatter:on
            } else {
                final int x = optX.get();
                if (graph.areNeighbors(v, x)) {
                    // C2
                    // @formatter:off
                    groups.add(new ModificationGroup(
                            21,
                            asList(
                                    new Modification(false, new Edge(u, w))
                            ),
                            asList(),
                            asList()
                    ));

                    groups.add(new ModificationGroup(
                            22,
                            asList(
                                    new Modification(true, new Edge(u, v)),
                                    new Modification(true, new Edge(u, x))
                            ),
                            asList(
                                    new Edge(u, w)
                            ),
                            asList()
                    ));

                    groups.add(new ModificationGroup(
                            23,
                            asList(
                                    new Modification(true, new Edge(u, v)),
                                    new Modification(true, new Edge(v, x)),
                                    new Modification(true, new Edge(w, x))
                            ),
                            asList(
                                    new Edge(u, w)
                            ),
                            asList(
                                    new Edge(u, x)
                            )
                    ));

                    groups.add(new ModificationGroup(
                            24,
                            asList(
                                    new Modification(true, new Edge(v, w)),
                                    new Modification(true, new Edge(w, x))
                            ),
                            asList(
                                    new Edge(u, w)
                            ),
                            asList(
                                    new Edge(v, u)
                            )
                    ));

                    groups.add(new ModificationGroup(
                            25,
                            asList(
                                    new Modification(true, new Edge(w, v)),
                                    new Modification(true, new Edge(v, x)),
                                    new Modification(true, new Edge(u, x))
                            ),
                            asList(
                                    new Edge(u, w)
                            ),
                            asList(
                                    new Edge(v, u),
                                    new Edge(w, x)
                            )
                    ));
                    // @formatter:on
                } else {
                    // C3
                    // @formatter:off
                    groups.add(new ModificationGroup(
                            31,
                            asList(
                                    new Modification(true, new Edge(u, v))
                            ),
                            asList(),
                            asList()
                    ));

                    groups.add(new ModificationGroup(
                            32,
                            asList(
                                    new Modification(true, new Edge(v, w)),
                                    new Modification(true, new Edge(u, x))
                            ),
                            asList(
                                    new Edge(u, w)
                            ),
                            asList(
                                    new Edge(u, v)
                            )
                    ));

                    groups.add(new ModificationGroup(
                            33,
                            asList(
                                    new Modification(true, new Edge(v, w)),
                                    new Modification(false, new Edge(v, x)),
                                    new Modification(true, new Edge(w, x))
                            ),
                            asList(
                                    new Edge(u, w)
                            ),
                            asList(
                                    new Edge(u, v),
                                    new Edge(u, x)
                            )
                    ));

                    groups.add(new ModificationGroup(
                            34,
                            asList(
                                    new Modification(true, new Edge(u, x)),
                                    new Modification(true, new Edge(w, x)),
                                    new Modification(false, new Edge(u, w))
                            ),
                            asList(
                                    new Edge(v, x)
                            ),
                            asList(
                                    new Edge(u, v),
                                    new Edge(v, w)
                            )
                    ));

                    groups.add(new ModificationGroup(
                            35,
                            asList(
                                    new Modification(false, new Edge(u, w)),
                                    new Modification(false, new Edge(v, x))
                            ),
                            asList(),
                            asList(
                                    new Edge(v, u),
                                    new Edge(v, w),
                                    new Edge(u, x),
                                    new Edge(w, x)
                            )
                    ));
                    // @formatter:on
                }
            }
        }

        for (final ModificationGroup group : groups) {
            recursiveSteps++;

            final int cost = group.getNeededSteps();
            if (cost > k) {
                continue;
            }

            if (graph.modify(group)) {
                if (!graph.hasP3()) {
                    return new Result(Optional.of(new ArrayList<>()), recursiveSteps);
                }

                final int newK = k - cost;

                if (newK < 1 || graph.getEdgeDisjointLowerBound() > newK) {
                    graph.rollback(group);
                    continue;
                }

                final Result result = ceBranch(graph, newK);

                if (result.getList().isPresent()) {
                    return result;
                }

                graph.rollback(group);
            }
        }

        return new Result(Optional.empty(), recursiveSteps);
    }

}
