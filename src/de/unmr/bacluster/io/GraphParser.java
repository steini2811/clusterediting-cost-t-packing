package de.unmr.bacluster.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.unmr.bacluster.exceptions.ParseException;
import de.unmr.bacluster.models.Edge;
import de.unmr.bacluster.util.Debug;

public class GraphParser {
    private final Mapper mapper;

    public GraphParser(final Mapper mapper) {
        super();
        this.mapper = mapper;
    }

    public List<Edge> parseInput() throws IOException, ParseException {
        BufferedReader reader;
        if (Debug.isEclipseDebug()) {
            reader = new BufferedReader(new FileReader(new File(Debug.getFileName())));
        } else {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }

        final List<Edge> list = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            parseLine(list, line);
        }
        reader.close();

        final List<Edge> uniqueList = list.stream().filter(e -> e.getNodeA() != e.getNodeB())
                .collect(Collectors.toList());

        return uniqueList;
    }

    private void parseLine(final List<Edge> list, final String line) throws ParseException {
        if (!line.isEmpty()) {
            String node1 = null;
            String node2 = null;
            String weight = null;

            if (line.trim().isEmpty()) {
                return;
            }

            final String[] lineParts = line.split("\\s+");
            for (final String s : lineParts) {
                if (s.startsWith("#") || s.startsWith("%")) {
                    return;
                }

                if (s.isEmpty()) {
                    continue;
                }

                if (s.matches("^[A-Za-z0-9_]+$")) {
                    if (node1 == null) {
                        node1 = s;
                        continue;
                    } else if (node2 == null) {
                        node2 = s;
                        continue;
                    } else if (weight == null) {
                        weight = s;
                        continue;
                    } else {
                        throw new ParseException("Too many node names in one line.");
                    }
                } else {
                    if (node1 != null && node2 != null && weight == null) {
                        weight = s;
                        continue;
                    }
                    throw new ParseException("Illegal node name: " + s);
                }
            }

            if (node1 == null || node2 == null) {
                throw new ParseException("Not enough node names in one line.");
            }

            list.add(new Edge(mapper.getInteger(node1), mapper.getInteger(node2)));
        }
    }
}
