package com.tjger.game.completed.playingfield;

import android.graphics.Point;

import com.tjger.lib.ShortestPath;
import com.tjger.lib.ShortestPathFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.awt.Rectangle;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The playing field class that gives access to all data related to a standard playing field.
 *
 * @author hagru
 */
public class PlayingField {

    private final Map<String, SingleField> fields = new HashMap<>();
    private final Map<SingleField, Map<SingleField, Integer>> connections = new HashMap<>();
    /**
     * Map for the properties of a connection. The key of the first map is the from field. The key of the second map is the to field. The key of the third map is the property key. The property value is the value of the third map.
     */
    private final Map<SingleField, Map<SingleField, Map<String, String>>> connectionProperties = new HashMap<>();
    private Dimension size;
    private GridType gridType;
    private Dimension grid;
    private Dimension gridSize;
    private Dimension gridSpan;
    private int idCounter = 0;

    /**
     * Create a new playing field without grid.
     *
     * @param width  the width of the field in pixels
     * @param height the height of the field in pixels
     */
    public PlayingField(int width, int height) {
        this.size = new Dimension(width, height);
        this.gridType = GridType.NO;
    }

    /**
     * Create a new playing field with a grid.<p>
     * The size of the field (in pixels) is calculated by the grid values.
     *
     * @param grid     the horizontal and vertical dimension of the grid, must not be null
     * @param gridSize the size of a single grid field, must not be null
     * @param gridSpan the horizontal and vertical span between grid fields, must not be null
     */
    public PlayingField(Dimension grid, Dimension gridSize, Dimension gridSpan) {
        this.grid = HGBaseTools.requireNonNull(grid, "The grid object must not be null!");
        this.gridSize = HGBaseTools.requireNonNull(gridSize, "The grid size must not be null!");
        this.gridSpan = HGBaseTools.requireNonNull(gridSpan, "The grid span must not be null!");
        this.gridType = GridType.YES;
        calculateSizeByGrid();
    }

    /**
     * Calculates the size of the playing field by the grid data.
     * The grid must be set and must not be null!
     */
    private void calculateSizeByGrid() {
        if (grid != null) {
            // use internal helper method; correct result is given as grid positions start with 0
            Point edgeBottomRight = getPositionOnGrid(grid.width, grid.height);
            this.size = new Dimension(edgeBottomRight.x, edgeBottomRight.y);
        }
    }

    /**
     * @return the size of the playing field in pixels
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Returns the (last) grid dimension that has been used when creating the field.
     *
     * @return the grid dimension, can be null if no grid is used
     * @see #getGridType()
     */
    public Dimension getGrid() {
        return grid;
    }

    /**
     * Set a new grid dimension.<p>
     * If a fixed grid was used before and null is passed, all grid information will be removed from the single fields.
     *
     * @param grid the new grid dimension, may be null
     */
    public void setGrid(Dimension grid) {
        if (grid == null) {
            if (gridType == GridType.YES) {
                // there was a grid before, so remove all grid information from single fields
                for (SingleField field : this.fields.values()) {
                    Point gridPosition = field.getGridPosition();
                    Point point1 = getPositionOnGrid(gridPosition.x, gridPosition.y);
                    Point point2 = new Point(point1.x + gridSize.width - 1, point1.y + gridSize.height - 1);
                    Point[] pixelPositions = new Point[]{point1, point2};
                    field.setPixelPositions(pixelPositions);
                }
            }
            gridType = GridType.NO;
        } else {
            if (gridType == GridType.NO) {
                gridType = GridType.MIXED;
            }
            calculateSizeByGrid();
        }
        this.grid = grid;
    }

    /**
     * Returns the grid type of the playing field.
     * Only if type is {@link GridType#YES} grid information can be used.
     *
     * @return the used grid type
     */
    public GridType getGridType() {
        return gridType;
    }

    /**
     * Returns the horizontal and vertical span of a grid.
     *
     * @return the horizontal and vertical span of a grid, may be null
     */
    public Dimension getGridSpan() {
        return gridSpan;
    }

    /**
     * Set a new grid span.<p>
     * This is only relevant if a grid is available. Changing the span does not influence the grid type.
     *
     * @param gridSpan the grid span, must not be null if a grid is used
     */
    public void setGridSpan(Dimension gridSpan) {
        this.gridSpan = (grid == null) ? gridSpan : HGBaseTools.requireNonNull(gridSpan, "The grid span must not be null!");
        calculateSizeByGrid();
    }

    /**
     * Returns the width and height of a single grid field.
     *
     * @return the width and height of a single grid field, may be null
     */
    public Dimension getGridSize() {
        return gridSize;
    }

    /**
     * Set a new size of a single grid field.<p>
     * This is only relevant if a grid is available. Changing the size does not influence the grid type.
     *
     * @param gridSize the width and height of a single grid field, must not be null if a grid is used
     */
    public void setGridSize(Dimension gridSize) {
        this.gridSize = (grid == null) ? gridSize : HGBaseTools.requireNonNull(gridSize, "The grid size must not be null!");
        calculateSizeByGrid();
    }

    /**
     * Returns the grid position for a given pixel position or null if the position is not on the grid (or the grid is not set).
     *
     * @param x the horizontal pixel position
     * @param y the vertical pixel position
     * @return a point that contains the row and column information for the grid or null
     */
    public Point getGridForPosition(int x, int y) {
        if (grid != null) {
            int gridX = x / (gridSpan.width + gridSize.width);
            int gridY = y / (gridSpan.height + gridSize.height);
            if (gridX < grid.width && gridY < grid.height && x % (gridSpan.width + gridSize.width) >= gridSpan.width && y % (gridSpan.height + gridSize.height) >= gridSpan.height) {
                return new Point(gridX, gridY);
            }
        }
        return null;
    }

    /**
     * Returns the pixel position on the grid for a column and a row (both starting with 0).
     * The fields {@code gridSize} and {@code gridSpan} must not be null.
     *
     * @param column the column in the grid, must not be negative for valid result
     * @param row    the row in the grid, must not be negative for valid result
     * @return the the pixel position on the grid
     */
    private Point getPositionOnGrid(int column, int row) {
        int x = column * gridSize.width + (column + 1) * gridSpan.width;
        int y = row * gridSize.height + (row + 1) * gridSpan.height;
        return new Point(x, y);
    }

    /**
     * Returns the pixel positions of the given single field.
     *
     * @param field the single field to get the positions for
     * @return the pixel points of this field, is calculated for a grid playing field
     */
    public Point[] getPixelPositions(SingleField field) {
        Point gridPosition = field.getGridPosition();
        if (gridPosition != null) {
            Point[] positions = new Point[2];
            positions[0] = getPositionOnGrid(gridPosition.x, gridPosition.y);
            positions[1] = new Point(positions[0].x + gridSize.width - 1, positions[0].y + gridSize.height - 1);
            return positions;
        } else {
            return field.getPixelPositions();
        }
    }

    /**
     * Returns the pixel position and dimension of the specified single field.
     *
     * @param field The single field to get the position and dimension for.
     * @return The pixel position and dimension of the specified single field.
     */
    public Rectangle getFieldRectangle(SingleField field) {
        Point[] pos = getPixelPositions(field);
        return new Rectangle(pos[0].x, pos[0].y, pos[1].x - pos[0].x, pos[1].y - pos[0].y);
    }

    /**
     * Returns a field at the given pixel position.
     *
     * @param x the x coordinate in pixels
     * @param y the y coordinate in pixels
     * @return the field at this position or null if there is no such field
     */
    public SingleField getFieldAtPosition(int x, int y) {
        // search all fields in inverse order, to report an overlapping field before the field below
        List<SingleField> sortedFields = new ArrayList<>(getFields());
        for (int i = sortedFields.size() - 1; i >= 0; i--) {
            SingleField field = sortedFields.get(i);
            if (ShapeType.POLYGON.equals(field.getShape())) {
                // calculate whether the point is within the polygon
                if (HGBaseGuiTools.isPointInPolygon(new Point(x, y), getPixelPositions(field))) {
                    return field;
                }
            } else {
                // just use the rectangle area for the check, only problematic with overlapping ellipses/circles
                Point[] positions = getPixelPositions(field);
                if (positions != null && positions.length == 2 && x >= positions[0].x && x <= positions[1].x && y >= positions[0].y && y <= positions[1].y) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * Returns the id of the field at the given pixel position.
     *
     * @param x the x coordinate in pixels
     * @param y the y coordinate in pixels
     * @return the id of the field at this position or null if there is no such field
     */
    public String getFieldIdAtPosition(int x, int y) {
        SingleField sf = getFieldAtPosition(x, y);
        return (sf == null) ? null : sf.getId();
    }

    /**
     * Returns the field at a given grid position, will be null if there is no grid or there is no field at
     * that position.
     *
     * @param gridPos the grid position
     * @return the field at the given grid position or null
     */
    public SingleField getFieldAtGridPosition(Point gridPos) {
        if (gridPos != null && getGrid() != null) {
            for (SingleField field : getFields()) {
                if (gridPos.equals(field.getGridPosition())) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * Returns all single fields in the default order of the fields.
     *
     * @return an unmodifiable collection of single fields, may be empty
     */
    public Collection<SingleField> getFields() {
        Collection<SingleField> sortedFields = new TreeSet<>(fields.values());
        return Collections.unmodifiableCollection(sortedFields);
    }

    /**
     * Returns the single field for the given id.
     *
     * @param id the id to get the particular field for
     * @return the field with the id or null if such a field does not exist
     */
    public SingleField getField(String id) {
        return fields.get(id);
    }

    /**
     * Returns the first field that has the given value as property set.
     *
     * @param key   the property key
     * @param value the value to test for
     * @return the first field or null
     */
    public SingleField getFirstFieldWithProperty(String key, String value) {
        Map<String, String> properties = new HashMap<>();
        properties.put(key, value);
        return getFirstFieldWithProperties(properties);
    }

    /**
     * Returns the first field that has the given property values set.
     *
     * @param properties a map with property keys and according values to test
     * @return the first field or null
     */
    public SingleField getFirstFieldWithProperties(Map<String, String> properties) {
        for (SingleField sf : getFields()) {
            if (checkPropertiesForField(sf, properties)) {
                return sf;
            }
        }
        return null;
    }

    /**
     * Returns all fields that have the given value as property set.
     *
     * @param key   the property key
     * @param value the value to test for
     * @return all fields with the given property set, may be an empty collection
     */
    public Collection<SingleField> getFieldsWithProperty(String key, String value) {
        Map<String, String> properties = new HashMap<>();
        properties.put(key, value);
        return getFieldsWithProperties(properties);
    }

    /**
     * Returns all fields that have the given property values set.
     *
     * @param properties a map with property keys and according values to test
     * @return all fields with the given properties set, may be an empty collection
     */
    public Collection<SingleField> getFieldsWithProperties(Map<String, String> properties) {
        Collection<SingleField> localFields = new ArrayList<>();
        for (SingleField sf : getFields()) {
            if (checkPropertiesForField(sf, properties)) {
                localFields.add(sf);
            }
        }
        return Collections.unmodifiableCollection(localFields);
    }

    /**
     * Checks the field for the given properties and returns true if all properties of the field have the given values.
     *
     * @param field      the field to check the properties for
     * @param properties the properties to check
     * @return true if the field has all property values set that are given by the map, otherwise false
     */
    private boolean checkPropertiesForField(SingleField field, Map<String, String> properties) {
        for (Entry<String, String> property : properties.entrySet()) {
            String fieldValue = field.getProperty(property.getKey());
            if (!HGBaseTools.equalObject(fieldValue, property.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all fields that have to given data assigned to.
     *
     * @param data the data that is assigned to single fields, must not be null
     * @return a list with all single fields, may be empty
     */
    public Collection<SingleField> getFieldsWithData(Object data) {
        Collection<SingleField> fieldsWithData = new ArrayList<>();
        for (SingleField field : fields.values()) {
            if (data.equals(field.getData())) {
                fieldsWithData.add(field);
            }
        }
        return fieldsWithData;
    }

    /**
     * Add a single field to the playing field.
     *
     * @param field the new field to add
     */
    public void addField(SingleField field) {
        fields.put(field.getId(), field);
    }

    /**
     * Removes the single field and all connections from and to this field.
     *
     * @param field the single field to remove from the playing field
     */
    public void removeField(SingleField field) {
        if (fields.remove(field.getId()) != null) {
            connections.remove(field);
            for (Map<SingleField, Integer> connection : connections.values()) {
                connection.remove(field);
            }
        }
    }

    /**
     * Add a connection from the start field to the target field with a given weight.<p>
     * If there is already a connection from start to target, the old value will be overwritten.
     *
     * @param start  the start field of the connection, must not be null
     * @param target the target field of the connection, must not be null
     * @param weight the weight of the connection, must be 1 or higher
     * @throws IllegalArgumentException if weight is to small
     */
    public void addConnection(SingleField start, SingleField target, int weight) {
        if (weight < 1) {
            throw new IllegalArgumentException("The weight of the connection must be 1 or higher");
        }
        Map<SingleField, Integer> targets = HGBaseTools.getOrCreateValue(connections, start, new HashMap<>());
        targets.put(target, weight);
    }

    /**
     * Removes the connection between start and target field. Nothing will be done if there is no connection.
     *
     * @param start  the start field of the connection to remove
     * @param target the target field of the connection to remove
     */
    public void removeConnection(SingleField start, SingleField target) {
        Map<SingleField, Integer> targets = this.connections.get(start);
        if (targets != null) {
            targets.remove(target);
        }
    }

    /**
     * Returns the internal map for the connections.
     *
     * @return the unmodifiable internal map for the connections
     */
    Map<SingleField, Map<SingleField, Integer>> getConnectionsMap() {
        return Collections.unmodifiableMap(connections);
    }

    /**
     * Returns a collection with all possible connections that are available for the field.
     *
     * @return an unmodifiable collection of possible connections
     */
    public Collection<SingleFieldConnection> getConnections() {
        Set<SingleFieldConnection> sfc = new LinkedHashSet<>();
        for (Entry<SingleField, Map<SingleField, Integer>> entry : connections.entrySet()) {
            SingleField f1 = entry.getKey();
            for (SingleField f2 : entry.getValue().keySet()) {
                sfc.add(new SingleFieldConnection(f1, f2));
            }
        }
        return Collections.unmodifiableCollection(sfc);
    }

    /**
     * Returns the weight between two single fields.
     *
     * @param start  the start field
     * @param target the target field
     * @return the weight between the two fields or 0 if there is no connection
     */
    private int getWeight(SingleField start, SingleField target) {
        Map<SingleField, Integer> targets = this.connections.get(start);
        int weight = 0;
        if (targets != null) {
            Integer w = targets.get(target);
            if (w != null) {
                weight = w;
            }
        }
        return weight;
    }

    /**
     * Returns the weight of the connection(s) between the given fields.
     * Return 0 if there is no way connecting the given fields.
     *
     * @param fields an arbitrary number of fields to get the connection weight, must be at least two fields
     * @return the weight between the fields or 0 if there is no connection
     */
    public int getConnectionWeight(SingleField... fields) {
        if (fields.length >= 2) {
            int weight = 0;
            for (int i = 0; i < fields.length - 1; i++) {
                int w = getWeight(fields[i], fields[i + 1]);
                if (w == 0) {
                    return 0;
                } else {
                    weight += w;
                }
            }
            return weight;
        } else {
            return 0;
        }
    }

    /**
     * Sets the specified properties for the specified connection.
     *
     * @param from       The origin field of the connection.
     * @param to         The target field of the connection.
     * @param properties The properties to set.
     */
    public void setConnectionProperties(SingleField from, SingleField to, Map<String, String> properties) {
        connectionProperties.computeIfAbsent(from, k -> new HashMap<>()).put(to, properties);
    }

    /**
     * Sets the specified property for the specified connection.
     *
     * @param from     The origin field of the connection.
     * @param to       The target field of the connection.
     * @param property The property to set.
     */
    public void setConnectionProperty(SingleField from, SingleField to, Entry<String, String> property) {
        if (property == null) {
            return;
        }
        connectionProperties.computeIfAbsent(from, k -> new HashMap<>()).computeIfAbsent(to, k -> new HashMap<>()).put(property.getKey(), property.getValue());
    }

    /**
     * Returns the properties for the specified connection.
     *
     * @param from The origin field of the connection.
     * @param to   The target field of the connection.
     * @return The properties for the specified connection.
     */
    public Map<String, String> getConnectionProperties(SingleField from, SingleField to) {
        return connectionProperties.computeIfAbsent(from, k -> new HashMap<>()).computeIfAbsent(to, k -> new HashMap<>());
    }

    /**
     * Returns the properties for the specified connection.
     *
     * @param connection The connection.
     * @return The properties for the specified connection.
     */
    public Map<String, String> getConnectionProperties(SingleFieldConnection connection) {
        return (connection == null) ? new HashMap<>() : getConnectionProperties(connection.getFirst(), connection.getSecond());
    }

    /**
     * Returns the value of the specified property for the specified connection.
     *
     * @param from The origin field of the connection.
     * @param to   The target field of the connection.
     * @param key  The key of the property to retrieve.
     * @return The value of the specified property for the specified connection.
     */
    public String getConnectionProperty(SingleField from, SingleField to, String key) {
        return getConnectionProperties(from, to).get(key);
    }

    /**
     * Returns a collection with all direct neighbors of the given field.
     *
     * @param field the field to get the neighbors for
     * @return an unmodifiable collection with neighbors, can be empty
     */
    public Collection<SingleField> getNeighbours(SingleField field) {
        return getNeighbours(field, (origin, target) -> true);
    }

    /**
     * Returns a collection with all direct neighbors of the given field where the connection is tested as valid.
     *
     * @param field     The field to get the neighbors for.
     * @param condition The condition a connection to a target field must met. The parameters for the {@link BiPredicate} are the origin and the target fields.
     * @return A collection with all direct neighbors of the given field where the connection is tested as valid.
     */
    public Collection<SingleField> getNeighbours(SingleField field, BiPredicate<SingleField, SingleField> condition) {
        Map<SingleField, Integer> neighbours = connections.get(field);
        if (neighbours == null || neighbours.isEmpty()) {
            return Collections.emptySet();
        }
        return neighbours.keySet().stream().filter(target -> condition.test(field, target)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the shortest path from the start field to the target field.
     *
     * @param start  the start field
     * @param target the target field
     * @return the shortest path object, will be null if there is no path available
     */
    public ShortestPath<SingleField> getShortestPath(SingleField start, SingleField target) {
        return getShortestPath(start, target, 0);
    }

    /**
     * Returns the shortest path from the start field to the target field with a maximum search depth.
     *
     * @param start    the start field
     * @param target   the target field
     * @param maxDepth the maximum search depth, 0 for infinite depth
     * @return the shortest path object, will be null if there is no path available
     */
    public ShortestPath<SingleField> getShortestPath(SingleField start, SingleField target, int maxDepth) {
        return ShortestPathFinder.find(new PlayingFieldShortestPathMethods(this, start, target, maxDepth));
    }

    /**
     * Returns a collection of single fields that is reachable from the start field with the given weight.
     *
     * @param start         the start field
     * @param weight        the exact weight to reach possible targets
     * @param allowTurnBack true if it is allowed to turn back during the way, i.e., the same field between
     *                      start and target can be used multiple times
     * @return an unmodifiable collection of all possible targets, may be empty
     */
    public Collection<SingleField> getReachableFields(SingleField start, int weight, boolean allowTurnBack) {
        Set<SingleField> targets = getReachableFields(start, weight);
        if (!allowTurnBack) {
            for (SingleField target : new HashSet<>(targets)) {
                ShortestPath<SingleField> path = getShortestPath(start, target, weight);
                if (path.getPathWeight() < weight) {
                    targets.remove(target);
                }
            }
        }
        return (!targets.isEmpty()) ? Collections.unmodifiableCollection(targets) : Collections.emptySet();
    }

    /**
     * Internal method to calculate the reachable fields (including turn back).
     *
     * @param start  the start field
     * @param weight the exact weight to reach possible targets
     * @return an modifiable collection of all possible targets, may be empty
     */
    private Set<SingleField> getReachableFields(SingleField start, int weight) {
        Set<SingleField> targets = new HashSet<>();
        for (SingleField neighbour : getNeighbours(start)) {
            int w = getWeight(start, neighbour);
            int remaining = weight - w;
            if (remaining == 0) {
                targets.add(neighbour);
            } else if (remaining > 0) {
                targets.addAll(getReachableFields(neighbour, remaining));
            }
        }
        return targets;
    }

    /**
     * Checks whether the target field is reachable from the start field with the given weight.
     *
     * @param start         the start field
     * @param target        the target field
     * @param weight        the exact weight to possibly reach the target field
     * @param allowTurnBack true if it is allowed to turn back during the way, i.e., the same field between
     *                      start and target can be used multiple times
     * @return true if the target field is reachable, otherwise false
     */
    public boolean isFieldReachable(SingleField start, SingleField target, int weight, boolean allowTurnBack) {
        return getReachableFields(start, weight, allowTurnBack).contains(target);
    }

    /**
     * Returns a sorted set with all available property keys of the single fields.<p>
     * This call is an expensive call as all fields are searched every time the method is called.
     *
     * @return an unmodifiable set with all available property keys, may be empty
     */
    public Set<String> getFieldPropertyKeys() {
        Set<String> keys = new TreeSet<>();
        for (SingleField field : fields.values()) {
            keys.addAll(field.getPropertyKeys());
        }
        return Collections.unmodifiableSet(keys);
    }

    /**
     * Returns a sorted set with all available property values for a given key of the single fields.<p>
     * This call is an expensive call as all fields are searched every time the method is called.
     *
     * @return an unmodifiable set with all available property values for the given key, may be empty
     */
    public Set<String> getFieldPropertyValues(String key) {
        Set<String> values = new TreeSet<>();
        for (SingleField field : fields.values()) {
            String value = field.getProperty(key);
            if (HGBaseTools.hasContent(value)) {
                values.add(value);
            }
        }
        return Collections.unmodifiableSet(values);
    }

    /**
     * Returns the next id for a single field.
     *
     * @return the next available id
     * @throws IllegalStateException if the maximum number of possible fields has been reached
     */
    public String getNextFieldId() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            idCounter++;
            String newId = String.valueOf(idCounter);
            if (!fields.containsKey(newId)) {
                // found an id that is not used yet
                return newId;
            }
        }
        throw new IllegalStateException("It is not possible to get a new id, the maximum number of possible fields (" + Integer.MAX_VALUE + ") has been reached!");
    }

    /**
     * Clears all data objects of the single fields.
     */
    public void clearSingleFieldData() {
        for (SingleField sf : fields.values()) {
            sf.clearData();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayingField: gridType=" + getGridType() + ", grid=" + getGrid() + ", size=" + getSize() + ", fields=" + getFields().size();
    }

}
