package com.tjger.game.completed.imagereader;

import org.w3c.dom.Node;

import com.tjger.gui.completed.ImageEffect;
import com.tjger.gui.completed.Part;

import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Provides all common methods for reading an image affect for a part.
 *
 * @param <T> the image effect type
 * @author hgr
 */
public abstract class AbstractImageEffectReader<T extends ImageEffect> {

    private final String attributeName;
    private Node currentNode;

    /**
     * Let sub-classes create instances.
     *
     * @param attributeName the name of the attribute holding the data.
     */
    protected AbstractImageEffectReader(String attributeName) {
        super();
        this.attributeName = attributeName;
    }

    /**
     * Returns the current node for letting sub-classes access the node when overriding abstract methods.
     *
     * @return the current node, can be null if called before first node is accessed.
     */
    protected Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Try to read the effect data from a node.
     *
     * @param node the node to read the effect data from.
     * @return an effect object or null if there is no effect definition or it is invalid.
     */
    public final T getEffectDefinition(Node node) {
    	currentNode = node;
        String effectValue = HGBaseXMLTools.getAttributeValue(node, attributeName);
        return readEffectDefinition(effectValue);
    }

    /**
     * Reads the effect data from a string value.
     *
     * @param effectValue the string value to read the data from
     * @return the effect object or null if value is invalid
     */
    abstract protected T readEffectDefinition(String effectValue);

    /**
     * Try to read the effect data from a node. If there is no effect defined, then the
     * higher level effect will be taken.
     *
     * @param node the node to read the effect data from.
     * @param higherLevelEffect the effect of the higher level part, can be null.
     * @return the effect of the node or the higher level effect, can be null.
     */
    public final T getEffectDefinition(Node node, T higherLevelEffect) {
    	T currentEffect = getEffectDefinition(node);
        return (currentEffect==null)? higherLevelEffect : currentEffect;
    }

    /**
     * Set the effect for a part if there is one to set.
     *
     * @param part the part to set the effect for.
     * @param node the node of the part to read the effect from.
     * @param higherLevelEffect a higher level effect if there is no defined in the node, can be null.
     */
    public final void setEffectForPart(Part part, Node node, ImageEffect higherLevelEffect) {
        currentNode = node;
        @SuppressWarnings("unchecked")
        T currentEffect = getEffectDefinition(node, (T) higherLevelEffect);
        if (currentEffect != null) {
            setEffectForPart(part, currentEffect);
        }
    }

    /**
     * Sets the effect for the part. This method is only called if the effect is not null.
     *
     * @param part the part to get the effect set.
     * @param effect the effect to set.
     */
    abstract protected void setEffectForPart(Part part, T effect);

    /**
     * Returns the effect from the current part.
     *
     * @param part the part, must not be null.
     * @return the effect, can be null.
     */
    abstract public T getEffectFromPart(Part part);

}