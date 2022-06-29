package com.tjger.lib;

import java.util.Date;

import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Helps to implement a timed action that should last at least a given time.
 * 
 * @author hagru
 */
public abstract class TimeAction extends Thread {
    
    final private long minimumTime;
    private boolean cancelAble;

    /**
     * @param minimumTime The minimum time, the action shall last.
     */
    public TimeAction(long minimumTime) {
    	super();
        this.minimumTime = minimumTime;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    /**
     * Runs the given action. The method <code>doAction</code> is called. 
     * The the thread waits until the minimum time is over (if it wasn't yet) 
     * and then <code>afterAction</code> is called.
     * 
     * @param action The action implementation. 
     */
    public static void run(TimeAction action) {
        action.start();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        GameEngine engine = GameManager.getInstance().getGameEngine();
        engine.addTimeAction(this);
        cancelAble = true;
        Date timeStart = new Date();
        this.doAction();
        long diff = HGBaseTools.getTimeDifference(timeStart);
        boolean interrupted = false;
        if (diff>=0 && diff<minimumTime) {
            try {
                Thread.sleep(minimumTime-diff);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (!interrupted) {
            cancelAble = false;
            this.afterAction();
        }
        cancelAble = true;
        engine.removeTimeAction(this);
    }
    
    /**
     * @return True if an interrupt still makes sense.
     */
    public boolean isCancelAble() {
        return cancelAble;
    }
    
    /**
     * Implement the action in this method.
     */
    abstract public void doAction(); 

    /**
     * Implement the things, that shall be done after the action.
     */
    abstract public void afterAction(); 

}
