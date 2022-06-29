package com.tjger.lib;

import com.tjger.game.MoveInformation;

/**
 * A move of something from a source field (defined by id) to a target field (defined by id).
 * 
 * @author hagru
 */
public class FieldMove implements MoveInformation {

    private String sourceFieldId;
    private String targetFieldId;
    
    /**
     * Moves something from the source field to the target field.
     * 
     * @param sourceFieldId the id of the source field
     * @param targetFieldId the id of the target field
     */
    public FieldMove(String sourceFieldId, String targetFieldId) {
        this.sourceFieldId = sourceFieldId;
        this.targetFieldId = targetFieldId;
    }

    /**
     * @return the id of the source field
     */
    public String getSourceFieldId() {
        return sourceFieldId;
    }

    /**
     * @return the id of the target field
     */
    public String getTargetFieldId() {
        return targetFieldId;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldMove) {
            FieldMove fm2 = (FieldMove)obj;
            return (sourceFieldId.equals(fm2.getSourceFieldId())
                 && targetFieldId.equals(fm2.getTargetFieldId()));
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        String ids = sourceFieldId + "/" + targetFieldId;
        return ids.hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Field move from " + getSourceFieldId() + " to " + getTargetFieldId();
    }
}
