package com.tjger.game.completed;

import com.tjger.gui.completed.GameElement;
import com.tjger.gui.completed.GameElementSet;

import org.w3c.dom.Node;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Helper class to create sets of game elements from the XML document.
 *
 * @param <S> The type of the set.
 * @param <E> The type of the elements in the set.
 */
abstract class GameElementSetConstructor<S extends GameElementSet, E extends GameElement> {
    /**
     * The configuration key for the type of the game element.
     */
    protected final String typeConfigKey;
    /**
     * The configuration key for the set.
     */
    protected final String setConfigKey;
    /**
     * The configuration key for the set elements.
     */
    protected final String elementConfigKey;
    /**
     * The configuration key for the filename from an element.
     */
    protected final String fileConfigKey;

    /**
     * Constructs a new instance.
     *
     * @param typeConfigKey    The configuration key for the type of the game element.
     * @param setConfigKey     The configuration key for the set.
     * @param elementConfigKey The configuration key for the set elements.
     * @param fileConfigKey    The configuration key for the filename from an element.
     */
    protected GameElementSetConstructor(String typeConfigKey, String setConfigKey, String elementConfigKey,
                                        String fileConfigKey) {
        this.typeConfigKey = typeConfigKey;
        this.setConfigKey = setConfigKey;
        this.elementConfigKey = elementConfigKey;
        this.fileConfigKey = fileConfigKey;
    }

    /**
     * Returns the alternative set type if the type is not specified in the XML document.
     *
     * @param node The set node.
     * @return The alternative set type if the type is not specified in the XML document.
     */
    protected abstract String getAlternativeSetType(Node node);

    /**
     * Logs the situation, that the type of of set is missing.
     *
     * @param setName The name of the set.
     */
    protected abstract void logTypeMissing(String setName);

    /**
     * Creates a set with the specified values.
     *
     * @param type      The type of the set.
     * @param name      The name of the set.
     * @param hidden    The flag if the set is hidden.
     * @param proTeaser The flag if the element set is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param productId The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     * @param node      The set node to read additional information from the XML document.
     * @param config    The game configuration object.
     * @return The created set.
     */
    protected abstract S createSet(String type, String name, boolean hidden, boolean proTeaser, String productId, Node node, GameConfig config);

    /**
     * Creates one element.
     *
     * @param set       The set to which the element belongs.
     * @param node      The element node to read additional information from the XML document.
     * @param sequence  The sequence of the element in the set.
     * @param proTeaser The flag if the element is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param productId The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     * @param config    The game configuration object.
     * @return The created element.
     */
    protected abstract E createElement(S set, Node node, int sequence, boolean proTeaser, String productId, GameConfig config);

    /**
     * Method to be called for reading the XML structure.
     *
     * @param root   The root of the XML structure.
     * @param config The game configuration object.
     */
    public void run(Node root, GameConfig config) {
        readSets(root, config);
    }

    /**
     * Reads all the sets of the set type.
     *
     * @param root   The root of the XML structure.
     * @param config The game configuration object.
     */
    private void readSets(Node root, GameConfig config) {
        ChildNodeIterator.run(new ChildNodeIterator(root, typeConfigKey, config) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                final GameConfig config = (GameConfig) obj;
                if (setConfigKey.equals(node.getNodeName()) && GameConfigFileReader.isAvailable(node, config)) {
                    readSet(node, config);
                }
            }
        });
    }

    /**
     * Reads one set.
     *
     * @param node   The set node.
     * @param config The game configuration object.
     */
    private void readSet(Node node, GameConfig config) {
        String type = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_TYPE);
        if (!HGBaseTools.hasContent(type)) {
            type = getAlternativeSetType(node);
        }
        String name = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_NAME);
        if ((isTypeNeeded()) && (!HGBaseTools.hasContent(type))) {
            logTypeMissing(name);
            return;
        }
        boolean hidden = GameConfigFileReader.isHiddenPart(node, config);
        boolean proTeaser = GameConfigFileReader.isProTeaser(node);
        String productId = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_PRODUCTID);
        S newSet = createSet(type, name, hidden, proTeaser, productId, node, config);
        readSetElements(node, config, newSet);

        if (!newSet.isEmpty()) {
            addSetToCollection(newSet, type);
        }
    }

    /**
     * Returns {@code true} if the set needs a type.
     *
     * @return {@code true} if the set needs a type.
     */
    protected abstract boolean isTypeNeeded();

    /**
     * Adds the specified set to the collection of all sets.
     *
     * @param set  The set to add
     * @param type The type of the set.
     */
    protected abstract void addSetToCollection(S set, String type);

    /**
     * Reads all elements of the set.
     *
     * @param node   The set node.
     * @param config The game configuration object.
     * @param set    The set.
     */
    private void readSetElements(Node node, GameConfig config, S set) {
        ChildNodeIterator.run(new ChildNodeIterator(node, setConfigKey, set) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                if (elementConfigKey.equals(node.getNodeName())) {
                    readElement(node, config, set);
                }
            }
        });
    }

    /**
     * Reads one element of the set.
     *
     * @param node   The element node.
     * @param config The game configuration object.
     * @param set    The set.
     */
    private void readElement(Node node, GameConfig config, S set) {
        int sequence = HGBaseXMLTools.getAttributeIntValue(node, GameConfigFileReader.CONFIG_SEQUENCE);
        if (sequence != HGBaseTools.INVALID_INT) {
            readElementSingleSequence(node, sequence, set, config);
        } else {
            readElementSequence(node, set, config);
        }
    }

    /**
     * Reads a sequence of game elements.
     *
     * @param node   The element node.
     * @param set    The set.
     * @param config The game configuration object.
     */
    private void readElementSequence(Node node, S set, GameConfig config) {
        int seqStart = HGBaseXMLTools.getAttributeIntValue(node, GameConfigFileReader.CONFIG_SEQUENCE_START);
        int seqEnd = HGBaseXMLTools.getAttributeIntValue(node, GameConfigFileReader.CONFIG_SEQUENCE_END);
        if (seqStart != HGBaseTools.INVALID_INT && seqEnd != HGBaseTools.INVALID_INT && seqStart <= seqEnd) {
            for (int sequence = seqStart; sequence <= seqEnd; sequence++) {
                readElementSingleSequence(node, sequence, set, config);
            }
        }
    }

    /**
     * Reads one single sequence of a game element.
     *
     * @param node     The element node.
     * @param sequence The sequence of the game element.
     * @param set      The set.
     * @param config   The game configuration object.
     */
    private void readElementSingleSequence(Node node, int sequence, S set, GameConfig config) {
        E element = createElement(set, node, sequence, GameConfigFileReader.isProTeaser(node), GameConfigFileReader.getProductId(node), config);
        if (element != null) {
            set.addElement(element);
        }
    }

    /**
     * Returns the path for the file of the game element.
     *
     * @param node     The element node.
     * @param sequence The sequence of the element in the set.
     * @param set      The set.
     * @param config   The configuration object.
     * @return The path for the file of the game element.
     */
    protected String getElementFilePath(Node node, int sequence, S set, GameConfig config) {
        String filePath = HGBaseXMLTools.getAttributeValue(node, fileConfigKey);
        return HGBaseTools.hasContent(filePath) ? filePath
                : calculateElementFilePath(node, sequence, set, config);
    }

    /**
     * Calculates the path for the file of the game element.
     *
     * @param node     The element node.
     * @param sequence The sequence of the element in the set.
     * @param set      The set.
     * @param config   The configuration object.
     * @return The path for the file of the game element.
     */
    protected String calculateElementFilePath(Node node, int sequence, S set, GameConfig config) {
        return config.getElementPath(elementConfigKey) + "/" + set.getName() + "/"
                + getElementFileName(node, set) + "-" + sequence + "."
                + config.getElementExtension(elementConfigKey);
    }

    /**
     * Returns the filename of the game element.
     *
     * @param node The element node.
     * @param set  The set.
     * @return The filename of the game element.
     */
    protected abstract String getElementFileName(Node node, S set);
}
