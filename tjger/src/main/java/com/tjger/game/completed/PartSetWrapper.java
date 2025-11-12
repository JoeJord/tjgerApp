package com.tjger.game.completed;

import com.tjger.gui.completed.ColorValuePart;
import com.tjger.gui.completed.GameElement;
import com.tjger.gui.completed.GameElementSet;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;

import java.util.Objects;

/**
 * Wrapper for {@link PartSet} so that {@link PartSetConstructor} can extend {@link GameElementSetConstructor}.<br>
 * {@link GameElementSetConstructor} needs a class which extends {@link GameElementSet}.<br>
 * {@link PartSet} extends {@link Part} which extends {@link GameElement} but is not an extension from {@link GameElementSet}.
 *
 * @see GameElementSetConstructor
 * @see GameElementSet
 * @see PartSetConstructor
 * @see PartSet
 */
public class PartSetWrapper<T extends PartSet> extends GameElementSet {
    /**
     * The partset to wrap.
     */
    private final T partSet;

    /**
     * Constructs a new instance.
     *
     * @param partSet The partset to wrap.
     */
    public PartSetWrapper(T partSet) {
        super(partSet.getType(), partSet.getName(), partSet.isHidden(), partSet.isProTeaser(), partSet.getProductId());
        this.partSet = partSet;
    }

    /**
     * Returns the wrapped partset.
     *
     * @return The wrapped partset.
     */
    public T getPartSet() {
        return partSet;
    }

    @Override
    public void addElement(GameElement element) {
        partSet.addPart((ColorValuePart) element);
    }

    @Override
    public boolean isEmpty() {
        return partSet.getParts().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PartSetWrapper<?> that = (PartSetWrapper<?>) o;
        return Objects.equals(partSet, that.partSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), partSet);
    }
}
