package com.tjger.lib;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.game.internal.PlayerFactory;
import com.tjger.gui.completed.Card;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.IntCollection;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Help methods for saving and loading elements into nodes.
 *
 * @author hagru
 */
public class XmlUtil {

    private static final String XML_NUMBER = "number";
    private static final String XML_TRUE = "true";
    private static final String XML_SEQUENCE = "sequence";
    private static final String XML_COLOR = "color";
    private static final String XML_NAME = "name";
    private static final String XML_TYPE = "type";
    private static final String XML_PIECE = "piece";

    private XmlUtil() {
        super();
    }

    /**
     * Saves an int array.
     *
     * @param doc    Document object.
     * @param name   The node's name.
     * @param values Int array.
     * @return The element with the array.
     */
    public static Element saveIntArray(Document doc, String name, int[] values) {
        Element nE = doc.createElement(name);
        nE.setAttribute(XML_NUMBER, String.valueOf(values.length));
        for (int i = 0; i < values.length; i++) {
            nE.setAttribute(name + i, String.valueOf(values[i]));
        }
        return nE;
    }

    /**
     * @param doc    Document object.
     * @param root   The root to store the new element.
     * @param name   The node's name.
     * @param values Int array.
     * @return The element with the array.
     */
    public static Element saveIntArray(Document doc, Element root, String name, int[] values) {
        Element node = saveIntArray(doc, name, values);
        root.appendChild(node);
        return node;
    }

    /**
     * Loads an int array.
     *
     * @param node The node containing the array.
     * @return The int array.
     */
    public static int[] loadIntArray(Node node) {
        String nodeName = node.getNodeName();
        int number = Math.max(0, HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, XML_NUMBER)));
        int[] values = new int[number];
        for (int i = 0; i < values.length; i++) {
            int value = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, nodeName + i));
            if (value != HGBaseTools.INVALID_INT) {
                values[i] = value;
            }
        }
        return values;
    }

    /**
     * Saves an IntCollection.
     *
     * @param doc   Document object.
     * @param name  The node's name.
     * @param value The IntCollection object.
     * @return The element with the collection.
     */
    public static Element saveIntCollection(Document doc, String name, IntCollection value) {
        return saveIntArray(doc, name, value.values());
    }

    /**
     * @param doc   Document object.
     * @param root  The root to store the new element.
     * @param name  The node's name.
     * @param value The IntCollection object.
     * @return The element with the collection.
     */
    public static Element saveIntCollection(Document doc, Element root, String name, IntCollection value) {
        return saveIntArray(doc, root, name, value.values());
    }


    /**
     * Loads an IntCollection.
     *
     * @param node The node containing the collection.
     * @return The IntCollection object.
     */
    public static IntCollection loadIntCollection(Node node) {
        return new IntCollection(loadIntArray(node));
    }

    /**
     * Saves an IntCollection array.
     */
    public static Element saveIntCollectionArray(Document doc, String name, IntCollection[] values) {
        Element nE = doc.createElement(name);
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                Element eNode = saveIntCollection(doc, name + i, values[i]);
                nE.appendChild(eNode);
            }
        }
        return nE;
    }

    /**
     * @see #saveIntCollectionArray(Document, String, IntCollection[])
     */
    public static Element saveIntCollectionArray(Document doc, Element root, String name, IntCollection[] values) {
        Element node = saveIntCollectionArray(doc, name, values);
        root.appendChild(node);
        return node;
    }

    /**
     * Loads an IntCollection array.
     */
    public static IntCollection[] loadIntCollectionArray(Node node) {
        final String nodeName = node.getNodeName();
        final List<IntCollection> list = new ArrayList<>();
        ChildNodeIterator.run(new ChildNodeIterator(node, null) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                if (node.getNodeName().startsWith(nodeName)) {
                    IntCollection collection = loadIntCollection(node);
                    list.add(collection);
                }
            }
        });
        return list.toArray(new IntCollection[0]);
    }

    /**
     * Saves a boolean array.
     *
     * @param doc    Document object.
     * @param name   The node's name.
     * @param values Boolean array.
     * @return The element with the array.
     */
    public static Element saveBooleanArray(Document doc, String name, boolean[] values) {
        Element nE = doc.createElement(name);
        nE.setAttribute(XML_NUMBER, String.valueOf(values.length));
        for (int i = 0; i < values.length; i++) {
            nE.setAttribute(name + i, String.valueOf(values[i]));
        }
        return nE;
    }

    /**
     * @param doc    Document object.
     * @param root   The root to store the new element.
     * @param name   The node's name.
     * @param values Boolean array.
     * @return The element with the array.
     */
    public static Element saveBooleanArray(Document doc, Element root, String name, boolean[] values) {
        Element node = saveBooleanArray(doc, name, values);
        root.appendChild(node);
        return node;
    }

    /**
     * Loads a boolean array.
     *
     * @param node The node containing the array.
     * @return The boolean array.
     */
    public static boolean[] loadBooleanArray(Node node) {
        String nodeName = node.getNodeName();
        int number = Math.max(0, HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, XML_NUMBER)));
        boolean[] values = new boolean[number];
        for (int i = 0; i < values.length; i++) {
            values[i] = HGBaseXMLTools.getAttributeValue(node, nodeName + i).equals(XML_TRUE);
        }
        return values;
    }

    /**
     * Saves a String array.
     *
     * @param doc    Document object.
     * @param name   The node's name.
     * @param values String array.
     * @return The element with the array.
     */
    public static Element saveStringArray(Document doc, String name, String[] values) {
        Element nE = doc.createElement(name);
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                Element eNode = doc.createElement(name + i);
                HGBaseXMLTools.setNodeValue(eNode, values[i]);
                nE.appendChild(eNode);
            }
        }
        return nE;
    }

    /**
     * @param doc    Document object.
     * @param root   The root to store the new element.
     * @param name   The node's name.
     * @param values String array.
     * @return The element with the array.
     */
    public static Element saveStringArray(Document doc, Element root, String name, String[] values) {
        Element node = saveStringArray(doc, name, values);
        root.appendChild(node);
        return node;
    }

    /**
     * Loads a String array.
     *
     * @param node The node containing the array.
     * @return The String array.
     */
    public static String[] loadStringArray(Node node) {
        final String nodeName = node.getNodeName();
        final List<String> list = new ArrayList<>();
        ChildNodeIterator.run(new ChildNodeIterator(node, null) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                if (node.getNodeName().startsWith(nodeName)) {
                    list.add(HGBaseXMLTools.getNodeValue(node));
                }
            }
        });
        return HGBaseTools.toStringArray(list);
    }

    /**
     * Saves a Card array.
     *
     * @param doc   Document object.
     * @param name  The node's name.
     * @param cards The Card array.
     * @return The element with the array.
     */
    public static Element saveCardArray(Document doc, String name, Card[] cards) {
        Element nE = doc.createElement(name);
        for (int i = 0; i < cards.length; i++) {
            Element eNode = saveCard(doc, name + i, cards[i]);
            nE.appendChild(eNode);
        }
        return nE;
    }

    /**
     * @param doc   Document object.
     * @param root  The root to store the new element.
     * @param name  The node's name.
     * @param cards The Card array.
     * @return The element with the array.
     */
    public static Element saveCardArray(Document doc, Element root, String name, Card[] cards) {
        Element node = saveCardArray(doc, name, cards);
        root.appendChild(node);
        return node;
    }

    /**
     * @see #saveCardArray(Document, String, Card[])
     */
    public static Element saveCardList(Document doc, String name, List<Card> cardList) {
        return saveCardArray(doc, name, ArrayUtil.toCard(cardList));
    }

    public static Element saveCardList(Document doc, Element root, String name, List<Card> cardList) {
        Element node = saveCardList(doc, name, cardList);
        root.appendChild(node);
        return node;
    }

    /**
     * Loads a Card array.
     *
     * @param node The node containing the array.
     * @return The Card array.
     */
    public static Card[] loadCardArray(Node node) {
        final String nodeName = node.getNodeName();
        final List<Card> list = new ArrayList<>();
        ChildNodeIterator.run(new ChildNodeIterator(node, null) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                if (node.getNodeName().startsWith(nodeName)) {
                    Card card = loadCard(node);
                    list.add(card);
                }
            }
        });
        return ArrayUtil.toCard(list);
    }

    /**
     * @see #loadCardArray(Node)
     */
    public static List<Card> loadCardList(Node node) {
        Card[] cards = loadCardArray(node);
        return ArrayUtil.toList(cards);
    }

    /**
     * Saves a single Card.
     *
     * @param doc  Document object.
     * @param name The node's name.
     * @param card The Card to save.
     * @return The Element containing the card.
     */
    public static Element saveCard(Document doc, String name, Card card) {
        Element eCard = doc.createElement(name);
        if (card != null) {
            eCard.setAttribute(XML_COLOR, card.getColor());
            eCard.setAttribute(XML_SEQUENCE, String.valueOf(card.getSequence()));
            String type = card.getCardSet().getType();
            if (!ConstantValue.CONFIG_CARDSET.equals(type)) {
                eCard.setAttribute(XML_TYPE, type);
            }
        }
        return eCard;
    }

    /**
     * @param doc  Document object.
     * @param root The root to store the new element.
     * @param name The node's name.
     * @param card The Card to save.
     * @return The Element containing the card.
     */
    public static Element saveCard(Document doc, Element root, String name, Card card) {
        Element node = saveCard(doc, name, card);
        root.appendChild(node);
        return node;
    }

    /**
     * Load a single Card.
     *
     * @param node The node containing the card.
     * @return The Card or null.
     */
    public static Card loadCard(Node node) {
        GameConfig config = GameManager.getInstance().getGameConfig();
        String color = HGBaseXMLTools.getAttributeValue(node, XML_COLOR);
        int sequence = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, XML_SEQUENCE));
        if (HGBaseTools.hasContent(color) && sequence != HGBaseTools.INVALID_INT) {
            String type = HGBaseXMLTools.getAttributeValue(node, XML_TYPE);
            if (HGBaseTools.hasContent(type)) {
                return config.getActiveCardSet(type).getCard(color, sequence);
            } else {
                return config.getActiveCardSet().getCard(color, sequence);
            }
        } else {
            return null;
        }
    }

    /**
     * Saves a single player.
     *
     * @param doc    Document object.
     * @param name   The node's name.
     * @param player The player to save.
     * @return The Element containing the card.
     */
    public static Element savePlayer(Document doc, String name, GamePlayer player) {
        Element eP = doc.createElement(name);
        if (player != null) {
            eP.setAttribute(XML_NAME, player.getName());
            eP.setAttribute(XML_TYPE, player.getType().getId());
            eP.setAttribute(XML_PIECE, player.getPieceColor());
        }
        return eP;
    }

    /**
     * @param doc    Document object.
     * @param root   The root to store the new element.
     * @param name   The node's name.
     * @param player The player to save.
     * @return The Element containing the card.
     */
    public static Element savePlayer(Document doc, Element root, String name, GamePlayer player) {
        Element node = savePlayer(doc, name, player);
        root.appendChild(node);
        return node;
    }

    /**
     * Load a single player.
     *
     * @param node The node containing the player.
     * @return The GamePlayer or null.
     */
    public static GamePlayer loadPlayer(Node node) {
        String pName = HGBaseXMLTools.getAttributeValue(node, XML_NAME);
        String pType = HGBaseXMLTools.getAttributeValue(node, XML_TYPE);
        String pPiece = HGBaseXMLTools.getAttributeValue(node, XML_PIECE);
        // always create the player to get correct piece color
        return PlayerFactory.getInstance().createPlayer(pType, pName, pPiece);
    }

    /**
     * Saves a map into an XML element and returns this element.
     *
     * @param doc          The xml document.
     * @param nodeName     The name of the new xml element.
     * @param itemName     The name of the xml element for the entries of the map, must not be null.
     * @param map          The map to save.
     * @param xmlConverter The converter to write the map items into xml elements.
     * @return The new created element.
     */
    public static <K, V> Element saveMap(Document doc, String nodeName, String itemName, Map<K, V> map, XmlMapConverter<K, V> xmlConverter) {
        Element mapNode = doc.createElement(nodeName);
        for (Entry<K, V> entry : map.entrySet()) {
            Element itemNode = doc.createElement(itemName);
            mapNode.appendChild(itemNode);
            xmlConverter.writeNode(itemNode, entry.getKey(), entry.getValue());
        }
        return mapNode;
    }

    /**
     * Saves a map into an XML element and appends it to a given XML root element.
     *
     * @param doc          The xml document.
     * @param root         The root element to append the new created element.
     * @param nodeName     The name of the new xml element.
     * @param itemName     The name of the xml element for the entries of the map, must not be null (if not given it is &quot;item&quot;).
     * @param map          The map to save.
     * @param xmlConverter The converter to write the map items into xml elements.
     * @return The new created element.
     */
    public static <K, V> Element saveMap(Document doc, Element root, String nodeName, String itemName,
                                         Map<K, V> map, XmlMapConverter<K, V> xmlConverter) {
        Element node = saveMap(doc, nodeName, itemName, map, xmlConverter);
        root.appendChild(node);
        return node;
    }

    /**
     * Saves the specified collection into an XML element and returns this element.
     *
     * @param doc          The XML document.
     * @param nodeName     The name of the new XML element.
     * @param itemName     The name of the XML element for the entries of the collection, must not be {@code null} (if not given it is &quot;item&quot;).
     * @param collection   The collection to save.
     * @param xmlConverter The converter to write the collection items into XML elements.
     * @return The new created element.
     */
    public static <V> Element saveCollection(Document doc, String nodeName, String itemName,
                                             Collection<V> collection, XmlCollectionConverter<V> xmlConverter) {
        Element collectionNode = doc.createElement(nodeName);
        for (V value : collection) {
            Element itemNode = doc.createElement(itemName);
            collectionNode.appendChild(itemNode);
            xmlConverter.writeValue(itemNode, value);
        }
        return collectionNode;
    }

    /**
     * Saves the specified collection into an XML element and appends it to the specified XML root element.
     *
     * @param doc          The XML document.
     * @param root         The root element to append the new created element.
     * @param nodeName     The name of the new XML element.
     * @param itemName     The name of the XML element for the entries of the collection, must not be {@code null} (if not given it is &quot;item&quot;).
     * @param collection   The collection to save.
     * @param xmlConverter The converter to write the collection items into XML elements.
     * @return The new created element.
     */
    public static <V> Element saveCollection(Document doc, Element root, String nodeName, String itemName,
                                             Collection<V> collection, XmlCollectionConverter<V> xmlConverter) {
        Element node = saveCollection(doc, nodeName, itemName, collection, xmlConverter);
        root.appendChild(node);
        return node;
    }

    /**
     * Reads the map from an xml element and returns it.
     *
     * @param node         The xml element that holds the map (is returned by saveMap and has the name given by nodeName there).
     * @param itemName     The name of the xml element for the entries, must not be null.
     * @param xmlConverter The converter to read the map items from xml elements.
     * @return A new map containing the values stored in the xml element.
     */
    public static <K, V> Map<K, V> loadMap(Node node, final String itemName, final XmlMapConverter<K, V> xmlConverter) {
        final Map<K, V> map = new HashMap<>();
        ChildNodeIterator.run(new ChildNodeIterator(node, map) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                if (itemName.equals(node.getNodeName())) {
                    K key = xmlConverter.readKey(node);
                    if (key != null) {
                        V value = xmlConverter.readValue(node);
                        map.put(key, value);
                    }
                }
            }
        });
        return map;
    }

    /**
     * Reads the collection from the specified XML element and returns it.
     *
     * @param node         The XML element that holds the collection (is returned by {@link XmlUtil#saveCollection(Document, String, String, Collection, XmlCollectionConverter)} and has the name given by {@code nodeName} there).
     * @param itemName     The name of the XML element for the entries, must not be {@code null}.
     * @param xmlConverter The converter to read the collection item from XML elements.
     * @return A new collection containing the values stored in the XML element.
     */
    public static <V> Collection<V> loadCollection(Node node, final String itemName,
                                                   final XmlCollectionConverter<V> xmlConverter) {
        final Collection<V> collection = new ArrayList<>();
        ChildNodeIterator.run(new ChildNodeIterator(node, collection) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                if (itemName.equals(node.getNodeName())) {
                    collection.add(xmlConverter.readValue(node));
                }
            }
        });
        return collection;
    }
}
