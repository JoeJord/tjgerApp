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
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Helper class to create sets (PieceSet, CardSet, PartSet) from the XML document.
 *
 * @param <T> The type of the set.
 * @author hagru
 */
abstract class PartSetConstructor<T extends PartSet> extends GameElementSetConstructor<PartSetWrapper<T>, ColorValuePart> {
    /**
     * The list of all available sets.
     */
    protected final List<T> setList;
    /**
     * The map of all available sets per type.
     */
    protected final Map<String, List<T>> setMap;

    /**
     * Constructs a new instance with the usage of a list of sets.<br>
     * Should be used if there are no different types of the set.<br>
     * If different types are needed, then use {@link #PartSetConstructor(String, String, String, Map)}.
     *
     * @param typeConfigKey    The configuration key for the type of the game element.
     * @param setConfigKey     The configuration key for the set.
     * @param elementConfigKey The configuration key for the set elements.
     * @param setList          The list of all available sets.
     */
    protected PartSetConstructor(String typeConfigKey, String setConfigKey, String elementConfigKey, List<T> setList) {
        super(typeConfigKey, setConfigKey, elementConfigKey, GameConfigFileReader.CONFIG_IMAGE);
        this.setList = setList;
        this.setMap = null;
    }

    /**
     * Constructs a new instance with the usage of a map of sets.<br>
     * Should be used if different types of the set are needed.<br>
     * If different types are not needed, then use {@link #PartSetConstructor(String, String, String, List)}.
     *
     * @param typeConfigKey    The configuration key for the type of the game element.
     * @param setConfigKey     The configuration key for the set.
     * @param elementConfigKey The configuration key for the set elements.
     * @param setMap           The map of all available sets per type.
     */
    protected PartSetConstructor(String typeConfigKey, String setConfigKey, String elementConfigKey, Map<String, List<T>> setMap) {
        super(typeConfigKey, setConfigKey, elementConfigKey, GameConfigFileReader.CONFIG_IMAGE);
        this.setList = null;
        this.setMap = setMap;
    }

    @Override
    protected String getAlternativeSetType(Node node) {
        if (GameConfigFileReader.CONFIG_CARDSET.equals(node.getNodeName())) {
            return ConstantValue.CONFIG_CARDSET;
        }
        return "";
    }

    @Override
    protected void logTypeMissing(String setName) {
        HGBaseLog.logWarn("No type defined for part set '" + setName + "'!");
    }

    @Override
    protected final PartSetWrapper<T> createSet(String type, String name, boolean hidden, boolean proTeaser, Node node, GameConfig config) {
        return new PartSetWrapper<>(createPartSet(type, name, hidden, proTeaser, node));
    }

    @Override
    protected boolean isTypeNeeded() {
        // When the set map is used, then the type is necessary.
        return (setMap != null);
    }

    @Override
    protected void addSetToCollection(PartSetWrapper<T> set, String type) {
        if (setList != null) {
            setList.add(set.getPartSet());
        }
        if (setMap != null) {
            GameConfigFileReader.getListFromMap(setMap, type).add(set.getPartSet());
        }
    }

    /**
     * Creates a part set with the specified values.
     *
     * @param type      The part set's type (only necessary for PartSet).
     * @param name      Name of the new part set.
     * @param hidden    The flag if the set is hidden.
     * @param proTeaser The flag if the element set is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param node      The XML node for additional information.
     * @return A new part set (PieceSet/CardSet).
     */
    protected abstract T createPartSet(String type, String name, boolean hidden, boolean proTeaser, Node node);

    @Override
    protected final ColorValuePart createElement(PartSetWrapper<T> set, Node node, int sequence, boolean proTeaser, GameConfig config) {
        String color = HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_COLOR);
        int value = HGBaseXMLTools.getAttributeIntValue(node, GameConfigFileReader.CONFIG_VALUE);
        String imagePath = getElementFilePath(node, sequence, set, config);
        Bitmap image = HGBaseGuiTools.loadImage(imagePath);
        if (image == null) {
            HGBaseLog.logWarn("The " + elementConfigKey + "'s image file " + imagePath + " was not found!");
            return null;
        }
        return createColorValuePart(set.getPartSet(), color, sequence, getPartValue(value, sequence), image, node);
    }

    @Override
    protected String getElementFileName(Node node, PartSetWrapper<T> set) {
        return HGBaseXMLTools.getAttributeValue(node, GameConfigFileReader.CONFIG_COLOR);
    }

    /**
     * Returns the standard file name of the image for the part.
     *
     * @param partSetName Name of the part set.
     * @param color       Name of the part's color.
     * @param sequence    Sequence of the part.
     * @param config      The GameConfig object.
     * @return The standard file name.
     */
    protected String calculateImagePath(String partSetName, String color, int sequence, GameConfig config) {
        return config.getElementPath(elementConfigKey) + "/" + partSetName + "/" + color + "-" + sequence + "." + config.getElementExtension(elementConfigKey);
    }

    /**
     * Creates a part with the specified values.
     *
     * @param set      The part set.
     * @param color    The color of the new part.
     * @param sequence The unique sequence of the new part.
     * @param value    The value of the new part.
     * @param image    The image for the new part.
     * @param node     The original XML node the part data were read from.
     * @return A new part.
     */
    protected abstract ColorValuePart createColorValuePart(T set, String color, int sequence, int value, Bitmap image, Node node);

    /**
     * Returns the value of this part.
     *
     * @param value    The value, can be INVALID.
     * @param sequence The sequence.
     * @return The value of this part.
     */
    private int getPartValue(int value, int sequence) {
        return (value == HGBaseTools.INVALID_INT) ? sequence : value;
    }
}