package com.tjger.game.completed.playingfield;

import java.util.Set;
import java.util.TreeSet;

import android.util.Pair;

/**
 * Helper class that represents a connection, independent of the direction or weight.
 * 
 * @author hagru
 */
public class SingleFieldConnection extends Pair<SingleField, SingleField> {
    
    /**
     * Creates a new connection, the order of the fields does not matter for the quality of the connection.
     * 
     * @param f1 the first field
     * @param f2 the second field
     */
    public SingleFieldConnection(SingleField f1, SingleField f2) {
        super(f1, f2);
    }

    /* (non-Javadoc)
     * @see hgb.lib.Pair#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        if (o2 instanceof SingleFieldConnection) {
            SingleFieldConnection c2 = (SingleFieldConnection)o2;
            return toString().equals(c2.toString());
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see hgb.lib.Pair#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Set<String> fields = new TreeSet<>();
        fields.add(first.getId());
        fields.add(second.getId());
        return fields.toString();
    }
    
    /**
     * @return the first field
     */
    public SingleField getFirst() {
    	return first;
    }

    /**
     * @return the second field
     */
    public SingleField getSecond() {
    	return second;
    }
 }
