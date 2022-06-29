package com.tjger.lib;

import com.tjger.game.MoveInformation;

/**
 * This move information should be used by games that shall get a computer opponent 
 * that artificial intelligence methods. It extends the normal MoveInformation by a method that 
 * holds an evaluation value.
 * 
 * @see AiAlgorithms
 * @author hagru
 */
public abstract class AiMoveInformation implements MoveInformation, Cloneable, Comparable<AiMoveInformation> {
    
    private long evaluationValue = 0;
    
    /**
     * @return The evaluation value of this move.
     */
    public long getEvaluationValue() {
        return evaluationValue;
    }
    
    /**
     * @param value The evaluation value to set.
     */
    public void setEvaluationValue(long value) {
        this.evaluationValue = value;
    }    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.valueOf(getEvaluationValue());
    }
    
    /**
     * Force subclasses to implement this method.
     */
    @Override
    abstract public Object clone();

    /**
     * @return A clone of the current ai move;
     */
    public AiMoveInformation cloneMove() {
        AiMoveInformation newMove = (AiMoveInformation)this.clone();
        newMove.setEvaluationValue(getEvaluationValue());
        return newMove;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AiMoveInformation m2) {
        if (m2 != null) {
        	if (getEvaluationValue() < m2.getEvaluationValue()) return -1;
        	if (getEvaluationValue() > m2.getEvaluationValue()) return +1;        
        }
        return 0;
    }    
    
}
