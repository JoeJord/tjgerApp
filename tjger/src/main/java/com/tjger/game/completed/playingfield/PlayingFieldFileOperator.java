package com.tjger.game.completed.playingfield;

import android.graphics.Point;

import com.tjger.lib.XmlMapStringConverter;
import com.tjger.lib.XmlUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Reads and writes playing fields from and to files.
 *
 * @author hagru
 */
public final class PlayingFieldFileOperator {

    /**
     * The name of the parent node for all single field data objects.
     */
    public static final String SINGLE_FIELD_OBJECTS = "singlefieldobjects";

    private static final String PLAYING_FIELD_NODE = "playingfield";
    private static final String SINGLE_FIELDS_NODE = "fields";
    private static final String SINGLE_FIELD_NODE = "field";
    private static final String CONNECTIONS_NODE = "nodes";
    private static final String CONNECTION_NODE = "node";
    private static final String POINTS_NODE = "points";
    private static final String POINT_NODE = "point";
    private static final String PROPERTIES_NODE = "properties";
    private static final String PROPERTY_NODE = "property";
    private static final String SINGLE_FIELD_OBJECT = "object";

    private static final String SIZE_ATTRIBUTE = "size";
    private static final String GRID_ATTRIBUTE = "grid";
    private static final String GRID_SIZE_ATTRIBUTE = "gridsize";
    private static final String GRID_SPAN_ATTRIBUTE = "gridspan";
    private static final String ID_ATTRIBUTE = "id";
    private static final String SHAPE_ATTRIBUTE = "shape";
    private static final String GRID_POSITION_ATTRIBUTE = "grid";
    private static final String POINT_POSITION_ATTRIBUTE = "position";
    private static final String FROM_ATTRIBUTE = "from";
    private static final String TO_ATTRIBUTE = "to";
    private static final String WEIGHT_ATTRIBUTE = "weight";
    private static final String SINGLE_FIELD_ID_ATTRIBUTE = "singlefieldid";

    private static final String DIMENSION_SEPARATOR = "x";
    private static final String POINT_SEPARATOR = "/";

    private static PlayingField fieldToLoad; // helper variable when reading a file

    /**
     * Prevent instantiation of class that is accessed by static methods only.
     */
    private PlayingFieldFileOperator() {
        super();
    }

    /**
     * Reads a playing field from a file.
     *
     * @param file the file to read the field from, must not be null
     * @return the playing field or null if an error occurred
     */
    public static PlayingField fromFile(File file) {
        try {
            return fromStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            HGBaseLog.logError("File '" + file + "' for reading playing field not found!");
            return null;
        }
    }

    /**
     * Reads a playing field from an input stream.
     *
     * @param stream the stream to read the field from, must not be null
     * @return the playing field or null if an error occurred
     */
    public static PlayingField fromStream(InputStream stream) {
        Element root = HGBaseXMLTools.readXML(stream);
        return fromXml(root);
    }

    /**
     * Reads a playing field from an XML document by passing the root node.
     *
     * @param root the root node to read the field from
     * @return the playing field or null if an error occurred
     */
    public static PlayingField fromXml(Node root) {
        fieldToLoad = null;
        if (root != null && PLAYING_FIELD_NODE.equals(root.getNodeName())) {
            readPlayingFieldData(root);
            ChildNodeIterator.run(new ChildNodeIterator(root, PLAYING_FIELD_NODE, null) {

                @Override
                public void performNode(Node node, int index, Object obj) {
                    if (fieldToLoad != null) {
                        ChildNodeIterator.run(new ChildNodeIterator(node, SINGLE_FIELDS_NODE, null) {

                            @Override
                            public void performNode(Node node, int index, Object obj) {
                                readSingleFields(node);
                            }
                        });
                        ChildNodeIterator.run(new ChildNodeIterator(node, CONNECTIONS_NODE, null) {

                            @Override
                            public void performNode(Node node, int index, Object obj) {
                                readConnections(node);
                            }
                        });
                    }

                }
            });
            if (fieldToLoad == null) {
                HGBaseLog.logWarn("Could not load specified playing field!");
            }
        }
        return fieldToLoad;
    }

    /**
     * Reads the basic data for a playing field and creates a new object.
     * This object is stored in {@code fieldToLoad}.
     *
     * @param node the node to read the data from
     */
    private static void readPlayingFieldData(Node node) {
        Dimension size = readDimensionAttribute(node, SIZE_ATTRIBUTE);
        if (size != null) {
            fieldToLoad = new PlayingField(size.width, size.height);
        }
        Dimension grid = readDimensionAttribute(node, GRID_ATTRIBUTE);
        Dimension gridSize = readDimensionAttribute(node, GRID_SIZE_ATTRIBUTE);
        Dimension gridSpan = readDimensionAttribute(node, GRID_SPAN_ATTRIBUTE);
        if (grid != null && gridSize != null && gridSpan != null) {
            if (fieldToLoad == null) {
                fieldToLoad = new PlayingField(grid, gridSize, gridSpan);
            } else {
                // will set grid to mixed mode
                fieldToLoad.setGridSize(gridSize);
                fieldToLoad.setGridSpan(gridSpan);
                fieldToLoad.setGrid(grid);
            }
        }
    }

    /**
     * Reads all single field information from the single fields node.
     *
     * @param node the node containing all single field nodes
     */
    private static void readSingleFields(Node node) {
        if (SINGLE_FIELD_NODE.equals(node.getNodeName())) {
            String id = HGBaseXMLTools.getAttributeValue(node, ID_ATTRIBUTE);
            if (HGBaseTools.hasContent(id)) {
                SingleField field = null;
                ShapeType shape = ShapeType.valueOf(HGBaseXMLTools.getAttributeValue(node, SHAPE_ATTRIBUTE));
                Point gridPosition = readPointAttribute(node, GRID_POSITION_ATTRIBUTE);
                if (gridPosition != null) {
                    field = new SingleField(id, shape, gridPosition);
                } else {
                    List<Point> pointList = readSingleFieldPixelPositions(node);
                    if (pointList.size() >= 2) {
                        field = new SingleField(id, shape, pointList.toArray(new Point[0]));
                    }
                }
                if (field != null) {
                    readSingleFieldProperties(node, field);
                    fieldToLoad.addField(field);
                }
            }
        }
    }

    /**
     * Reads the pixel positions of a single field.
     *
     * @param node the node of the single field
     * @return a list with all pixel positions, may be empty
     */
    private static List<Point> readSingleFieldPixelPositions(Node node) {
        final List<Point> pointList = new ArrayList<>();
        ChildNodeIterator.run(new ChildNodeIterator(node, SINGLE_FIELD_NODE, null) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                ChildNodeIterator.run(new ChildNodeIterator(node, POINTS_NODE, null) {

                    @Override
                    public void performNode(Node node, int index, Object obj) {
                        if (POINT_NODE.equals(node.getNodeName())) {
                            Point p = readPointAttribute(node, POINT_POSITION_ATTRIBUTE);
                            if (p != null) {
                                pointList.add(p);
                            }
                        }
                    }
                });
            }
        });
        return pointList;
    }

    /**
     * Reads the single field properties from the field node.
     *
     * @param node  the node of the single field
     * @param field the single field to add the properties
     */
    private static void readSingleFieldProperties(Node node, SingleField field) {
        ChildNodeIterator.run(new ChildNodeIterator(node, SINGLE_FIELD_NODE, field) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                if (PROPERTIES_NODE.equals(node.getNodeName())) {
                    SingleField field = (SingleField) obj;
                    Map<String, String> properties = XmlUtil.loadMap(node, PROPERTY_NODE, new XmlMapStringConverter());
                    for (Entry<String, String> property : properties.entrySet()) {
                        field.setProperty(property.getKey(), property.getValue());
                    }
                }
            }
        });
    }

    /**
     * Reads all connection information from the connections node.
     *
     * @param node the node containing all connection nodes
     */
    private static void readConnections(Node node) {
        if (CONNECTION_NODE.equals(node.getNodeName())) {
            String fromId = HGBaseXMLTools.getAttributeValue(node, FROM_ATTRIBUTE);
            String toId = HGBaseXMLTools.getAttributeValue(node, TO_ATTRIBUTE);
            int weight = HGBaseXMLTools.getAttributeIntValue(node, WEIGHT_ATTRIBUTE, HGBaseXMLTools.getAttributeIntValue(node, "weigt"));
            SingleField from = fieldToLoad.getField(fromId);
            SingleField to = fieldToLoad.getField(toId);
            if (from != null && to != null && weight != HGBaseTools.INVALID_INT) {
                fieldToLoad.addConnection(from, to, weight);
            }

        }
    }

    /**
     * Writes a playing field to a file.
     *
     * @param file  the file to write the field into, must no be null
     * @param field the playing field to write
     * @return true if writing was successful, otherwise false
     */
    public static boolean toFile(File file, PlayingField field) {
        Document doc = HGBaseXMLTools.createDocument();
        if (toXml(doc, null, field)) {
            return HGBaseXMLTools.writeXML(doc, file.getPath());
        }
        return false;
    }

    /**
     * Writes a playing field to an XML document.
     *
     * @param doc    the xml document
     * @param parent the parent node to add the data, may be null
     * @param field  the playing field to write
     * @return true if writing was successful, otherwise false
     */
    public static boolean toXml(Document doc, Element parent, PlayingField field) {
        if (doc != null) {
            Element root = writePlayingFieldData(field, doc, parent);
            writeSingleFields(field, doc, root);
            writeConnections(field, doc, root);
            return true;
        }
        return false;
    }

    /**
     * Add the basic data of a playing field.
     *
     * @param field  the playing field
     * @param doc    the xml document
     * @param parent the parent node to put the playing field data into, is null if added as root element (default)
     * @return the new created root node
     */
    private static Element writePlayingFieldData(PlayingField field, Document doc, Element parent) {
        Element root = (parent == null) ? HGBaseXMLTools.createElement(doc, null, PLAYING_FIELD_NODE) : parent;
        if (!field.getGridType().isYes()) {
            writeDimensionAttribute(root, SIZE_ATTRIBUTE, field.getSize());
        }
        writeDimensionAttribute(root, GRID_ATTRIBUTE, field.getGrid());
        writeDimensionAttribute(root, GRID_SIZE_ATTRIBUTE, field.getGridSize());
        writeDimensionAttribute(root, GRID_SPAN_ATTRIBUTE, field.getGridSpan());
        return root;
    }

    /**
     * Add single fields to the playing field.
     *
     * @param field the playing field
     * @param doc   the xml document
     * @param root  the root node to add the single fields node
     */
    private static void writeSingleFields(PlayingField field, Document doc, Element root) {
        Element fieldsNode = HGBaseXMLTools.createElement(doc, root, SINGLE_FIELDS_NODE);
        for (SingleField sf : field.getFields()) {
            Element fieldNode = HGBaseXMLTools.createElement(doc, fieldsNode, SINGLE_FIELD_NODE);
            fieldNode.setAttribute(ID_ATTRIBUTE, sf.getId());
            fieldNode.setAttribute(SHAPE_ATTRIBUTE, sf.getShape().name());
            Map<String, String> properties = sf.getProperties();
            if (!properties.isEmpty()) {
                XmlUtil.saveMap(doc, fieldNode, PROPERTIES_NODE, PROPERTY_NODE, properties, new XmlMapStringConverter());
            }
            Point grid = sf.getGridPosition();
            if (grid != null) {
                writePointAttribute(fieldNode, GRID_POSITION_ATTRIBUTE, grid);
            } else {
                Element pointsNode = HGBaseXMLTools.createElement(doc, fieldNode, POINTS_NODE);
                for (Point point : sf.getPixelPositions()) {
                    Element pointNode = HGBaseXMLTools.createElement(doc, pointsNode, POINT_NODE);
                    writePointAttribute(pointNode, POINT_POSITION_ATTRIBUTE, point);
                }
            }
        }
    }

    /**
     * Add connections between single fields.
     *
     * @param field the playing field
     * @param doc   the xml document
     * @param root  the root node to add the connections node
     */
    private static void writeConnections(PlayingField field, Document doc, Element root) {
        Element connectionsNode = HGBaseXMLTools.createElement(doc, root, CONNECTIONS_NODE);
        for (Entry<SingleField, Map<SingleField, Integer>> connection : field.getConnectionsMap().entrySet()) {
            String id = connection.getKey().getId();
            for (Entry<SingleField, Integer> target : connection.getValue().entrySet()) {
                Element connectionNode = HGBaseXMLTools.createElement(doc, connectionsNode, CONNECTION_NODE);
                connectionNode.setAttribute(FROM_ATTRIBUTE, id);
                connectionNode.setAttribute(TO_ATTRIBUTE, target.getKey().getId());
                connectionNode.setAttribute(WEIGHT_ATTRIBUTE, target.getValue().toString());
            }
        }
    }

    /**
     * Saves a dimension object as attribute value.
     *
     * @param node      the node to add the attribute
     * @param attribute the name of the attribute
     * @param dim       the dimension to save, may be null
     */
    private static void writeDimensionAttribute(Element node, String attribute, Dimension dim) {
        if (dim != null) {
            String value = concatenateIntegers(DIMENSION_SEPARATOR, dim.width, dim.height);
            node.setAttribute(attribute, value);
        }
    }

    /**
     * Saves a dimension object as attribute value.
     *
     * @param node      the node to add the attribute
     * @param attribute the name of the attribute
     * @return the dimension object or null if it invalid
     */
    private static Dimension readDimensionAttribute(Node node, String attribute) {
        String value = HGBaseXMLTools.getAttributeValue(node, attribute);
        int[] intValues = getSeparatedIntegers(value, DIMENSION_SEPARATOR, 2);
        return (intValues == null) ? null : new Dimension(intValues[0], intValues[1]);
    }

    /**
     * Saves a point object as attribute value.
     *
     * @param node      the node to add the attribute
     * @param attribute the name of the attribute
     * @param point     the point to save, may be null
     */
    private static void writePointAttribute(Element node, String attribute, Point point) {
        if (point != null) {
            String value = concatenateIntegers(POINT_SEPARATOR, point.x, point.y);
            node.setAttribute(attribute, value);
        }
    }

    /**
     * Saves a point object as attribute value.
     *
     * @param node      the node to add the attribute
     * @param attribute the name of the attribute
     * @return the point object or null if it invalid
     */
    private static Point readPointAttribute(Node node, String attribute) {
        String value = HGBaseXMLTools.getAttributeValue(node, attribute);
        int[] intValues = getSeparatedIntegers(value, POINT_SEPARATOR, 2);
        return (intValues == null) ? null : new Point(intValues[0], intValues[1]);
    }

    /**
     * Concatenates integers by the given separator.
     *
     * @param separator the separator
     * @param values    the integer values
     * @return a string with the integer values concatenated by the separator
     */
    private static String concatenateIntegers(String separator, int... values) {
        String[] text = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            text[i] = String.valueOf(values[i]);
        }
        return HGBaseTools.toStringText(text, separator);
    }

    /**
     * Returns separated integer values from a string separated by the given separator.
     *
     * @param value     the string value to get the integers from
     * @param separator the separator between the integer values
     * @param count     the number of expected integers
     * @return an array with count size holding the integers or null if values are invalid
     */
    private static int[] getSeparatedIntegers(String value, String separator, int count) {
        String[] stringValues = value.split(separator);
        if (stringValues.length == count) {
            int[] result = new int[count];
            for (int i = 0; i < count; i++) {
                int intValue = HGBaseTools.toInt(stringValues[i]);
                if (intValue == HGBaseTools.INVALID_INT) {
                    // one integer is invalid, can exit with null
                    return null;
                } else {
                    result[i] = intValue;
                }
            }
            return result;
        }
        return null;
    }

    /**
     * Creates an xml node that stores the data object for each single field using the given {@link XmlSingleFieldDataConverter}.
     *
     * @param doc        the xml document
     * @param parentNode the parent node to append the new node, may be null
     * @param field      the playing field that contains all single fields
     * @param converter  the data object converter
     * @return the new created xml element
     */
    public Element createNodeWithAllFieldData(Document doc, Element parentNode, PlayingField field, XmlSingleFieldDataConverter converter) {
        Element dataNode = HGBaseXMLTools.createElement(doc, parentNode, SINGLE_FIELD_OBJECTS);
        for (SingleField sf : field.getFields()) {
            Object data = sf.getData();
            if (data != null) {
                Element singleNode = HGBaseXMLTools.createElement(doc, dataNode, SINGLE_FIELD_OBJECT);
                converter.dataToXml(doc, singleNode, data);
                singleNode.setAttribute(SINGLE_FIELD_ID_ATTRIBUTE, sf.getId());
            }
        }
        return dataNode;
    }

    /**
     * Reads all data objects from the given node and assigns them to the according single fields using the
     * given {@link XmlSingleFieldDataConverter}. The {@code dataNode} can be parent node or the node created
     * with {@link #createNodeWithAllFieldData(Document, Element, PlayingField, XmlSingleFieldDataConverter)}.
     *
     * @param dataNode  the xml node to read the data from, must not be null
     * @param field     the playing field holding all single fields to assign the data to
     * @param converter the data object converter
     */
    public void readAllFieldDataFromNode(Node dataNode, final PlayingField field, final XmlSingleFieldDataConverter converter) {
        if (SINGLE_FIELD_OBJECTS.equals(dataNode.getNodeName())) {
            ChildNodeIterator.run(new ChildNodeIterator(dataNode, SINGLE_FIELD_OBJECTS, null) {

                @Override
                public void performNode(Node node, int index, Object obj) {
                    if (SINGLE_FIELD_OBJECT.equals(node.getNodeName())) {
                        String fieldId = HGBaseXMLTools.getAttributeValue(node, SINGLE_FIELD_ID_ATTRIBUTE);
                        SingleField sf = field.getField(fieldId);
                        if (sf != null) {
                            Object data = converter.dataFromXml(node);
                            sf.setData(data);
                        }
                    }
                }
            });
        } else {
            ChildNodeIterator.run(new ChildNodeIterator(dataNode, null) {

                @Override
                public void performNode(Node node, int index, Object obj) {
                    if (SINGLE_FIELD_OBJECTS.equals(node.getNodeName())) {
                        readAllFieldDataFromNode(node, field, converter);
                    }
                }
            });
        }
    }

}
