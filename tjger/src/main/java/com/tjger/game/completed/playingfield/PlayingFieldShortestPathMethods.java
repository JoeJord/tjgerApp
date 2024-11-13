package com.tjger.game.completed.playingfield;

import com.tjger.lib.AbstractShortestPathMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Shortest path calculation for a playing field.
 *
 * @author hagru
 */
public class PlayingFieldShortestPathMethods extends AbstractShortestPathMethods<SingleField> {

    private final PlayingField playingField;
    private final List<SingleField> fieldList;
    /**
     * The condition a connection to a target field must met. The parameters for the {@link BiPredicate} are the origin and the target fields.
     */
    private BiPredicate<SingleField, SingleField> condition;

    /**
     * Constructs a new instance.
     *
     * @param field    The playing field.
     * @param start    The start field of the path.
     * @param target   The target field of the path.
     * @param maxDepth The maximum number of steps in the path.
     */
    public PlayingFieldShortestPathMethods(PlayingField field, SingleField start, SingleField target, int maxDepth) {
        this(field, start, target, maxDepth, (connectionOrigin, connectionTarget) -> true);
    }

    public PlayingFieldShortestPathMethods(PlayingField field, SingleField start, SingleField target, int maxDepth, BiPredicate<SingleField, SingleField> condition) {
        super(start, target, maxDepth);
        this.playingField = field;
        this.fieldList = new ArrayList<>(playingField.getFields());
        this.condition = condition;
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
        return playingField.getNeighbours(field, condition).toArray(new SingleField[0]);
    }

    @Override
    public int getWeight(SingleField from, SingleField to) {
        return playingField.getConnectionWeight(from, to);
    }
}
