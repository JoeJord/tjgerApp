package com.tjger.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implements the algorithm for finding a shortest path.
 * Needs an implementation of ShortestPathMethods to handle
 * the specific fields that are used and returns a ShortestPath object.
 *
 * @author hagru
 */
public class ShortestPathFinder {

    private static final int NOVISIT = 0; // node was visited
    private static final int VISITED = 1; // node wasn't visited


    private ShortestPathFinder() {
        super();
    }

    /**
     * Find the shortest path, implements the shortest-path-algorithm.
     *
     * @param methods The object that holds the context specific implentations of necessary methods for the shortest path algorithm.
     * @return A shortest path object or null if no path is available.
     */
    public static <T> ShortestPath<T> find(ShortestPathMethods<T> methods) {
        T[] fieldList = methods.getAllFields();
        int maxNrNodes = fieldList.length;
        int from = methods.getIndexOfField(methods.getStartField());
        int to = methods.getIndexOfField(methods.getTargetField());
        if (from >= 0 && to >= 0) {
            // if "from" and "to" are the same, create a simple shortest path and return it
            if (from == to){
                List<T> path = new ArrayList<>();
                path.add(methods.getStartField());
                return new ShortestPath<>(path, 0);
            } else {
                // declare and init the variables for the algorithm
                int[] parent = createIntList(maxNrNodes, -1);        // field for finding the parent
                int[] weight = createIntList(maxNrNodes, 0);         // weight from the origin node
                int[] allNodes = createIntList(maxNrNodes, NOVISIT); // remembers which nodes are already visited
                int minW=-1;                    // actually shortest way
                int nearN=-1;                   // neighbour node with shortest way
                int fromN=-1;                   // node from where to the shortest way
                boolean neighbourFound=false;   // was a neighbour found in this turn?
                int maximumWeight = methods.getMaxWeightToSearch();
                allNodes[from]=VISITED;
                do {
                    neighbourFound=false;
                    // go through all visited nodes
                    for (int i=0; i<maxNrNodes; i++) {
                        if (allNodes[i] == VISITED) {
                            // search for not visited neighbours
                            T[] neighbours = methods.getNeighbours(fieldList[i]);
                            for (int j=0; j<neighbours.length;j++) {
                                int neighbourIndex = methods.getIndexOfField(neighbours[j]);
                                if (allNodes[neighbourIndex]==NOVISIT) {
                                    int newW = methods.getWeight(fieldList[i], fieldList[neighbourIndex]);
                                    // look if there is the shortest new path
                                    if (!neighbourFound || (newW + weight[i])<minW) {
                                        fromN = i;
                                        nearN = neighbourIndex;
                                        minW = weight[i] + newW;
                                        neighbourFound = true;
                                    }
                                }
                            }
                        }
                    }
                    // Remember the new shortest path
                    if (neighbourFound) {
                        allNodes[nearN]=VISITED;
                        weight[nearN] = minW;
                        parent[nearN]=fromN;
                        if (maximumWeight>0 && maximumWeight<minW) {
                            // break searching as maximum to look for weight is reached
                            break;
                        }
                    } else {
                        // there was no neighbour found in this turn
                        // node "from" is not reachable from node "to", return null
                        return null;
                    }
                } while (nearN != to);  // end loop when node "to" was found
                // a path was found, insert the origin node
                List<T> path = new ArrayList<>();
                path.add(fieldList[to]);
                // insert nodes in the correct order
                int i=to;
                do {
                    path.add(fieldList[parent[i]]);
                    i = parent[i];
                } while (i!=from);
                // return the new shortest path, reverse order
                Collections.reverse(path);
                return new ShortestPath<>(path, minW);
            }
        }
        return null;
    }

    /**
     * @param fieldLength The length of the list.
     * @param defaultValue The default value for each entry.
     * @return The new created list.
     */
    private static int[] createIntList(int listLength, int defaultValue) {
        int[] list = new int[listLength];
        Arrays.fill(list, defaultValue);
        return list;
    }

}
