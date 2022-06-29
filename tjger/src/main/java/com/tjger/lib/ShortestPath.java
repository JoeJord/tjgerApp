package com.tjger.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the information about a shortest path.
 *
 * @param <T> the type of the field objects
 * @author hagru
 */
public class ShortestPath<T> {

    final private List<T> path;
    final private int pathWeight;

    public ShortestPath(List<T> path, int pathWeight) {
        super();
        this.path = new ArrayList<>(path);
        this.pathWeight = pathWeight;
    }

    /**
     * Returns an unmodifiable list of the path.<p>
     * 
     * @return The shortest path, the array represents the fields used at ShortestPathFinder.
     */
    public List<T> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * @return The weight (length) of the shortest path.
     */
    public int getPathWeight() {
        return pathWeight;
    }

}
