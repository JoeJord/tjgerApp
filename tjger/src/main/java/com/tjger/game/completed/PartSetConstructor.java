package com.tjger.game.completed;

import android.graphics.Bitmap;

import com.tjger.gui.completed.ColorValuePart;
import com.tjger.gui.completed.PartSet;
import com.tjger.lib.ConstantValue;

import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;

import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Helper class for create sets (PieceSet, CardSet, PartSet) from the xml document.
 *
 * @author hagru
 */
abstract class PartSetConstructor<T extends PartSet> {

    protected String key1;
    protected String key2;
    protected String key3;
    protected List<T> listSet;
    protected Map<String, List<T>> mapSet;

    private PartSetConstructor(String key1, String key2, String key3, List<T> listSet, Map<String, List<T>> mapSet) {
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
        this.listSet = listSet;
        this.mapSet = mapSet;
    }

    PartSetConstructor(String key1, String key2, String key3, List<T> listSet) {
        this(key1, key2, key3, listSet, null);
    }

    PartSetConstructor(String key1, String key2, String key3, Map<String, List<T>> mapSet) {
        this(key1, key2, key3, null, mapSet);
    }

    /**
     * Method to be called for reading the xml structure.
     *
     * @param root   The root of the xml structure.
     * @param config The GameConfig object.
     */
    public void run(Node root, GameConfig config) {
        readSets(root, config);
    }

    /**
     * Reads all sets of the set type.
     *
     * @param root   The root of the XML structure.
     * @param config The GameConfig object.
     */
    private void readSets(Node root, GameConfig config) {
        ChildNodeIterator.run(new ChildNodeIterator(root, key1, config) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                final GameConfig config = (GameConfig) obj;
                if (node.getNodeName().equals(key2) && GameConfigFileReader.isAvailable(node, config)) {
                    readSet(node, config);
                }
            }
        });
    }

    /**
     * Reads one set.
     *
     * @param node   The set node.
     * @param config The GameConfig object.
     */
    private void readSet(Node node, final GameConfig config) {
        String type = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_TYPE);
        if (GameConfigFileReader.CONFIG_CARDSET.equals(node.getNodeName()) && !HGBaseTools.hasContent(type)) {
            type = ConstantValue.CONFIG_CARDSET;
        }
        String name = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_NAME);
        if (mapSet != null && !HGBaseTools.hasContent(type)) {
            HGBaseLog.logWarn("No type defined for part set '" + name + "'!");
        } else {
            // test for value order for user defined part sets
            if (mapSet != null) {
                GameConfigFileReader.setOrderBy(config, type, node);
            }
            boolean hidden = GameConfigFileReader.isHiddenPart(node, config);
            // create the new set
            T setNew = createPartSet(type, name, node, hidden);
            readSetElements(node, config, setNew);

            if (!setNew.getParts().isEmpty()) {
                if (listSet != null) {
                    listSet.add(setNew);
                }
                if (mapSet != null) {
                    GameConfigFileReader.getListFromMap(mapSet, type).add(setNew);
                }
            }
        }
    }

    /**
     * Reads all elements of the set.
     *
     * @param node   The set node.
     * @param config The GameConfig object.
     * @param set    The set.
     */
    private void readSetElements(Node node, final GameConfig config, T set) {
        ChildNodeIterator.run(new ChildNodeIterator(node, key2, set) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                PartSet set = (PartSet) obj;
                if (node.getNodeName().equals(key3)) {
                    readSetElement(node, config, set);
                }
            }

        });
    }

    /**
     * Reads one element of the set.
     *
     * @param node   The element node.
     * @param config The GameConfig object.
     * @param set    The set.
     */
    private void readSetElement(Node node, final GameConfig config, PartSet set) {
        String color = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_COLOR);
        int sequence = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "sequence"));
        int value = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_VALUE));
        if (sequence != HGBaseTools.INVALID_INT) {
            String image = getPartImage(config, node, set, color, sequence);
            loadSinglePart(set, color, sequence, getPartValue(value, sequence), image, node);
        } else {
            int seqS = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "sequencestart"));
            int seqE = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "sequenceend"));
            if (seqS != HGBaseTools.INVALID_INT && seqE != HGBaseTools.INVALID_INT && seqS <= seqE) {
                for (int i = seqS; i <= seqE; i++) {
                    String image = getPartImage(config, node, set, color, i);
                    loadSinglePart(set, color, i, getPartValue(value, i), image, node);
                }
            }
        }
    }

    /**
     * @param value    The value, can be INVALID.
     * @param sequence The sequence.
     * @return The value of this part.
     */
    private int getPartValue(int value, int sequence) {
        if (value == HGBaseTools.INVALID_INT) {
            return sequence;
        } else {
            return value;
        }
    }

    /**
     * @param type The part set's type (only necessary for PartSet).
     * @param name Name of the new part set.
     * @param node The xml node for additional information.
     * @return A new part set (PieceSet/CardSet).
     */
    protected abstract T createPartSet(String type, String name, Node node, boolean hidden);

    /**
     * @param partSet  Name of the part set.
     * @param color    Name of the part's color
     * @param sequence Sequence of the part.
     * @param config   The GameConfig object.
     * @return The standard file name.
     */
    protected String calculateImage(String partSet, String color, int sequence, GameConfig config) {
        return config.getImagePath(key3) + "/" + partSet + "/" + color + "-" + sequence + "." + config.getImageExtension(key3);
    }

    /**
     * Loads a single part into the part set.
     *
     * @param set       The part set to load the part into.
     * @param color     The color of the new part.
     * @param sequence  Sequence value of the new part.
     * @param value     Value of the new part.
     * @param imagePath The path to the image of the new part.
     * @param node      the original XML node the data were read from.
     */
    protected void loadSinglePart(PartSet set, String color, int sequence, int value, String imagePath, Node node) {
        Bitmap image = HGBaseGuiTools.loadImage(imagePath);
        if (image != null) {
            set.addPart(createColorValuePart(set, color, sequence, value, image, node));
        } else {
            HGBaseLog.logWarn("The " + key3 + "'s image file " + imagePath + " was not found!");
        }
    }

    /**
     * @param set      The part set.
     * @param color    The color of the new part.
     * @param sequence The unique sequence of the new part.
     * @param value    The value of the new part.
     * @param image    The image for the new part.
     * @param node     the original XML node the part data were read from.
     * @return A new part.
     */
    protected abstract ColorValuePart createColorValuePart(PartSet set, String color, int sequence, int value, Bitmap image, Node node);

    /**
     * @param config   the game configuration.
     * @param node     the node to read the data from.
     * @param set      The part set.
     * @param color    The color of the new part.
     * @param sequence The unique sequence of the new part.
     * @return the name of the image for the part.
     */
    private String getPartImage(final GameConfig config, Node node, PartSet set, String color, int sequence) {
        String image = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_IMAGE);
        if (!HGBaseTools.hasContent(image)) {
            image = calculateImage(set.getName(), color, sequence, config);
        }
        return image;
    }
}