package com.tjger.lib;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * An XML map converter that reads the elements from attribute values and therefore needs
 * only methods to convert types {@code <K>} and {@code <V>} to String and back.
 *
 * @param <K> the type of the key objects
 * @param <V> the type of the value objects
 * @author hagru
 */
public abstract class AbstractXmlMapByAttributeConverter<K,V> implements XmlMapConverter<K,V> {

    private static final String XML_KEY = "key";
    private static final String XML_VALUE = "value";

    private String keyAttributeName;
    private String valueAttributeName;

    /**
     * A converter using the default key and value attributes.
     */
    protected AbstractXmlMapByAttributeConverter() {
        this(XML_KEY, XML_VALUE);
    }

    /**
     * A converter using the the given key and value attributes.
     *
     * @param keyAttributeName the name of the attribute that stores the key
     * @param valueAttributeName the name of the attribute that stores the value
     */
    protected AbstractXmlMapByAttributeConverter(String keyAttributeName, String valueAttributeName) {
        this.keyAttributeName = keyAttributeName;
        this.valueAttributeName = valueAttributeName;
    }

    @Override
    public void writeNode(Element itemNode, K key, V value) {
        itemNode.setAttribute(keyAttributeName, keyToString(key));
        itemNode.setAttribute(valueAttributeName, valueToString(value));
    }

    @Override
    public K readKey(Node itemNode) {
        return stringToKey(HGBaseXMLTools.getAttributeValue(itemNode, keyAttributeName));
    }

    @Override
    public V readValue(Node itemNode) {
        return stringToValue(HGBaseXMLTools.getAttributeValue(itemNode, valueAttributeName));
    }

    /**
     * Converts the key object into a string.
     *
     * @param key the key object, can be null
     * @return the key as string, is empty if null is given
     */
    protected String keyToString(K key) {
        return (key == null)? "" : key.toString();
    }

    /**
     * Converts the value object into a string.
     *
     * @param value the value object, can be null
     * @return the value as string, is empty if null is given
     */
    protected String valueToString(V value) {
        return (value == null)? "" : value.toString();
    }

    /**
     * Converts the key string into the correct type.
     *
     * @param keyString the key as string, can be empty
     * @return the converted key
     */
    abstract protected K stringToKey(String keyString);

    /**
     * Converts the value string into the correct type.
     *
     * @param valueString the value as string, can be empty
     * @return the converted value
     */
    abstract protected V stringToValue(String valueString);

}
