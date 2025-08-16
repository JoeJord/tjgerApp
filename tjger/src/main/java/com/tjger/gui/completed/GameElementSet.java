package com.tjger.gui.completed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameElementSet extends GameElement {
    /**
     * The list of elements in the list.
     */
    private final List<GameElement> elements = new ArrayList<>();

    /**
     * Constructs a new instance.
     *
     * @param elementType The type of the element set.
     * @param name        The name of the element set.
     * @param hidden      The flag if the element set is hidden.
     * @param proTeaser   The flag if the element set is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    protected GameElementSet(String elementType, String name, boolean hidden, boolean proTeaser) {
        super(elementType, name, hidden, proTeaser);
    }

    /**
     * Returns {@code true} if the set is empty.
     *
     * @return {@code true} if the set is empty.
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Adds the specified element to the set.
     *
     * @param element The element to add.
     */
    public void addElement(GameElement element) {
        elements.add(element);
    }

    /**
     * Returns all elements from this set.
     *
     * @return All elements from this set.
     */
    public List<GameElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof GameElementSet) && (elements.equals(((GameElementSet) other).elements))
                && (super.equals(other));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements.hashCode());
    }
}
