package com.tjger;

import android.app.Fragment;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.tjger.MainMenu.StatePanelMenu.StatePanelPosition;
import com.tjger.game.GameStateAdapter;
import com.tjger.game.GameStateListener;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameEngine;
import com.tjger.gui.GamePanel;
import com.tjger.gui.completed.PlayerStatePanel;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.awt.Rectangle;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.HGBaseStatusBar;
import at.hagru.hgbase.gui.UpdateUiTimerTask;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The main panel of the game.<p>
 * Contains a GamePanel and optionally a PlayerInfoPanel.
 *
 * @author hagru
 */
public class MainPanel extends Fragment {

    public static final int ZOOM_DELAY_TIME = 250;

    /**
     * Possible positions for the next game button.
     */
    public enum ButtonPosition {
        BOTTOM,        /* place the button at the bottom of the screen with full width (this is the default). */
        TOP,           /* place the button at the top of the screen with full width. */
        CENTER,        /* place the button at the center of the screen with full width in portrait and half width in landscape mode. */
        TOP_LEFT,       /* place the button at the top left of the screen with half width. */
        TOP_CENTER,    /* place the button at the top center of the screen with half width. */
        TOP_RIGHT,     /* place the button at the top right of the screen with half width. */
        BOTTOM_LEFT,   /* place the button at the bottom left of the screen with half width. */
        BOTTOM_CENTER, /* place the button at the bottom center of the screen with half width. */
        BOTTOM_RIGHT   /* place the button at the bottom right of the screen with half width. */
    }

    private FrameLayout rootFrameLayout;
    private RelativeLayout buttonLayout;
    private LinearLayout rootLayout;
    private Button nextGameButton;
    private ViewGroup scrollPane;
    private GamePanel gamePanel;
    private PlayerStatePanel statePanel;
    private GameStateListener stateListener;

    public MainPanel() {
        super();
        this.statePanel = createStatePanel();
        OnSharedPreferenceChangeListener prefChangeListener = (sharedPreferences, key) -> {
            if (key.equals(ConstantValue.CONFIG_STATEPANEL)) {
                repositionStatePanel();
                getMainFrame().getMainMenu().setZoomFitToWindow();
            }
        };
        HGBaseConfig.getPreferences().registerOnSharedPreferenceChangeListener(prefChangeListener);
    }

    /**
     * @return the main frame
     */
    protected MainFrame getMainFrame() {
        return MainFrame.getInstance();
    }

    /**
     * @return the root layout.
     */
    protected LinearLayout getRootLayout() {
        return rootLayout;
    }

    /**
     * @return the next game button.
     */
    protected Button getNextGameButton() {
        return nextGameButton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        repositionStatePanel();
        setZoomFitWindowDelayed(false);
    }

    /**
     * Calls the method to fit the zoom that the game panel fits into the current window after a certain period of time.
     *
     * @param onCreate true if this method is called during creation, false if just configuration was changed
     * @see #ZOOM_DELAY_TIME
     */
    protected void setZoomFitWindowDelayed(final boolean onCreate) {
        if (scrollPane != null && gamePanel != null) {
            getMainFrame().setCursorWait();
            HGBaseTools.delay(ZOOM_DELAY_TIME, new UpdateUiTimerTask(getMainFrame(), () -> {
                getMainFrame().getMainMenu().setZoomFitToWindow();
                getMainFrame().setCursorDefault();
                if (onCreate) {
                    getMainFrame().onGamePanelCreated();
                }
            }));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootLayout != null) {
            rootLayout.removeAllViews();
            if (scrollPane != null && gamePanel != null) {
                scrollPane.removeView(gamePanel);
                scrollPane = null;
            }
        }
        rootFrameLayout = new FrameLayout(getMainFrame());
        buttonLayout = new RelativeLayout(getMainFrame());
        rootLayout = HGBaseGuiTools.createLinearLayout(getMainFrame(), false);
        rootLayout.setLayoutParams(HGBaseGuiTools.createLinearLayoutParams(true, true));
        nextGameButton = createNextGameButton();
        scrollPane = createAndAddScrollView(rootLayout);
        rootLayout.setBackgroundColor(Color.BLACK);
        // add the game finished listener after all required view are created
        GameEngine engine = getMainFrame().getGameManager().getGameEngine();
        if (stateListener != null) {
            engine.removeGameStateListener(stateListener);
        }
        stateListener = new GameFinishedListener();
        engine.addGameStateListener(stateListener);
        // show the game panel
        showGamePanel();
        rootFrameLayout.addView(rootLayout);
        rootFrameLayout.addView(buttonLayout);
        return rootFrameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainFrame().onPostActivityChanged();
    }

    /**
     * @return true if there shall be a vertical and horizontal scroll view, false for a relative layout view
     */
    private boolean isScrollVH() {
        return (getMainFrame().getZoomType() == MainFrame.ZoomType.ZOOM_SCROLL_VH);
    }

    /**
     * Create the scroll view and adds it to the parent view.
     *
     * @param parent the parent view to add the new created scroll view
     * @return the scroll view that will hold the game panel
     */
    protected ViewGroup createAndAddScrollView(ViewGroup parent) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1.0f;
        ViewGroup scroll;
        if (isScrollVH()) {
            HorizontalScrollView scrollH = new HorizontalScrollView(getMainFrame());
            scrollH.setMinimumWidth((int) (HGBaseGuiTools.getScreenSize(getMainFrame()).x * 2 / 3.0));
            scrollH.setFillViewport(true);
            scrollH.setScrollbarFadingEnabled(true);
            scrollH.requestDisallowInterceptTouchEvent(true);
            parent.addView(scrollH, lp);
            scroll = new ScrollView(getMainFrame());
            scroll.setScrollbarFadingEnabled(true);
            scroll.requestDisallowInterceptTouchEvent(true);
            ((ScrollView) scroll).setFillViewport(true);
            scrollH.addView(scroll, HGBaseGuiTools.createViewGroupLayoutParams(true, true));
        } else {
            scroll = new RelativeLayout(getMainFrame());
            scroll.setMinimumWidth((int) (HGBaseGuiTools.getScreenSize(getMainFrame()).x * 2 / 3.0));
            parent.addView(scroll, lp);
        }
        return scroll;
    }

    /**
     * Use this method to create the player state panel or return null if no panel shall be shown
     *
     * @return the player state panel or null
     */
    protected PlayerStatePanel createStatePanel() {
        return null;
    }

    /**
     * Refreshes the panel.
     * Should be called, if the size of the game field has changed (e.g. other board).
     */
    public void refresh() {
        setGamePanelSize();
        gamePanel.invalidate();
        scrollPane.invalidate();
    }

    /**
     * @return The GamePanel object.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Sets the game panel into a scrolling pane.
     */
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        showGamePanel();
    }

    /**
     * Shows the panel in the view if the view is available.
     */
    private void showGamePanel() {
        if (gamePanel != null && scrollPane != null) {
            scrollPane.removeAllViews();
            scrollPane.addView(gamePanel, HGBaseGuiTools.createViewGroupLayoutParams(true, true));
            repositionStatePanel();
            setZoomFitWindowDelayed(true);
        }
    }

    /**
     * @return the player state panel, may be null
     */
    public View getStatePanel() {
        return statePanel;
    }

    /**
     * Set the state panel manually or override {@link #createStatePanel()}.
     *
     * @param statePanel the state panel to set, or null to have no state panel
     * @see PlayerStatePanel
     * @see #createStatePanel
     */
    public void setStatePanel(PlayerStatePanel statePanel) {
        this.statePanel = statePanel;
    }

    /**
     * @return the scroll view that holds the game panel
     */
    public ViewGroup getScrollView() {
        return scrollPane;
    }

    /**
     * @return The scroll panels viewport
     */
    public Rectangle getViewport() {
        if (scrollPane == null || gamePanel == null || gamePanel.getLayoutParams() == null) {
            return null;
        } else {
            if (isScrollVH()) {
                HorizontalScrollView scrollH = (HorizontalScrollView) scrollPane.getParent();
                Dimension scrollSize = getScrollPanelSize();
                return new Rectangle(scrollH.getScrollX(), scrollPane.getScrollY(), scrollSize.width, scrollSize.height);
            } else {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) gamePanel.getLayoutParams();
                return new Rectangle(lp.leftMargin * -1, lp.topMargin * -1, scrollPane.getWidth(), scrollPane.getHeight());
            }
        }
    }

    /**
     * @return the visible size of the scroll panel
     */
    protected Dimension getScrollPanelSize() {
        if (isScrollVH()) {
            HorizontalScrollView scrollH = (HorizontalScrollView) scrollPane.getParent();
            int width = (int) (scrollH.getMeasuredWidth() * scrollH.getScaleX());
            int height = (int) (scrollPane.getMeasuredHeight() * scrollPane.getScaleY());
            return new Dimension(width, height);
        } else {
            return HGBaseGuiTools.getViewSize(scrollPane);
        }
    }

    /**
     * Set's the size of the game panel.
     */
    private void setGamePanelSize() {
        if (gamePanel != null) {
            GameConfig config = GameConfig.getInstance();
            Dimension scrollSize = getScrollPanelSize();
            int w = Math.max(gamePanel.transform(config.getFieldWidth()), scrollSize.width);
            int h = Math.max(gamePanel.transform(config.getFieldHeight()), scrollSize.height);
            Dimension old = HGBaseGuiTools.getViewSize(gamePanel);
            if (old.getWidth() > 0 && w != old.getWidth() || old.getHeight() > 0 && h != old.getHeight()) {
                HGBaseGuiTools.setViewSize(gamePanel, new Dimension(w, h));
            }
        }
    }

    /**
     * Tries to reposition the state panel if there is one available.
     */
    void repositionStatePanel() {
        if (statePanel != null && rootLayout != null) {
            String newPosition = MainMenu.STATEPANEL_MENU.getValue();
            setNewStatePanelPosition(newPosition);
        }
    }

    /**
     * @param position the new position for the state panel
     */
    private void setNewStatePanelPosition(String position) {
        rootLayout.removeView(statePanel);
        if (StatePanelPosition.STATEPANEL_AUTO.name().equals(position)) {
            position = HGBaseGuiTools.isScreenPortrait(getMainFrame()) ? StatePanelPosition.STATEPANEL_BOTTOM.name() : StatePanelPosition.STATEPANEL_RIGHT.name();
        }
        if (!StatePanelPosition.STATEPANEL_NONE.name().equals(position)) {
            int index = (StatePanelPosition.STATEPANEL_BOTTOM.name().equals(position) || StatePanelPosition.STATEPANEL_RIGHT.name().equals(position)) ? 1 : 0;
            int layout = (StatePanelPosition.STATEPANEL_BOTTOM.name().equals(position) || StatePanelPosition.STATEPANEL_TOP.name().equals(position)) ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statePanel.getPanelHeight());
            if (layout == LinearLayout.HORIZONTAL) {
                lp.weight = 2.0f;
            }
            statePanel.setLayoutParams(lp);
            statePanel.setMinimumHeight(statePanel.getPanelHeight());
            rootLayout.addView(statePanel, index);
            rootLayout.setOrientation(layout);
        }
        rootLayout.requestLayout();
    }

    /**
     * Creates the button that allows to start the next game from the main panel.<p>
     * The button will be added with the game listener as soon as the game is finished.
     *
     * @return the new created next game button
     * @see #addNextGameButton()
     */
    protected Button createNextGameButton() {
        Button nextGame = HGBaseGuiTools.createButton(getMainFrame(), HGBaseText.getText("game_next"), view -> {
            removeNextGameButton();
            getMainFrame().onGameNext();
        });
        setNextGameButtonColors(nextGame);
        return nextGame;
    }

    /**
     * Sets the text color and the background color or image of the next game button.
     *
     * @param btNextGame the button for the next game
     */
    protected void setNextGameButtonColors(Button btNextGame) {
        MainMenu menu = getMainFrame().getMainMenu();
        if (menu == null) {
            menu = new MainMenu();
        }
        menu.setButtonColors(btNextGame);
    }

    /**
     * Adds the next game button if the game has finished.<p>
     * Note: it is necessary to add the button to the <code>rootLayout</code>.
     *
     * @see #removeNextGameButton()
     */
    protected void addNextGameButton() {
        addNextGameButton(ButtonPosition.BOTTOM);
    }

    /**
     * Adds the next game button if the game has finished at the given position.
     *
     * @param pos the position to place the button
     */
    protected void addNextGameButton(ButtonPosition pos) {
        addNextGameButtonToRoot(nextGameButton, pos);
    }

    /**
     * Adds the next game button (or a view with this functionality) to the root layout.
     *
     * @param button the next game button or a view with this functionality
     * @param pos    the position to place the button
     */
    protected void addNextGameButtonToRoot(View button, ButtonPosition pos) {
        HGBaseStatusBar statusBar = getMainFrame().getStatusBar();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height, Gravity.BOTTOM);
        if (statusBar != null) {
            lp.bottomMargin = HGBaseGuiTools.getViewSize(statusBar).height;
        }
        if (pos != ButtonPosition.BOTTOM && pos != ButtonPosition.TOP && !(pos == ButtonPosition.CENTER && HGBaseGuiTools.isScreenPortrait(getMainFrame()))) {
            lp.width = HGBaseGuiTools.getScreenSize(getActivity()).x / 2;
        }
        if (pos == ButtonPosition.TOP || pos == ButtonPosition.TOP_LEFT || pos == ButtonPosition.TOP_CENTER || pos == ButtonPosition.TOP_RIGHT) {
            lp.gravity = Gravity.TOP;
        } else if (pos == ButtonPosition.CENTER) {
            lp.gravity = Gravity.CENTER;
        }
        if (pos == ButtonPosition.TOP_LEFT || pos == ButtonPosition.BOTTOM_LEFT) {
            lp.gravity = lp.gravity | Gravity.START;
        } else if (pos == ButtonPosition.TOP_RIGHT || pos == ButtonPosition.BOTTOM_RIGHT) {
            lp.gravity = lp.gravity | Gravity.END;
        }
        rootFrameLayout.addView(nextGameButton, lp);
    }

    /**
     * Removes the next game button from the <code>rootLayout</code>.
     */
    protected void removeNextGameButton() {
        removeNextGameButtonFromRoot(nextGameButton);
    }

    /**
     * Removes the next game button (or a view with this functionality) from the root layout.
     *
     * @param button the next game button or a view with this functionality
     */
    protected void removeNextGameButtonFromRoot(View button) {
        HGBaseGuiTools.removeViewFromParent(button);
    }

    /**
     * Adds a button to the root frame layout of the main panel.
     *
     * @param btView the button to add
     * @param lp     the layout parameters
     */
    public void addButton(Button btView, FrameLayout.LayoutParams lp) {
        HGBaseGuiTools.removeViewFromParent(btView);
        rootFrameLayout.addView(btView, lp);
    }

    /**
     * Adds a button to the relative layout of the main panel.
     *
     * @param btView the button to add
     * @param lp     the layout parameters
     */
    public void addButton(Button btView, RelativeLayout.LayoutParams lp) {
        HGBaseGuiTools.removeViewFromParent(btView);
        buttonLayout.addView(btView, lp);
    }

    /**
     * Adds a button to the relative layout of the main panel.
     *
     * @param btView the button to add
     * @param width  the width of the button
     * @param height the height of the button
     * @param x      the x position of the button
     * @param y      the y position of the button
     */
    public void addButton(Button btView, int width, int height, int x, int y) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.leftMargin = x;
        lp.topMargin = y;
        addButton(btView, lp);
    }

    /**
     * Listens if a game was finished and add the "next game" button to the root view.
     */
    private final class GameFinishedListener extends GameStateAdapter {

        @Override
        public void gameFinished(boolean normal) {
            if (normal && nextGameButton != null) {
                removeNextGameButton();
                addNextGameButton();
            }
        }
    }

}
