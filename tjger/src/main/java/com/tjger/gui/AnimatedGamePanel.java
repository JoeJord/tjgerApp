package com.tjger.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.tjger.gui.completed.Part;
import com.tjger.lib.PartUtil;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Point;

/**
 * A game panel that allows automatic animation of parts.
 * 
 * @author hagru
 */
public class AnimatedGamePanel extends GamePanel {
    
    private static final int ANIMATION_STEPS = 5;
    private static final int ANIMATION_DURATION = 50;
    private final boolean automaticAnimation;

    // stores the last and current parts for animation
    private Map<Part,List<DrawData>> currentPartPositions = new LinkedHashMap<>();
    private Map<Part,List<DrawData>> lastPartPositions = new LinkedHashMap<>();
    private Map<Part,List<DrawData>> animationCanditates = new LinkedHashMap<>();    
    
    /**
     * @param activity the activity
     */
    public AnimatedGamePanel(Activity activity) {
        this(activity, true);
    }

    /**
     * @param activity the activity
     * @param automaticAnimation true to switch on automatic animation (the default for this panel)
     */
    public AnimatedGamePanel(Activity activity, boolean automaticAnimation) {
        super(activity);
        this.automaticAnimation = automaticAnimation;
    }

    /**
     * Paint all parts on the panel. Calls methods to paint background, board and parts.
     * 
     * @param g the canvas object
     */
    @Override
    protected void paintComponent(Canvas g) {
        initAnimationData();
        super.paintComponent(g);
        paintAnimation(g);        
    }
    
    /**
     * For each time the panel is drawn the data structures for the automatic animation support
     * have to be initialized.
     */
    private void initAnimationData() {
        if (automaticAnimation) {
            lastPartPositions.clear();
            lastPartPositions.putAll(currentPartPositions);
            animationCanditates.clear();
            currentPartPositions.clear();
        }
    }    
    
    /**
     * Try to animate the parts as good as possible.
     * The method is private as it is not thought to be overwritten.
     *
     * @param g The canvas object.
     */
    private void paintAnimation(Canvas g) {
        if (automaticAnimation) {
            // variable was initially false;
            // changed to true because of overlapping problems, but then there is always repaint
            boolean requireRepaint = animationCanditates.size() > 0;

            // go through all candidates and look if there are still parts left in the lastPartPositions map
            for (Map.Entry<Part,List<DrawData>> candiateEntry : animationCanditates.entrySet()) {
                Part part = candiateEntry.getKey();
                List<DrawData> candidatesPositions = candiateEntry.getValue();
                List<DrawData> leftPositions = getList(lastPartPositions, part);
                int numCandidates = candidatesPositions.size();
                if (numCandidates==leftPositions.size()) {
                    // the same number of positions are left, so animate each position
                    requireRepaint = (numCandidates>0);
                    for (int i=0; i<numCandidates; i++) {
                        DrawData target = candidatesPositions.get(i);
                        DrawData current = leftPositions.get(i);
                        drawPartAnimated(part, current, target, g);
                    }
                } else {
                    // do not draw any animation, just draw the left positions
                    // (by this it can happen that wrong parts are overlapped)
                    for (DrawData dd : candidatesPositions) {
                        drawPart(dd, part, g);
                    }
                }
            }
            // start a thread if there is at least one animation
            repaintForAnimation(requireRepaint);
        }
    }   

    /**
     * Draw the part as animation.
     *
     * @param part The part to draw.
     * @param current The current position.
     * @param target The target position.
     * @param g The canvas object.
     */
    private void drawPartAnimated(Part part, DrawData current, DrawData target, Canvas g) {
        if (current.animationStep>=getAnimationSteps()) {
            // animation is over, paint at target position
            DrawData dd = new DrawData(target.x, target.y, target.percentSize, target.angle);
            drawPart(dd, part, g);
        } else {
            // animate for the next step
            DrawData dd = calculateAnimation(current, target);
            drawPart(dd, part, g);
        }
    }    

    /**
     * Calculate the draw data for animation from current to target.
     *
     * @param current The current position (can be starting point or meanwhile an animation).
     * @param target The target position, size and angle.
     * @return The draw date for the next step
     */
    private DrawData calculateAnimation(DrawData current, DrawData target) {
        int remainSteps = getAnimationSteps() - current.animationStep;
        int x = current.x + (target.x-current.x)/remainSteps;
        int y = current.y + (target.y-current.y)/remainSteps;
        int percentSize = current.percentSize + (target.percentSize-current.percentSize)/remainSteps;
        double angle = (current.angle + (target.angle-current.angle)/remainSteps)%360.0;
        int animationStep = current.animationStep + 1;
        return new DrawData(x, y, percentSize, angle, animationStep);
    }
    
    /**
     * @param repaint True to repaint, false if there was no animation
     */
    protected void repaintForAnimation(boolean repaint) {
        if (repaint) {
            int countdown = getAnimationDuration() / getAnimationSteps();
            Timer t = new Timer();
            t.schedule(new RepaintActionListener(), countdown);
        }
    }    
    

    @Override
    public void drawPart(int x, int y, int percentSize, double angle, Align hAlignment, int vAlignment,
	    Part part, Canvas g) {
	Point drawingPosition = PartUtil.getDrawingPosition(x, y, percentSize, hAlignment, vAlignment, part);
        if (part!=null) {
	    if (automaticAnimation && shallAnimatePart(part, drawingPosition.x, drawingPosition.y)) {
                List<DrawData> lastPoints = getList(lastPartPositions, part);
		DrawData currData = new DrawData(drawingPosition.x, drawingPosition.y, percentSize, angle);
                if (lastPoints.contains(currData)) {
                    lastPoints.remove(currData);
                } else {
                    if (!lastPoints.isEmpty()) {
                        getList(animationCanditates, part).add(currData);
                        currData = null;
                    }
                }
                drawPart(currData, part, g);
            } else {
		super.drawPart(x, y, percentSize, angle, hAlignment, vAlignment, part, g);
            }
        }
    }
    

    /**
     * Check whether a particular part shall be animated.<p>
     * Intended to be used by sub-classes to prevent some parts of automatic animation.
     *
     * @param part The part to be animated (or not).
     * @param x The x coordinate the part shall be placed.
     * @param y The y coordinate the part shall be placed.
     * @return True if the part shall be animated.
     */
    protected boolean shallAnimatePart(Part part, int x, int y) {
        return true;
    }       
    
    /**
     * Draws a part using the draw data object.
     *
     * @param dd Contains position, size and angle.
     * @param part The part to paint.
     * @param g The canvas object.
     */
    private void drawPart(DrawData dd, Part part, Canvas g) {
        if (dd!=null) {
            checkShadowForPart(part);
            checkReflectionForPart(part);
            drawImage(dd.x, dd.y, dd.percentSize, dd.angle, part.getImage(), g);
            getList(currentPartPositions, part).add(dd);
        }
    } 
    
    /**
     * Get a list with draw data for the given part, if list does not exist yet,
     * it will be created.
     *
     * @param map The map that stores the list.
     * @param part The part to get the list for.
     * @return A list with draw data, can be empty.
     */
    private List<DrawData> getList(Map<Part,List<DrawData>> map, Part part) {
        List<DrawData> list = map.get(part);
        if (list==null) {
            list = new ArrayList<>();
            map.put(part, list);
        }
        return list;
    }    
    
    /**
     * @return The number of animation steps that shall be drawn for automatic animation support.
     */
    protected int getAnimationSteps() {
        return ANIMATION_STEPS;
    }

    /**
     * @return The milliseconds each animation shall take.
     */
    protected int getAnimationDuration() {
        return ANIMATION_DURATION;
    }    

    /**
     * Stores the data for drawing an animation for an element.
     */
    private static final class DrawData {

        public final int x;
        public final int y;
        public final int percentSize;
        public final double angle;
        public final int animationStep;

        public DrawData(int x, int y, int percentSize, double angle) {
            this(x, y, percentSize, angle, 1);
        }

        public DrawData(int x, int y, int percentSize, double angle, int animationStep) {
            super();
            this.x = x;
            this.y = y;
            this.percentSize = percentSize;
            this.angle = angle;
            this.animationStep = animationStep;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DrawData) {
                DrawData dd = (DrawData)o;
                return (this.x==dd.x && this.y==dd.y && this.percentSize==dd.percentSize
                        && this.angle==dd.angle && this.animationStep==dd.animationStep);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return new Point(x,y).hashCode() + 11*percentSize + (int)(11011*angle) + animationStep;
        }

        @Override
        public String toString() {
            return "x="+x+", y="+y+", step="+animationStep+", percentSize="+percentSize+", angle="+angle;
        }
    }
    
}
