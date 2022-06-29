package com.tjger.lib;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * An XML collection converter that reads the elements from attribute values and therefore need only methods to convert the type of {@code <V>} to {@code String} and back.
 *
 * @param <V> The type of the value objects.
 */
public abstract class AbstractXmlCollectionByAttributeConverter<V> implements XmlCollectionConverter<V> {
    /**
     * The default attribute name for the value.
     */
    private static final String VALUE_DEFAULT_ATTRIBUTE_NAME = "value";
    /**
     * The attribute name for the value.
     */
    private String valueAttributeName;

    /**
     * A constructor using the default value attribute name.
     */
    protected AbstractXmlCollectionByAttributeConverter() {
        this(VALUE_DEFAULT_ATTRIBUTE_NAME);
    }

    /**
     * A constructor using the specified value attribute name.
     *
     * @param valueAttributeName The attribute name for the value.
     */
    protected AbstractXmlCollectionByAttributeConverter(String valueAttributeName) {
        this.valueAttributeName = valueAttributeName;
    }

    @Override
    public void writeValue(Element itemNode, V value) {
        itemNode.setAttribute(valueAttributeName, valueToString(value));
    }

    @Override
    public V readValue(Node itemNode) {
        return stringToValue(HGBaseXMLTools.getAttributeValue(itemNode, valueAttributeName));
    }

    /**
     * Converts the value object into a string.
     *
     * @param value The value object, can be {@code null}.
     * @return The value as string, is empty if {@code null} is given.
     */
    protected String valueToString(V value) {
        return (value == null) ? "" : value.toString();
    }

    /**
     * Converts the value string into the correct type.
     *
     * @param valueString The value as string, can be empty.
     * @return The converted value.
     */
    abstract protected V stringToValue(String valueString);
}
