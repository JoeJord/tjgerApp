package com.tjger.lib;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This interface is used by the {@code XmlUtil} to read and write collections from/to XML.
 *
 * @param <V> The type of the value objects.
 */
public interface XmlCollectionConverter<V> {
    /**
     * Writes the value into the node.
     *
     * @param itemNode The XML node for the item.
     * @param value The value to write.
     */
    public void writeValue(Element itemNode, V value);

    /**
     * Reads the value from the node.
     *
     * @param itemNode The node to extract the value from.
     * @return The value object, can also be null to be used as value.
     */
    public V readValue(Node itemNode);
}
