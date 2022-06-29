package com.tjger.gui;

import java.util.Timer;

import com.tjger.MainFrame;
import com.tjger.MainFrame.ZoomType;
import com.tjger.MainMenu;
import com.tjger.MainPanel;
import com.tjger.game.GameRules;
import com.tjger.game.GameState;
import com.tjger.game.completed.GameEngine;
import com.tjger.lib.PlayerUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import at.hagru.hgbase.android.awt.Rectangle;
import at.hagru.hgbase.android.view.OnClickAdapter;
import at.hagru.hgbase.android.view.OnTouchZoomProvider;
import at.hagru.hgbase.android.view.ZoomListener;
import at.hagru.hgbase.gui.UpdateUiTimerTask;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The panel with the game field that is thought to be the one where the game is played on.
 *
 * @author hagru
 */
public class GamePanel extends SimpleGamePanel implements ZoomListener {

    private static final long DOUBLE_CLICK_DELTA = 350;
    private static final int WIPE_MINIMUM_MOVE = 50;
    private static final int WIPE_DIRECTION_FACTOR = 3;    

	private OnSharedPreferenceChangeListener prefChangeListener;
    private Point mousePoint;
    private Point mouseStartPoint;
    private MotionEvent lastMotionEvent;
    private boolean wasLongClick;
    private long lastClickTime = 0;
    private OnTouchZoomProvider zoomProvider;
    private Timer repaintTimer;
    private double buttonZoom = 0;

    public GamePanel(Activity activity) {
        super(activity);
        this.mousePoint = new Point(0, 0);
        this.mouseStartPoint = new Point(0, 0);
        this.setLongClickable(false);
        this.setOnClickListener(new OnClickAdapter());
        this.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				wasLongClick = true;
				mouseLongClicked(lastMotionEvent);				
				return false;
			}
		});
        if (getMainFrame().getZoomType() != MainFrame.ZoomType.ZOOM_FIT_ONLY) {
	        this.zoomProvider = new OnTouchZoomProvider(getGameConfig().getMinZoom(), getGameConfig().getMaxZoom(), this);
	        this.setOnTouchListener(zoomProvider);
        }
        prefChangeListener = new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				repaint();
			}
        };
        HGBaseConfig.getPreferences().registerOnSharedPreferenceChangeListener(prefChangeListener);
    }
    
	/**
	 * Adds the already created action button to the main panel (using <code>getMainPanel().addButton(...)</code>.
	 * The buttons can be created by the constructor. It is also necessary to implement {@link #removeActionButtons()}.
	 * 
	 * @see SchnapsenGamePanel#removeActionButtons()
	 */
	protected void addActionButtons() {
		//NOCHECK: to be overridden by sub classes
	}
	
	/**
	 * Remove the action buttons from its parent (e.g. using <code>HGBaseGuiTools.removeViewFromParent(...)</code>.
	 * 
	 * @see #removeActionButtons()
	 */
	protected void removeActionButtons() {
		//NOCHECK: to be overridden by sub classes
	}
	
    @Override
    protected void onAttachedToWindow() {
    	super.onAttachedToWindow();
		buttonZoom = getZoomFactor();
    	removeActionButtons();
        addActionButtons();
    }	
    
    @Override
    protected void paintParts(Canvas g) {
		if (!HGBaseTools.isEqual(buttonZoom, getZoomFactor())) {
			buttonZoom = getZoomFactor();
			removeActionButtons();
			addActionButtons();
		}
    	super.paintParts(g);
    }
    
    /**
     * The screen was clicked. To be overridden by sub classes.
     * 
	 * @param event the last motion event
	 */
	protected void mouseClicked(MotionEvent event) {
		//NOCHECK: to be overridden by sub classes
	}

    /**
     * The screen was double clicked. To be overridden by sub classes.<p>
     * NOTE: The first click of the double click will also call {@link #mouseClicked(MotionEvent)}-
     * 
	 * @param event the last motion event
	 */
	protected void mouseDoubleClicked(MotionEvent event) {
		//NOCHECK: to be overridden by sub classes
	}

    /**
     * The screen was long clicked. To be overridden by sub classes.
     * 
	 * @param event the last motion event
	 */
	protected void mouseLongClicked(MotionEvent event) {
		//NOCHECK: to be overridden by sub classes
	}

    /**
     * The screen was touched (can be a click). To be overridden by sub classes.
     * 
	 * @param event the last motion event
	 */
	protected void mousePressed(MotionEvent event) {
		//NOCHECK: to be overridden by sub classes
	}

    /**
     * The touch was released (and not clicked). To be overridden by sub classes.<p>
     * 
	 * @param event the last motion event
     * @param mouseStartPoint the starting point of the touch move
     * @param direction the mouse (touch) direction
	 */
	protected void mouseReleased(MotionEvent event, Point mouseStartPoint, MouseDirection direction) {
		//NOCHECK: to be overridden by sub classes
	}
	
    /**
     * The touch was moved. To be overridden by sub classes.<p>
     * NOTE: Touch movement is also used by moving a game panel with zoom.
     * 
	 * @param event the last motion event
	 */
	protected void mouseMoved(MotionEvent event) {
		//NOCHECK: to be overridden by sub classes
	}

    @Override
    public boolean performClick() {
    	if (wasLongClick) {
    		wasLongClick = false;
    	} else {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_DELTA) {
            	mouseDoubleClicked(lastMotionEvent);
            } else {
        		mouseClicked(lastMotionEvent);
            }
            lastClickTime = clickTime;    		
    	}
    	return super.performClick();
    }
    
	@SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	lastMotionEvent = event;
		mousePoint = new Point(invertTransform(event.getX()), invertTransform(event.getY()));
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				MouseDirection direction = checkMouseDirection(mouseStartPoint, mousePoint);
				mouseReleased(lastMotionEvent, mouseStartPoint, direction);
				break;
			case MotionEvent.ACTION_DOWN:
		    	mouseStartPoint = new Point(invertTransform(lastMotionEvent.getX()), invertTransform(lastMotionEvent.getY()));
				mousePressed(lastMotionEvent);
				break;
			case MotionEvent.ACTION_MOVE:
				//scrollPanelByMouseMove();
				mouseMoved(lastMotionEvent);
				break;
		}
    	return super.onTouchEvent(event);
    }

	/**
	 * Calculate the mouse (touch) direction of the movement (from pressing to releasing).
	 *  
	 * @param start the starting point
	 * @param target the target point
	 * @return the kind of direction or {@link MouseDirection#NONE} if no direction can be calculated
	 */
	private MouseDirection checkMouseDirection(Point start, Point target) {
		int diffX = start.x - target.x;
		int diffY = start.y - target.y;
		if (Math.abs(diffX) >= WIPE_MINIMUM_MOVE) {
			if (Math.abs(diffY) >= WIPE_MINIMUM_MOVE) {
				if (diffX > 0) {
					return (diffY > 0) ? MouseDirection.DIAGONAL_BOTTOMRIGHT_TO_TOPLEFT : MouseDirection.DIAGONAL_TOPRIGHT_TO_BOTTOMLEFT;
				} else {
					return (diffY > 0) ? MouseDirection.DIAGONAL_BOTTOMLEFT_TO_TOPRIGHT : MouseDirection.DIAGONAL_TOPLEFT_TO_BOTTOMRIGHT;
				}
			} else if (Math.abs(diffY) > 0 && Math.abs(diffX) / Math.abs(diffY) > WIPE_DIRECTION_FACTOR) {
				return (diffX > 0) ? MouseDirection.RIGHT_TO_LEFT : MouseDirection.LEFT_TO_RIGHT;
			}
		} else if (Math.abs(diffY) >= WIPE_MINIMUM_MOVE && Math.abs(diffX) > 0 && Math.abs(diffY) / Math.abs(diffX) > WIPE_DIRECTION_FACTOR) {
			return (diffY > 0) ? MouseDirection.UPWARDS : MouseDirection.DOWNWARDS;
		}
		return MouseDirection.NONE;
	}

	/**
	 * Scrolls the panel just by mouse move in {@link ZoomType#ZOOM_SCROLL_ANY} mode.<p>
	 * NOTE: Not called by default, has to be called by the {@link #mouseMoved(MotionEvent)} method.
	 * 
	 * @return true if the panel was moved, otherwise false
	 */
	protected boolean scrollPanelByMouseMove() {
		if (isScrollMode() && getMainFrame().getZoomType() == MainFrame.ZoomType.ZOOM_SCROLL_ANY) {
			if (movePanel(mouseStartPoint.x - mousePoint.x, mouseStartPoint.y - mousePoint.y)) {
				mouseStartPoint = mousePoint;
				return true;
			}
		}
		return false;
	}
	
    /**
     * @return the main activity of the game
     */
    public MainFrame getMainFrame() {
        return getGameManager().getMainFrame();
    }

    /**
     * @return the main menu of the game
     */
    public MainMenu getMainMenu() {
        return getMainFrame().getMainMenu();
    }
    
    /**
     * @return the main panel that surounds the game panel
     */
    public MainPanel getMainPanel() {
    	return getMainFrame().getMainPanel();
    }

    /**
     * @return The game engine.
     */
    public GameEngine getGameEngine() {
        return getGameManager().getGameEngine();
    }

    /**
     * @return The game state.
     */
    public GameState getGameState() {
        return getGameManager().getGameState();
    }

    /**
     * @return The game rules.
     */
    public GameRules getGameRules() {
        return getGameManager().getGameRules();
    }
    
    /**
     * @return true if scrolling is possible at the moment, i.e., the game panel is larger than the screen
     */
    public boolean isScrollMode() {
    	return getMainMenu().getZoomMenu().isScrollingPossible();
    }
    
    /**
     * @return true if the user is currently zooming, otherwise false
     */
    public boolean isZooming() {
    	return (zoomProvider != null && zoomProvider.isZooming());
    }

    @Override
	public double getZoomFactor() {
		if (getMainMenu() != null) {
			return getMainMenu().getZoom() / 100.0;
		} else {
			return super.getZoomFactor();
		}
    }
    
    @Override
    public void performZoom(View v, int zoom, float scaleDiff) {
        getMainMenu().setZoom(zoom);
    	checkFieldBoundaries();
    }
    
    /**
	 * Checks if the field gets out of the screen (can only happen top-left) and moves the panel accordingly.
	 */
	protected void checkFieldBoundaries() {
		if (getMainFrame().getZoomType() == MainFrame.ZoomType.ZOOM_SCROLL_ANY) {
			Rectangle rect = getViewport();
			int maxW = transform(getFieldWidth());
			int maxH = transform(getFieldHeight());
			int x = (int) rect.getX();
			int y = (int) rect.getX();
			int diffX = 0;
			int diffY = 0;
			if (maxW - x < rect.width) {
				diffX = x * -1;
			}
			if (maxH - y < rect.height) {
				diffY = y * -1;
			}
			if (diffX != 0 || diffY != 0) {
				movePanel(invertTransform(diffX), invertTransform(diffY));
			}
		}
	}

	/**
     * @return the zoom provider of the game panel that handles zoom changes by touch gestures, may be null
     */
    public OnTouchZoomProvider getZoomProvider() {
        return zoomProvider;
    }

    /**
     * Returns the mouse x-position considering the zoom.
     * If the mouse is not positioned in the panel, the last x-position is returned.
     *
     * @return The mouse x-position on the game field.
     */
    public int getMouseX() {
        return mousePoint.x;
    }

    /**
     * Returns the mouse y-position considering the zoom.
     * If the mouse is not positioned in the panel, the last y-position is returned.
     *
     * @return The mouse y-position on the game field.
     */
    public int getMouseY() {
        return mousePoint.y;
    }
    
    /**
     * @return The mouse start x-position on the game field.
     */
    public int getMouseStartX() {
        return mouseStartPoint.x;
    }

    /**
     * @return The mouse start y-position on the game field.
     */
    public int getMouseStartY() {
        return mouseStartPoint.y;
    }
    
    /**
     * @return The game panel's viewport or null.
     */
    protected Rectangle getViewport() {
		MainPanel mainPanel = getMainFrame().getMainPanel();
		if (mainPanel != null) {
			return mainPanel.getViewport();
		} else {
			return null;
		}
    }
    
    /**
     * @return The game panel's surrounding scroll view or null.
     */
    protected ViewGroup getScrollView() {
        MainPanel mainPanel = getMainFrame().getMainPanel();
        if (mainPanel!=null) {
            return mainPanel.getScrollView();
        } else {
        	return null;
        }
    }

    /**
     * Moves the panel in the given directions
     *
     * @param moveX horizontal movement
     * @param moveY vertical movement
     * @return true if panel was moved
     */
    public boolean movePanel(int moveX, int moveY) {
    	if (moveX != 0 || moveY != 0) {
	    	if (getMainFrame().getZoomType() == MainFrame.ZoomType.ZOOM_SCROLL_VH) {
	    		ScrollView scroll = (ScrollView) getScrollView();
	    		scroll.scrollBy(0, transform(moveY));
	    		HorizontalScrollView scrollH = (HorizontalScrollView) scroll.getParent();
	    		scrollH.scrollBy(transform(moveX), 0);
	    		return true;
	        } else {
				Rectangle rect = getViewport();
				int maxW = transform(getFieldWidth());
				int maxH = transform(getFieldHeight());
				int w = Math.min(rect.width, maxW);
				int h = Math.min(rect.height, maxH);
				int x = (int) rect.getX() + transform(moveX) * -1;
				int y = (int) rect.getY() + transform(moveY) * -1;
				x = Math.max(x, w - maxW);
				x = Math.min(x, 0);
				y = Math.max(y, h - maxH);
				y = Math.min(y, 0);
				if (x != (int) rect.getX() * -1 || y != (int) rect.getY() * -1) {
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(getLayoutParams());
					lp.leftMargin = x;
					lp.topMargin = y;
					setLayoutParams(lp);
					return true;
				}
	    	}
    	}
		return false;
    }    
    
    /**
     * Centers the panel to the given x-/y-position
     *
     * @param x x-position to center
     * @param y y-position to center
     */
	public void centerXY(int x, int y) {
		Point center = getCurrentCenter();
		if (center != null) {
			movePanel(x - center.x, y - center.y);
		}
	}   
    
    /**
     * Centers the panel.
     */
    public void centerPanel() {
        Point center = getCenterPos();
        centerXY(center.x, center.y);
    }

    /**
     * Returns the current center of the panel independent from the zoom factor.
     *
     * @return The center or null
     */
    public Point getCurrentCenter() {
        Rectangle rect = getViewport();
        if (rect != null) {
			int midX = invertTransform((rect.getX() + (rect.getWidth() / 2)));
			int midY = invertTransform((rect.getY() + (rect.getHeight() / 2)));
	        return new Point(midX, midY);
        } else {
        	return null;
        }
    }

    /**
     * Returns true if there is an active game and the player who has to play is
     * a human one.
     *
     * @return True, if the human player does his move.
     */
    protected boolean isHumanPlayer() {
        return PlayerUtil.isHumanPlaying(getGameEngine());
    }      
    
    /**
     * Set a timer that repaints the panel.
     * If milliSeconds are set to 0 or lower, a possible existing timer will be removed.
     * Activating the timer can make troubles if automatic animation support is activated.
     *
     * @param milliSeconds The number of milliseconds to repaint the panel.
     */
    public void setRepaintTimer(int milliSeconds) {
        if (repaintTimer!=null) {
            repaintTimer.cancel();
        }
        if (milliSeconds>0) {
            repaintTimer = new Timer();
            repaintTimer.schedule(new RepaintActionListener(), 0, milliSeconds);
        }
    } 
    
    /**
     * Performs a repaint if action is invoked.
     */
    class RepaintActionListener extends UpdateUiTimerTask {
    
        public RepaintActionListener() {
            super(getMainFrame(), new Runnable() {
                
                @Override
                public void run() {
                    invalidate();
                }
            });
        }
    }

    /**
     * Returns the width of the specified string.
     *
     * @param string The string to be measured.
     * @return The width of the specified string.
     */
    protected int getStringWidth(String string) {
        if (!HGBaseTools.hasContent(string)) {
            return 0;
        }
        Rect bounds = new Rect();
        getCurrentPaint().getTextBounds(string, 0, string.length(), bounds);
        return bounds.right - bounds.left;
    }
}
