package com.tjger.lib;


/**
 * An XML converter where key and value are of type {@link String}.
 */
public class XmlMapStringConverter extends AbstractXmlMapByAttributeConverter<String, String> {

    /* (non-Javadoc)
     * @see tjger.lib.AbstractXmlMapByAttributeConverter#stringToKey(java.lang.String)
     */
    @Override
    protected String stringToKey(String keyString) {
        return keyString;
    }

    /* (non-Javadoc)
     * @see tjger.lib.AbstractXmlMapByAttributeConverter#stringToValue(java.lang.String)
     */
    @Override
    protected String stringToValue(String valueString) {
        return valueString;
    }
}