package com.tjger.gui;

import com.tjger.gui.completed.Part;

/**
 * Interface for changing the sort behaviour of parts. 
 * To activate one, use the methode <code>setPartSorter</code> of the GameManager.
 * 
 * @author hagru
 */
public interface PartSorter {
    
    /**
     * Compares two parts (e.g. two cards).
     *  
     * @param part1 The first part.
     * @param part2 The second part.
     * @return -1, 0 or 1.
     */
    public int compareParts(Part part1, Part part2);

}
