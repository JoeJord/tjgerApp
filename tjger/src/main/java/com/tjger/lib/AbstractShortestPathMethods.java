package com.tjger.lib;

import java.util.Arrays;

/**
 * Simple implementation of the shortest path methods that only needs a handfull methods for the current context.
 *
 * @param <T> the type of the field objects
 * @author hagru
 */
abstract public class AbstractShortestPathMethods<T> implements ShortestPathMethods<T> {

    protected T startField;
    protected T targetField;
    protected int maxSearchDepth;

    public AbstractShortestPathMethods(T startField, T targetField) {
        this(startField, targetField, 0);
    }

    public AbstractShortestPathMethods(T startField, T targetField, int maxSearchDepth) {
        super();
        this.startField = startField;
        this.targetField = targetField;
        this.maxSearchDepth = maxSearchDepth;
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getStartField()
     */
    @Override
    public T getStartField() {
        return startField;
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getTargetField()
     */
    @Override
    public T getTargetField() {
        return targetField;
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getWeight(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getWeight(T from, T to) {
        return 1;
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getMaxWeightToSearch()
     */
    @Override
    public int getMaxWeightToSearch() {
        return maxSearchDepth;
    }

    /**
     * A simple method to get the index of a field in the field list.
     * Can be used by getIndexOfField(Object).
     *
     * @param fieldList A list with all fields.
     * @param field The field to look for.
     * @param sortedList True if the list is sorted and the fields are comparable (better performance), false if not.
     * @return The index of the field in the list or -1.
     */
    protected int getIndexOfField(T[] fieldList, T field, boolean sortedList) {
        if (sortedList) {
            int index = Arrays.binarySearch(fieldList, field);
            if (index>=0 && field.equals(fieldList[index])) {
                return index;
            } else {
                return -1;
            }
        } else {
            return Arrays.asList(fieldList).indexOf(field);
        }
    }
}
