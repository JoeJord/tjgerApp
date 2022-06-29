package com.tjger.lib;

/**
 * The interface that is necessary for calculating the path,
 * like finding the neighbours of a field or get their distance weight.
 * Has to be implemented to act with the own fields the way shall be calculated.
 * The fields have to implement the equals method.
 *
 * @param <T> the type of the field objects
 * @author hagru
 */
public interface ShortestPathMethods<T> {

    /**
     * @return All fields to look for.
     */
    public T[] getAllFields();

    /**
     * Get the index of a field in the field list that is returned by the
     * <code>getAllFields()</code> method.
     *
     * @param field Any field of the field list.
     * @return The index of this field (or -1 if does not exist).
     * @see #getAllFields()
     */
    public int getIndexOfField(T field);

    /**
     * @return The starting field.
     */
    public T getStartField();

    /**
     * @return The target field.
     */
    public T getTargetField();

    /**
     * @param field A field.
     * @return All neighbours of a field.
     */
    public T[] getNeighbours(T field);

    /**
     * Get the weight between fields, it is guaranteed that both fields are neighbours.
     *
     * @param from A start field.
     * @param to A neighbour field.
     * @return The weight between the fields (default is 1).
     */
    public int getWeight(T from, T to);

    /**
     * @return The weight to break searching for the path, if 0 (or lower) it's searched until a way is found.
     */
    public int getMaxWeightToSearch();

}
