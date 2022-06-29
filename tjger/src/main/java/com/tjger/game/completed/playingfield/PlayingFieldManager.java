package com.tjger.game.completed.playingfield;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Manages the available playing fields.
 *
 * @author hagru
 */
public final class PlayingFieldManager {

    private static final PlayingFieldManager INSTANCE = new PlayingFieldManager();
    
    private final Map<String,PlayingField> fields = new HashMap<>();

    /**
     * Prevent instantiation of the singleton.
     */
    private PlayingFieldManager() {
        super();
    }

    /**
     * Returns the one and only instance of the playing field manager.
     *
     * @return the one and only instance of the playing field manager
     */
    public static PlayingFieldManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Lets the manager know a new playing field. The name of the field must be unique.
     * 
     * @param name the name of the field, i.e., the file name without extension.
     * @param field the new playing field to add
     */
    public void addField(String name, PlayingField field) {
        fields.put(name, field);
    }
    
    /**
     * Returns names, i.e., the file name without extension, of the playing fields in the order as they were
     * added to the playing field manager.
     * 
     * @return an array with names, can be empty
     */
    public String[] getFieldNames() {
        return fields.keySet().toArray(new String[fields.size()]);
    }
    
    /**
     * Returns the playing field for the given name.
     * 
     * @param name the name of the playing field
     * @return the corresponding playing field or null if the name is invalid
     */
    public PlayingField getField(String name) {
        return fields.get(name);
    }
    
    /**
     * Returns the name of the given field or null if this field is not stored by the manager.
     * 
     * @param field the playing field to get the name for, may be null
     * @return the (first) name of the given field or null if field is not stored by the manager
     */
    public String getFieldName(PlayingField field) {
        if (field != null) {
            for (Entry<String, PlayingField> entry : fields.entrySet()) {
                if (field.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

}
