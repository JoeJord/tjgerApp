package com.tjger.game.completed.playingfield;

import com.tjger.lib.AbstractShortestPathMethods;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public SingleField[] getAllFields() {
        return fieldList.toArray(new SingleField[0]);
    }

    @Override
    public int getIndexOfField(SingleField field) {
        return fieldList.indexOf(field);
    }

    @Override
    public SingleField[] getNeighbours(SingleField field) {
        return playingField.getNeighbours(field).toArray(new SingleField[0]);
    }

    @Override
    public int getWeight(SingleField from, SingleField to) {
        return playingField.getConnectionWeight(from, to);
    }
}
