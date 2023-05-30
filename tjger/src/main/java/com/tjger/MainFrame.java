package com.tjger;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.PlayerManager;
import com.tjger.gui.GameDialogs;
import com.tjger.gui.GamePanel;
import com.tjger.gui.actions.ShowCreditsDlgAction;
import com.tjger.gui.actions.ShowGameHintsDlgAction;
import com.tjger.gui.actions.ShowGameInfoDlgAction;
import com.tjger.gui.actions.ShowGameInstructionsDlgAction;
import com.tjger.gui.actions.ShowPartsDlgAction;
import com.tjger.gui.actions.ShowTjgerDlgAction;
import com.tjger.gui.actions.SoundConfigurationAction;
import com.tjger.gui.actions.TjgerGameCloseAction;
import com.tjger.gui.actions.TjgerGameNewAction;
import com.tjger.gui.actions.TjgerGameResumeAction;
import com.tjger.gui.actions.TjgerGameSettingsAction;
import com.tjger.gui.internal.GameDialogFactory;
import com.tjger.gui.internal.TjgerWelcome;
import com.tjger.lib.ConstantValue;
import com.tjger.lib.GameConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import at.hagru.hgbase.HGBaseWelcomeActivity;
import at.hagru.hgbase.android.HGBaseAdvertisements;
import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.gui.HGBaseDialog;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.menu.IMenuAction;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseFileTools;
import at.hagru.hgbase.lib.HGBaseSound;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Main class for tjger.
 * <p>
 * New games have to subclass this class and call
 *
 * @author hagru
 */
public abstract class MainFrame extends HGBaseWelcomeActivity {

    public static final String AUTOSAVE_FILENAME = "autosave.xml";
    private static MainFrame instance; // hold the instance of this main frame
    private static MainMenu mainMenu; // needs to be static, otherwise it makes troubles with Android
    private static MainPanel mainPanel; // needs to be static, otherwise it makes troubles with Android
    private final ZoomType zoomType;
    private GameManager gameManager; // needs to be static, otherwise it makes troubles with Android
    private Fragment activeFragment;
    private MainStatusBar statusBar;
    private boolean isResumeGame; // is necessary because new/resume game has a part before and after creation of main/game panel
    private boolean isBlockMenu;

    public MainFrame(ZoomType zoomType) {
        this(HGBaseTools.INVALID_INT, zoomType);
    }

    public MainFrame(int optionsMenuId) {
        this(optionsMenuId, ZoomType.ZOOM_FIT_ONLY);
    }

    public MainFrame(int optionsMenuId, ZoomType zoomType) {
        super(R.layout.activity_main_frame, optionsMenuId);
        this.zoomType = zoomType;
    }

    /**
     * @return the current instance of the main frame
     */
    public static MainFrame getInstance() {
        return instance;
    }

    /**
     * @return the game manager for this game.
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }

    /**
     * @return the current zoom type.
     */
    public ZoomType getZoomType() {
        return zoomType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainFrame.instance = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateDuringWelcome() {
        gameManager = GameManager.createInstance(this);
        if (gameManager.getGameConfig().hasErrors()) {
            throw new GameConfigurationException();
        }
    }

    @Override
    protected void onDestroy() {
        MainFrame.instance = null;
        super.onDestroy();
    }

    @Override
    protected Dialog getWelcomeDialog() {
        return TjgerWelcome.createDialog(this);
    }

    @Override
    protected void registerDefaultOptionsMenuActions() {
        super.registerDefaultOptionsMenuActions();
        registerAction(MainMenu.MENU_ID_GAME_CLOSE, new TjgerGameCloseAction(this));
        registerAction(MainMenu.MENU_ID_GAME_NEW, new TjgerGameNewAction(this));
        registerAction(MainMenu.MENU_ID_GAME_RESUME, new TjgerGameResumeAction(this));
        registerAction(MainMenu.MENU_ID_NEW_GAME_DLG, new TjgerGameSettingsAction(this));
        registerAction(MainMenu.MENU_ID_GAMEINFO, new ShowGameInfoDlgAction(this));
        registerAction(MainMenu.MENU_ID_HELP_TJGER, new ShowTjgerDlgAction(this));
        registerAction(MainMenu.MENU_ID_HELP_INSTRUCTIONS, new ShowGameInstructionsDlgAction(this));
        registerAction(MainMenu.MENU_ID_HELP_GAMEHINTS, new ShowGameHintsDlgAction(this));
        registerAction(MainMenu.MENU_ID_SETTINGS_PARTS, new ShowPartsDlgAction(this));
        registerAction(MainMenu.MENU_ID_SETTINGS_SOUND, new SoundConfigurationAction(this));
        registerAction(MainMenu.MENU_ID_CREDITS, new ShowCreditsDlgAction(this));
    }

    /**
     * Register the default menu items for the menu of the activity.
     *
     * @param menuId the menu id
     * @param action the action to call for the corresponding menu item
     */
    protected void registerAction(String menuId, IMenuAction action) {
        int resId = HGBaseResources.getResourceIdByName(menuId, HGBaseResources.ID);
        if (resId >= 0) {
            registerOptionsMenuAction(resId, action);
        }
    }

    /**
     * Sets the main menu, the main panel and the game panel.
     * <p>
     * Should be called by the method {@link #onCreatePostWelcome()}.
     *
     * @param mainMenu  the main menu to set, i.e., the starting view with basic
     *                  commands
     * @param mainPanel the main panel
     * @param gamePanel the game panel, will get part of the main panel
     * @param statusBar the optional status bar, may be null
     */
    protected final void setPanels(MainMenu mainMenu, MainPanel mainPanel, GamePanel gamePanel, MainStatusBar statusBar) {
        MainFrame.mainMenu = mainMenu;
        MainFrame.mainPanel = mainPanel;
        mainPanel.setGamePanel(gamePanel);
        this.statusBar = statusBar;
        activateFragment(mainMenu);
        blockMenuActions(true);
        showHintsDialog(ConstantValue.HINTS_APPLICATION);
        blockMenuActions(false);
    }

    @Override
    public View getContentView() {
        View mainPanelRoot = (mainPanel == null) ? null : mainPanel.getRootLayout();
        return (mainPanelRoot == null) ? super.getContentView() : mainPanelRoot;
    }

    /**
     * @param fragment the fragment to activate
     */
    protected void activateFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        activeFragment = fragment;
        transaction.commit();
    }

    /**
     * @return the active fragement, can be null at the start of the application
     */
    protected Fragment getActiveFragment() {
        return activeFragment;
    }

    /**
     * @return true if the main panel is visible, false otherwise (the main menu
     * should be visible then)
     */
    protected boolean isMainPanelVisible() {
        return (activeFragment != null && activeFragment == mainPanel);
    }

    /**
     * Is called after the current activity fragment has been created.
     */
    public void onPostActivityChanged() {
        if (isMainPanelVisible()) {
            setStatusBar(statusBar);
        } else {
            setStatusBar(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (HGBaseAdvertisements.isShowing()) {
            HGBaseAdvertisements.hideAdvertisementDialog(this);
        } else if (isMainPanelVisible()) {
            int closeGameId = HGBaseResources.getResourceIdByName(MainMenu.MENU_ID_GAME_CLOSE, HGBaseResources.ID);
            getOptionsMenuAction(closeGameId).perform(closeGameId, null);
        } else {
            super.onBackPressed();
        }
        checkResumeGame();
    }

    /**
     * @param block True to block menu action, false to allow them.
     */
    protected void blockMenuActions(boolean block) {
        this.isBlockMenu = block;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        HGBaseGuiTools.setEnabledAllMenuItems(menu, !isBlockMenu);
        boolean result = super.onPrepareOptionsMenu(menu);
        if (mainMenu != null) {
            mainMenu.createButtonsInsteadOfActionBar();
        }
        return result;
    }

    @Override
    protected ListView createNavigationDrawerList(DrawerLayout navDrawer, Map<Integer, MenuItem> itemMap) {
        return super.createNavigationDrawerList(navDrawer, removeSoundSettingFromNavigationDrawer(itemMap));
    }

    /**
     * @param itemMap the original menu item map
     * @return the new menu item map without sound menu or the original one if
     * no sound menu item exists
     */
    protected Map<Integer, MenuItem> removeSoundSettingFromNavigationDrawer(Map<Integer, MenuItem> itemMap) {
        int soundMenuId = getSoundSettingsMenuId();
        if (itemMap.containsKey(soundMenuId)) {
            Map<Integer, MenuItem> itemMapWithoutSound = new LinkedHashMap<>(itemMap);
            itemMapWithoutSound.remove(soundMenuId);
            return itemMapWithoutSound;
        } else {
            return itemMap;
        }
    }

    /**
     * @return the id of the sound settings
     */
    int getSoundSettingsMenuId() {
        return HGBaseResources.getResourceIdByName(MainMenu.MENU_ID_SETTINGS_SOUND, HGBaseResources.ID);
    }

    /**
     * Shows the hint dialog for the given type (see ConstantValue.HINT_LIST),
     * if the user wants to see this dialogs.
     *
     * @param hintType The hint type.
     */
    public void showHintsDialog(String hintType) {
        GameConfig config = getGameManager().getGameConfig();
        if (!HGBaseConfig.getBoolean(ConstantValue.CONFIG_HINT_DONTSHOW) && HGBaseTools.hasContent(config.getHintsSetting(hintType))) {
            GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(this);
            GameEngine engine = getGameManager().getGameEngine();
            dlg.showGameHintsDialog(this, hintType, engine, config);
        }
    }

    /**
     * @return The application's main panel, can be null.
     */
    public MainPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * @return The game panel.
     */
    public GamePanel getGamePanel() {
        return (mainPanel != null) ? mainPanel.getGamePanel() : null;
    }

    /**
     * @return The main menu of tjger, i.e. the starting vie (NOT the Android
     * menu)
     */
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    /**
     * @return the tjger specific status bar
     */
    public MainStatusBar getMainStatusBar() {
        return (MainStatusBar) getStatusBar();
    }

    /**
     * Used to set the new game information. These values are typically set by
     * configuration values ({@link HGBaseConfig}).
     *
     * @param manager    the game manager
     * @param numPlayers the number of players that will take part at the new game
     * @see #setNewGameInformationFromIntegerConfig(GameManager, String, int)
     * @see #setNewGameInformationFromBooleanConfig(GameManager, String,
     * boolean)
     * @see #setNewGameInformationFromStringConfig(GameManager, String, String)
     * @see GameManager#setNewGameInformation(String, int)
     * @see GameManager#setNewGameInformation(String, boolean)
     * @see GameManager#setNewGameInformation(String, String)
     */
    protected void onSetNewGameInformation(GameManager manager, int numPlayers) {
        // NOCHECK: nothing to do in default implementation
    }

    /**
     * Sets a new game integer information from the configuration.
     *
     * @param manager      the game manager
     * @param configKey    the configuration key
     * @param defaultValue the default value
     */
    protected final void setNewGameInformationFromIntegerConfig(GameManager manager, String configKey, int defaultValue) {
        manager.setNewGameInformation(configKey, HGBaseConfig.getInt(configKey, defaultValue));
    }

    /**
     * Sets a new game boolean information from the configuration.
     *
     * @param manager      the game manager
     * @param configKey    the configuration key
     * @param defaultValue the default value
     */
    protected final void setNewGameInformationFromBooleanConfig(GameManager manager, String configKey, boolean defaultValue) {
        manager.setNewGameInformation(configKey, HGBaseConfig.getBoolean(configKey, defaultValue));
    }

    /**
     * Sets a new game string information from the configuration.
     *
     * @param manager      the game manager
     * @param configKey    the configuration key
     * @param defaultValue the default value
     */
    protected final void setNewGameInformationFromStringConfig(GameManager manager, String configKey, String defaultValue) {
        manager.setNewGameInformation(configKey, HGBaseConfig.get(configKey, defaultValue));
    }

    /**
     * A new game is started.
     * <p>
     * By default take the configuration values to define all parameters for a
     * new game and reset the auto save game. Switch to the main panel from the
     * main menu.
     */
    public void onGameNew() {
        GameEngine engine = getGameManager().getGameEngine();
        if (engine.isActiveGame()) {
            engine.stopGame();
        }
        if (isAutosaveFileAvailable()) {
            getAutosaveFile().delete();
        }
        setChanged(false);
        isResumeGame = false;
        activateFragment(mainPanel);
    }

    /**
     * This method does the new game stuff after the game panel is visible.
     */
    private void onGameNewAfterGamePanelWasCreated() {
        final int numPlayers = HGBaseConfig.getInt(ConstantValue.CONFIG_NUMPLAYERS, getGameConfig().getDefaultPlayers());
        getGameManager().resetNewGameInformation();
        onSetNewGameInformation(getGameManager(), numPlayers);
        if (getGameManager().getGameEngine().startGame(numPlayers) != 0) {
            HGBaseDialog.showErrorDialog(this, "err_startgame", (dialog, which) -> activateFragment(mainMenu));
        }
    }

    /**
     * Restart a new game from the game panel.
     */
    public void onGameNext() {
        if (isAutosaveFileAvailable()) {
            getAutosaveFile().delete();
        }
        GamePlayer[] active = getGameManager().getGameEngine().getActivePlayers();
        if (active != null) {
            getGameManager().getGameEngine().setActivePlayers(active.clone());
            if (getGameManager().getGameEngine().newGame() != 0) {
                HGBaseDialog.showErrorDialog(this, "err_startgame", (dialog, which) -> activateFragment(mainMenu));
            }
        }
    }

    /**
     * Is called by the game panel after it was created.
     */
    public void onGamePanelCreated() {
        if (isResumeGame) {
            onGameResumeAfterGamePanelWasCreated();
        } else {
            onGameNewAfterGamePanelWasCreated();
        }
    }

    /**
     * The auto saved game is restored.
     * <p>
     * Switch to the main panel from the main menu.
     */
    public void onGameResume() {
        if (isResumeGameAllowed()) {
            setChanged(false);
            isResumeGame = true;
            activateFragment(mainPanel);
        }
    }

    /**
     * This method does the resume game stuff after the game panel is visible.
     */
    private void onGameResumeAfterGamePanelWasCreated() {
        Element root = HGBaseXMLTools.readXML(AUTOSAVE_FILENAME);
        if (root == null || getGameManager().loadGame(root) != 0) {
            HGBaseDialog.showErrorDialog(this, "err_readgame", (dialog, which) -> activateFragment(mainMenu));
        }
    }

    /**
     * Enable/disable the resume game button depending if there is a game to
     * resume.
     */
    public void checkResumeGame() {
        if (getMainMenu() != null) {
            getMainMenu().setEnabledResume(isResumeGameAllowed());
        }
    }

    /**
     * Enable/disable the new game button depending if it is allowed to start a new game.
     */
    public void checkNewGame() {
        if (getMainMenu() != null) {
            getMainMenu().setEnabledNewGame(isNewGameAllowed());
        }
    }

    /**
     * Returns {@code true} if it is allowed to resume a game.
     *
     * @return {@code true} if it is allowed to resume a game.
     */
    public boolean isResumeGameAllowed() {
        return (isAutosaveFileAvailable()) && ((isProVersion()) || (!isProTeaserElementSelected()));
    }

    /**
     * Returns {@code true} if it is allowed to start a new game.
     *
     * @return {@code true} if it is allowed to start a new game.
     */
    public boolean isNewGameAllowed() {
        return (isProVersion()) || (!isProTeaserElementSelected());
    }

    /**
     * Returns {@code true} if at least one of the selected elements is a teaser for the pro version of the app.
     *
     * @return {@code true} if at least one of the selected elements is a teaser for the pro version of the app.
     */
    private boolean isProTeaserElementSelected() {
        return (isProTeaserPartSelected()) || (isProTeaserPlayerTypeSelected());
    }

    /**
     * Returns {@code true} if at least one of the selected parts is a teaser for the pro version of the app.
     *
     * @return {@code true} if at least one of the selected parts is a teaser for the pro version of the app.
     */
    private boolean isProTeaserPartSelected() {
        return getGameConfig().isProTeaserPartSelected();
    }

    /**
     * Returns {@code true} if at least one of the selected player types is a teaser for the pro version of the app.
     *
     * @return {@code true} if at least one of the selected player types is a teaser for the pro version of the app.
     */
    private boolean isProTeaserPlayerTypeSelected() {
        return PlayerManager.getInstance().isProTeaserPlayerTypeSelected();
    }

    /**
     * The current game is closed.
     * <p>
     * Automatically save the game state if game is not finished or delete auto
     * save file otherwise. Switch to the main menu from the main panel.
     */
    public void onGameClose() {
        GameEngine engine = getGameManager().getGameEngine();
        File saveFile = getAutosaveFile();
        if (engine.isActiveGame()) {
            if (isChanged()) {
                Document doc = HGBaseXMLTools.createDocument();
                if (doc == null) {
                    HGBaseDialog.showErrorDialog(this, "err_writegame");
                }
                int ret = getGameManager().saveGame(doc);
                if (ret != 0 || !HGBaseXMLTools.writeXML(doc, AUTOSAVE_FILENAME)) {
                    HGBaseDialog.showErrorDialog(this, "err_writegame");
                }
            }
            engine.stopGame();
        } else {
            // stop game anyway to reset game state
            engine.stopGame();
            if (isAutosaveFileAvailable()) {
                saveFile.delete();
            }
        }
        setChanged(false);
        activateFragment(mainMenu);
    }

    /**
     * @return the auto save file, may be null
     */
    public File getAutosaveFile() {
        return HGBaseFileTools.getFileForIntern(AUTOSAVE_FILENAME);
    }

    /**
     * @return true if there is an auto save file, otherwise false
     */
    public boolean isAutosaveFileAvailable() {
        File f = getAutosaveFile();
        return (f != null && f.exists());
    }

    /**
     * Plays a sound if the configuration is set.
     *
     * @param sound Sound identification as it is defined in the settings file.
     */
    public void playAudio(String sound) {
        if (HGBaseConfig.getBoolean(ConstantValue.CONFIG_PLAYSOUND, true)) {
            HGBaseSound.playAudio(sound);
        }
    }

    /**
     * Returns the game configuration.
     *
     * @return The game configuration.
     */
    private GameConfig getGameConfig() {
        return (getGameManager() == null) ? GameConfig.getInstance() : getGameManager().getGameConfig();
    }

    @Override
    public boolean isProVersion() {
        return getGameConfig().isProVersion();
    }

    @Override
    public String getAdvertisementURL() {
        return getGameConfig().getAdvertisementURL();
    }

    @Override
    public String getAdvertisementErrorPageURL() {
        return getGameConfig().getAdvertisementErrorPageURL();
    }

    @Override
    public int getAdvertisementViewWidthPercent() {
        int widthPercent = getGameConfig().getAdvertisementWidthPercent();
        return (widthPercent == HGBaseTools.INVALID_INT) ? super.getAdvertisementViewWidthPercent() : widthPercent;
    }

    @Override
    public int getAdvertisementViewHeightPercent() {
        int heightPercent = getGameConfig().getAdvertisementHeightPercent();
        return (heightPercent == HGBaseTools.INVALID_INT) ? super.getAdvertisementViewHeightPercent() : heightPercent;
    }

    @Override
    public FullscreenMode getFullscreenMode() {
        FullscreenMode mode;
        try {
            mode = FullscreenMode.valueOf(getGameConfig().getFullscreenMode().toUpperCase());
        } catch (Exception e) {
            mode = null;
        }

        return mode;
    }

    public enum ZoomType {
        ZOOM_FIT_ONLY, /* The game panel fits on the screen only. */
        ZOOM_SCROLL_VH, /*
         * Allows zooming, scrolling is only possible vertically
         * or horizontally.
         */
        ZOOM_SCROLL_ANY /*
         * Allows zooming, the scrolling is possibly in any
         * direction (experimental, does not work well).
         */
    }
}
