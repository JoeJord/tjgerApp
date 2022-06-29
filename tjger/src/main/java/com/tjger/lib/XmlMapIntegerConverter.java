package com.tjger.lib;


/**
 * An XML converter where key and value are of type {@link Integer}.
 */
public class XmlMapIntegerConverter extends AbstractXmlMapByAttributeConverter<Integer, Integer> {

    /* (non-Javadoc)
     * @see tjger.lib.AbstractXmlMapByAttributeConverter#stringToKey(java.lang.String)
     */
    @Override
    protected Integer stringToKey(String keyString) {
        return Integer.valueOf(keyString);
    }

    /* (non-Javadoc)
     * @see tjger.lib.AbstractXmlMapByAttributeConverter#stringToValue(java.lang.String)
     */
    @Override
    protected Integer stringToValue(String valueString) {
        return Integer.valueOf(valueString);
    }
}