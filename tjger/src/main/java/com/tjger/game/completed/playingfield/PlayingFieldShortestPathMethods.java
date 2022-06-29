package com.tjger.game.completed.playingfield;

import java.util.ArrayList;
import java.util.List;

import com.tjger.lib.AbstractShortestPathMethods;

/**
 * Shortest path calculation for a playing field.
 *
 * @author hagru
 */
public class PlayingFieldShortestPathMethods extends AbstractShortestPathMethods<SingleField> {

    private final PlayingField playingField;
    private final List<SingleField> fieldList;

    public PlayingFieldShortestPathMethods(PlayingField field, SingleField start, SingleField target, int maxDepth) {
        super(start, target, maxDepth);
        this.playingField = field;
        this.fieldList = new ArrayList<>(playingField.getFields());
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getAllFields()
     */
    @Override
    public SingleField[] getAllFields() {
        return fieldList.toArray(new SingleField[0]);
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getIndexOfField(java.lang.Object)
     */
    @Override
    public int getIndexOfField(SingleField field) {
        return fieldList.indexOf(field);
    }

    /* (non-Javadoc)
     * @see tjger.lib.ShortestPathMethods#getNeighbours(java.lang.Object)
     */
    @Override
    public SingleField[] getNeighbours(SingleField field) {
        return playingField.getNeighbours(field).toArray(new SingleField[0]);
    }
    
    /* (non-Javadoc)
     * @see tjger.lib.AbstractShortestPathMethods#getWeight(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getWeight(SingleField from, SingleField to) {
        return playingField.getConnectionWeight(from, to);
    }

}
