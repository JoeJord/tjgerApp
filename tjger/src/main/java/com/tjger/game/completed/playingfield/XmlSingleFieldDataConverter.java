package com.tjger.game.completed.playingfield;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Converts data (of type {@link Object}) that are stored to single fields from and to xml nodes.
 * 
 * @author hagru
 */
public interface XmlSingleFieldDataConverter {
    
    /**
     * Stores the data object into the given xml node.
     * 
     * @param doc the xml document
     * @param node the xml node to store the data object into
     * @param data the data object to store, must not be null
     */
    public void dataToXml(Document doc, Element node, Object data);
    
    /**
     * Reads the data object from the given node.
     * 
     * @param node the xml node that holds the information about the data object
     * @return the created object or null in case of error
     */
    public Object dataFromXml(Node node);

}
