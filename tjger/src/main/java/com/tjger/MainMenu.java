package com.tjger;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.billingclient.api.ProductDetails;
import com.tjger.game.GameState;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.game.internal.GameStatistics;
import com.tjger.game.internal.PlayerProfiles;
import com.tjger.gui.GameDialogs;
import com.tjger.gui.NewGameDialog;
import com.tjger.gui.internal.GameDialogFactory;
import com.tjger.gui.internal.ZoomMenu;
import com.tjger.lib.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.hagru.hgbase.android.HGBaseAppTools;
import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.config.HGBaseConfigDialog;
import at.hagru.hgbase.gui.menu.IMenuAction;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.billing.HGBaseBillingHelper;
import at.hagru.hgbase.lib.internal.billing.HGBaseBillingListener;

/**
 * The game's main/starting menu displays some basic buttons.<p>
 * Note: this is not the menu of the tjger app, which is created by the menu xml file, but some well known functionality
 * for getting configuration, etc. is still implemented here.
 *
 * @author hagru
 */
public class MainMenu extends Fragment {

    public static final int DEFAULT_BUTTON_BACK_COLOR = Color.GRAY;
    public static final int DEFAULT_BUTTON_TEXT_COLOR = Color.WHITE;
    public static final int BUTTON_BACK_COLOR_DISABLED = Color.DKGRAY;
    public static final int BUTTON_TEXT_COLOR_DISABLED = Color.GRAY;
    public static final String MENU_ID_GAME_CLOSE = "game_close";
    public static final String MENU_ID_GAME_NEW = "game_new";
    public static final String MENU_ID_GAME_RESUME = "game_resume";
    public static final String MENU_ID_NEW_GAME_DLG = "dlg_new_game";
    public static final String MENU_ID_GAMEINFO = "action_gameinfo";
    public static final String MENU_ID_HELP_TJGER = "help_tjger";
    public static final String MENU_ID_HELP_INSTRUCTIONS = "help_instructions";
    public static final String MENU_ID_HELP_GAMEHINTS = "help_gamehints";
    public static final String MENU_ID_SETTINGS_PARTS = "settings_parts";
    /**
     * The menu id for the sound settings dialog.
     */
    public static final String MENU_ID_SETTINGS_SOUNDS = "settings_sounds";
    public static final String MENU_ID_SETTINGS_PLAYSOUND = "settings_playsound";
    /**
     * The menu id for the show credits dialog action.
     */
    public static final String MENU_ID_CREDITS = "action_credits";
    /**
     * The menu id of the remove advertisement action.
     */
    public static final String MENU_ID_REMOVE_ADS = "menu_remove_ads";
    /**
     * The menu id of the of the information about what is preventing a game start.
     */
    private static final String MENU_ID_PREVENT_GAME_START_INFO = "prevent_game_start_info";
    public static final String MAIN_MENU_IMAGE = "main_menu";
    public static final String MAIN_MENU_BUTTON = "main_menu_button";
    public static final int MENU_ICON_SIZE = 64;
    static final StatePanelMenu STATEPANEL_MENU = new StatePanelMenu();
    static final GameSpeedMenu GAMESPEED_MENU = new GameSpeedMenu();
    private static final int BUTTON_PADDING = 20;
    private final ZoomMenu zoomMenu;
    private final List<Button> buttonList = new ArrayList<>();
    private Button btResume;
    /**
     * The button to start a new game.
     */
    private Button btNewGame;
    private Button btMenuIcon;
    private Button btSoundSettings;

    private FrameLayout rootView;
    private View imageView;

    /**
     * Constructs a new instance.
     */
    public MainMenu() {
        this.zoomMenu = new ZoomMenu();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (Button bt : buttonList) {
            changeButtonLayoutParams(bt);
        }
        if (imageView != null) {
            rootView.removeView(imageView);
            imageView = createBackgroundImageView();
            rootView.addView(imageView, 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        buttonList.clear();
        btResume = null;
        btNewGame = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = new FrameLayout(getMainFrame());
        rootView.setLayoutParams(HGBaseGuiTools.createViewGroupLayoutParams(true, true));
        rootView.setBackgroundColor(Color.BLACK);
        imageView = createBackgroundImageView();
        if (imageView != null) {
            rootView.addView(imageView);
        }
        ScrollView scrollView = new ScrollView(getMainFrame());
        scrollView.setFillViewport(true);
        rootView.addView(scrollView);
        ViewGroup buttonView = createDefaultActionButtons(inflater);
        scrollView.addView(buttonView);

        addLeftSideIcons(rootView);
        addFooterInfo(rootView);

        return rootView;
    }

    /**
     * Inflates a menu from the specified resource.
     *
     * @param context The context for the menu.
     * @param menuRes The menu resource.
     * @return The inflated menu.
     */
    protected Menu inflateMenu(Context context, int menuRes) {
        Menu menu = new PopupMenu(context, null).getMenu();
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(menuRes, menu);
        return menu;
    }

    /**
     * Returns a list with all items of the specified menu.
     *
     * @param menu The menu from which the items should be retrieved.
     * @return A list with all items of the specified menu.
     */
    protected List<MenuItem> getMenuItems(Menu menu) {
        List<MenuItem> items = new ArrayList<>();
        if (menu == null) {
            return items;
        }
        for (int itemIndex = 0; itemIndex < menu.size(); itemIndex++) {
            items.add(menu.getItem(itemIndex));
        }
        return items;
    }

    /**
     * Adds icons to the left side of the main screen.<br>
     * Based on {@code main_left.xml}.
     *
     * @param parent The parent view.
     */
    protected void addLeftSideIcons(ViewGroup parent) {
        Context context = getActivity();
        Menu menu = inflateMenu(context, R.menu.main_left);

        int iconSize = (int) (72 * context.getResources().getDisplayMetrics().density);
        int margin = (int) (8 * context.getResources().getDisplayMetrics().density);
        final int[] topOffset = {margin};

        getMenuItems(menu).forEach(item -> {
            int itemId = item.getItemId();
            String itemName = context.getResources().getResourceEntryName(itemId);
            if ((item.getIcon() == null) || (!isMenuItemAvailable(itemName))) {
                return;
            }
            ImageButton icon = createIcon(context, item);
            if (MENU_ID_REMOVE_ADS.equals(itemName)) {
                HGBaseBillingHelper.getInstance().addListener(new HGBaseBillingListener() {
                    @Override
                    public void onProductsLoaded(@NonNull List<ProductDetails> products) {
                        // Nothing to do.
                    }

                    @Override
                    public void onPurchaseSuccess(@NonNull String productId) {
                        if ((icon != null) && (icon.getParent() instanceof ViewGroup) && (Objects.equals(getGameManager().getGameConfig().getAdvertisementProductId(), productId))) {
                            ((ViewGroup) icon.getParent()).removeView(icon);
                        }
                    }

                    @Override
                    public void onBillingError(@NonNull String message) {
                        // Nothing to do.
                    }
                });
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize, Gravity.TOP | Gravity.START);
            params.setMargins(margin, topOffset[0], 0, 0);
            icon.setLayoutParams(params);
            icon.setOnClickListener(v -> {
                IMenuAction action = getMainFrame().getOptionsMenuAction(itemId);
                if (action != null) {
                    action.perform(itemId, item);
                }
            });

            parent.addView(icon);

            topOffset[0] += iconSize + margin;
        });
    }

    /**
     * Adds a footer information to the bottom of the main screen.<br>
     * Based on {@code main_footer.xml}.
     *
     * @param parent The parent view.
     */
    protected void addFooterInfo(ViewGroup parent) {
        Context context = getActivity();
        Menu menu = inflateMenu(context, R.menu.main_footer);

        getMenuItems(menu).forEach(item -> {
            int itemId = item.getItemId();
            String itemName = context.getResources().getResourceEntryName(itemId);
            if (!isMenuItemAvailable(itemName)) {
                return;
            }
            TextView textView = new TextView(context);
            textView.setTextColor(getFooterTextColor());
            textView.setTextSize(20);
            textView.setPadding(16, 16, 16, 16);
            textView.setGravity(Gravity.CENTER);
            textView.setText(getFooterText(item, itemName));
            textView.setId(itemId);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            params.bottomMargin = 50;
            textView.setLayoutParams(params);

            parent.addView(textView);
        });
    }

    /**
     * Returns {@code true}, if the specified menu item is available.
     *
     * @param itemId The id of the menu item to check.
     * @return {@code true}, if the specified menu item is available.
     */
    protected boolean isMenuItemAvailable(String itemId) {
        if (MENU_ID_REMOVE_ADS.equals(itemId)) {
            return isRemoveAdvertisementsAvailable();
        } else if (MENU_ID_PREVENT_GAME_START_INFO.equals(itemId)) {
            return isPreventGameStartInfoAvailable();
        }
        return true;
    }

    /**
     * Returns {@code true}, if the menu item to remove advertisements is available.
     *
     * @return {@code true}, if the menu item to remove advertisements is available.
     */
    public boolean isRemoveAdvertisementsAvailable() {
        return getMainFrame().isAdvertisementAvailable();
    }

    /**
     * Returns {@code true}, if the information about what is preventing a game start is available.
     *
     * @return {@code true}, if the information about what is preventing a game start is available.
     */
    protected boolean isPreventGameStartInfoAvailable() {
        return (!getMainFrame().isResumeGameAllowed()) || (!getMainFrame().isNewGameAllowed());
    }

    /**
     * Returns the for the footer information.
     *
     * @param item The footer item.
     * @param itemId The id of the footer item.
     * @return The for the footer information.
     */
    protected String getFooterText(MenuItem item, String itemId) {
        if (MENU_ID_PREVENT_GAME_START_INFO.equals(itemId)) {
            return getPreventGameStartInfo();
        }
        return (String) item.getTitle();
    }

    /**
     * Returns the information about what is preventing a game start.
     *
     * @return The information about what is preventing a game start.
     */
    protected String getPreventGameStartInfo() {
        if (getMainFrame().isUnpurchasedElementSelected()) {
            return HGBaseText.getText(R.string.prevent_game_start_info_unpurchased);
        } else if ((!getMainFrame().isProVersion()) && (getMainFrame().isProTeaserElementSelected())) {
            return HGBaseText.getText(R.string.prevent_game_start_info_teaser);
        }
        return "";
    }

    /**
     * Returns the text color for the footer information.
     *
     * @return The text color for the footer information.
     */
    protected int getFooterTextColor() {
        return Color.RED;
    }

    /**
     * Creates a clickable icon for the specified menu item.
     *
     * @param context The context.
     * @param item    The menu item.
     * @return The created icon.
     */
    protected ImageButton createIcon(Context context, MenuItem item) {
        ImageButton button = new ImageButton(context);
        button.setImageDrawable(item.getIcon());
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        button.setContentDescription(item.getTitle());
        return button;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainFrame().onPostActivityChanged();
        createButtonsInsteadOfActionBar();
    }

    /**
     * Create buttons directly into the main menu view if there exits no action bar.
     *
     * @see #getMenuIconButton()
     * @see #getSoundSettingsButton()
     */
    protected void createButtonsInsteadOfActionBar() {
        if (isVisible()) {
            btMenuIcon = null;
            btSoundSettings = null;
            if (!isActionBarVisible()) {
                if (getMainFrame().getOptionsMenuId() > 0) {
                    btMenuIcon = createOptionMenuIcon();
                }
                int soundMenuId = getMainFrame().getSoundSettingsMenuId();
                if (getMainFrame().getOptionsMenuItem(soundMenuId) != null) {
                    btSoundSettings = createSoundMenuIcon(soundMenuId);
                }
            }
        }
    }

    /**
     * Returns {@code true} if the action bar is visible.
     *
     * @return {@code true} if the action bar is visible.
     */
    protected boolean isActionBarVisible() {
        return (HGBaseAppTools.getTitleBarHeight(getActivity()) != 0);
    }

    /**
     * Calculates the position for a menu icon.
     *
     * @param positionIndex The index of the position of the menu icon.
     * @return {@link android.widget.FrameLayout.LayoutParams} for the calculated position.
     */
    protected FrameLayout.LayoutParams calcMenuIconPosition(int positionIndex) {
        int position = (HGBaseGuiTools.isScreenLandscape(getActivity())) ? Gravity.END | Gravity.TOP : Gravity.END | Gravity.BOTTOM;
        int iconSize = (int) (MENU_ICON_SIZE * getResources().getDisplayMetrics().density / 1.5);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(iconSize, iconSize, position);
        int margin = (iconSize / 4) + ((iconSize + (iconSize / 4)) * positionIndex);
        if ((HGBaseGuiTools.isScreenLandscape(getActivity()))) {
            lp.rightMargin = iconSize / 4;
            lp.topMargin = margin;
        } else {
            lp.rightMargin = margin;
            lp.topMargin = iconSize / 4;
        }
        return lp;
    }

    /**
     * Creates a menu icon.
     *
     * @param iconResId       The resource id of the icon.
     * @param positionIndex   The index of the position of the menu icon.
     * @param onClickListener The {@link OnClickListener} for the menu icon.
     * @return The created menu icon.
     */
    private Button createMenuIcon(int iconResId, int positionIndex, OnClickListener onClickListener) {
        Button button = new Button(getActivity());
        button.setBackground(HGBaseResources.getDrawable(iconResId));
        rootView.addView(button, calcMenuIconPosition(positionIndex));
        button.setOnClickListener(onClickListener);
        return button;
    }

    /**
     * Creates a menu icon.
     *
     * @param menuId          The id of the menu item.
     * @param iconResId       The resource id of the icon.
     * @param positionIndex   The index of the position of the menu icon.
     * @param onClickListener The {@link OnClickListener} for the menu icon.
     * @return The created menu icon.
     */
    protected Button createMenuIcon(String menuId, int iconResId, int positionIndex, OnClickListener onClickListener) {
        Button button = null;
        int menuResId = HGBaseResources.getResourceIdByName(menuId, HGBaseResources.ID);
        if (getMainFrame().getOptionsMenuItem(menuResId) != null) {
            button = createMenuIcon(iconResId, positionIndex, onClickListener);
        }
        return button;
    }

    /**
     * Creates an options menu icon if no action bar is displayed.
     *
     * @return the new created button
     */
    protected Button createOptionMenuIcon() {
        return createMenuIcon(R.drawable.menu_icon, 0, view -> {
            // getActivity().openOptionsMenu(); does not work on new Android versions
            PopupMenu menu = new PopupMenu(getMainFrame(), view);
            menu.setOnMenuItemClickListener(item -> {
                IMenuAction action = getMainFrame().getOptionsMenuAction(item.getItemId());
                if (action != null) {
                    action.perform(item.getItemId(), item);
                    return true;
                } else {
                    return false;
                }
            });
            menu.inflate(getMainFrame().getOptionsMenuId());
            menu.getMenu().removeItem(getMainFrame().getSoundSettingsMenuId());
            if (getActivity().onPrepareOptionsMenu(menu.getMenu())) {
                menu.show();
            }
        });
    }

    /**
     * Creates a sound settings icon if no action bar is displayed (and sound settings is activated in the menu).
     *
     * @param soundMenuId the menu id of the sound settings
     * @return the new created button
     */
    protected Button createSoundMenuIcon(final int soundMenuId) {
        int imageId = HGBaseConfig.getBoolean(MainMenu.MENU_ID_SETTINGS_PLAYSOUND) ? R.drawable.sound_on : R.drawable.sound_off;
        return createMenuIcon(imageId, 1, view -> getMainFrame().getOptionsMenuAction(soundMenuId).perform(soundMenuId, null));
    }

    /**
     * @return the menu icon button or null if the action bar is displayed
     */
    public Button getMenuIconButton() {
        return btMenuIcon;
    }

    /**
     * @return the button for sound configuration or null if an action bar is displayed
     */
    public Button getSoundSettingsButton() {
        return btSoundSettings;
    }

    /**
     * Creates the view with the background image on the main menu, if there is one available.
     *
     * @return the new created view or null
     */
    protected View createBackgroundImageView() {
        Bitmap image = HGBaseGuiTools.loadImage(MAIN_MENU_IMAGE);
        if (image != null) {
            ImageView view = new ImageView(getMainFrame());
            view.setBackgroundColor(0);
            view.setImageBitmap(image);
            ScaleType scaleType = getGameManager().getGameConfig().getMainMenuImageScaleType();
            if (scaleType != null) {
                view.setScaleType(scaleType);
            }
            return view;
        } else {
            return null;
        }
    }

    /**
     * Create the default buttons that shall be shown on this view.
     * Is called by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} method.
     *
     * @param inflater the layout inflater if the layout is taken from an xml file
     * @return the new view with the buttons
     */
    protected ViewGroup createDefaultActionButtons(LayoutInflater inflater) {
        GameConfig config = getGameManager().getGameConfig();
        LinearLayout layout = HGBaseGuiTools.createLinearLayout(getMainFrame(), false);
        setResumeButton(addButton(layout, MENU_ID_GAME_RESUME));
        setEnabledResume(getMainFrame().isResumeGameAllowed());
        setNewGameButton(addButton(layout, MENU_ID_GAME_NEW));
        setEnabledNewGame(getMainFrame().isNewGameAllowed());
        addButton(layout, MENU_ID_NEW_GAME_DLG);
        addButton(layout, MENU_ID_SETTINGS_PARTS);
        if (showSoundSettingsDlgButton(config)) {
            addButton(layout, MENU_ID_SETTINGS_SOUNDS);
        }
        if (config.isRememberGames() || config.isRememberScores() || config.getHighScoreLength() > 0) {
            addButton(layout, MENU_ID_GAMEINFO);
        }
        layout.setBackgroundColor(Color.TRANSPARENT);
        return layout;
    }

    /**
     * Returns {@code true} if the button for sound settings dialog should be shown.
     *
     * @param config The configuration object.
     * @return {@code true} if the button for sound settings dialog should be shown.
     */
    protected boolean showSoundSettingsDlgButton(GameConfig config) {
        return (config.getSoundTypes().length > 0) || (config.getSoundSetTypes().length > 0);
    }

    /**
     * @return the a list with all created button by the method {@link #addButton(LinearLayout, String)}.
     */
    protected List<Button> getButtons() {
        return buttonList;
    }

    /**
     * @return the resume button created by {@link #createDefaultActionButtons(LayoutInflater)}.
     */
    protected Button getResumeButton() {
        return btResume;
    }

    /**
     * @param btResume the resume button
     */
    protected void setResumeButton(Button btResume) {
        this.btResume = btResume;
    }

    /**
     * Sets the new game button.
     *
     * @param btNewGame The button to set.
     */
    protected void setNewGameButton(Button btNewGame) {
        this.btNewGame = btNewGame;
    }

    /**
     * Adds a button to the (main) button view.
     *
     * @param buttonView the button view
     * @param menuId     the menu id
     * @return the new created button that was added
     */
    protected Button addButton(LinearLayout buttonView, String menuId) {
        Button bt = HGBaseGuiTools.createActionButton(getMainFrame(), menuId);
        setButtonColors(bt);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING, 0);
        lp.gravity = Gravity.CENTER;
        bt.setLayoutParams(lp);
        changeButtonLayoutParams(bt);
        buttonView.addView(bt);
        buttonList.add(bt);
        return bt;
    }

    /**
     * Sets the text color and the background color or image for a menu button.
     *
     * @param bt the button to set the color(s) and the image for
     */
    public void setButtonColors(Button bt) {
        int backgroundId = HGBaseResources.getResourceIdByName(MAIN_MENU_BUTTON, HGBaseResources.DRAWABLE);
        if (backgroundId > 0) {
            bt.setBackground(HGBaseResources.getDrawable(backgroundId));
            bt.setAlpha(1.0f);
        } else {
            bt.setBackgroundColor(getButtonBackColor());
        }
        bt.setTextColor(getButtonTextColor());
    }

    /**
     * Sets the text color and background color or image for the specified button depending if it is enabled or not.
     *
     * @param button  The button where to set the color.
     * @param enabled The flag if the button is enabled or not.
     */
    protected void setEnabledButtonColors(Button button, boolean enabled) {
        if (button == null) {
            return;
        }
        if (enabled) {
            setButtonColors(button);
        } else {
            button.setTextColor(BUTTON_TEXT_COLOR_DISABLED);
            int backgroundId = HGBaseResources.getResourceIdByName(MAIN_MENU_BUTTON, HGBaseResources.DRAWABLE);
            if (backgroundId > 0) {
                button.setAlpha(0.5f);
            } else {
                button.setBackgroundColor(BUTTON_BACK_COLOR_DISABLED);
            }
            button.setTextColor(getButtonTextColor());
        }
    }

    /**
     * @return the text color for the button
     */
    protected int getButtonTextColor() {
        return DEFAULT_BUTTON_TEXT_COLOR;
    }

    /**
     * @return the background color for the button (if no background image set)
     */
    protected int getButtonBackColor() {
        return DEFAULT_BUTTON_BACK_COLOR;
    }

    /**
     * Change the button layout, depending on the screen orientation.
     *
     * @param bt the button to change the layout for
     */
    protected void changeButtonLayoutParams(Button bt) {
        LayoutParams lp = (LayoutParams) bt.getLayoutParams();
        if (HGBaseGuiTools.isScreenPortrait(getMainFrame())) {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = HGBaseGuiTools.getScreenSize(getMainFrame()).x / 2;
        }
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        bt.requestLayout();
    }

    /**
     * @return the main tjger activity
     */
    public MainFrame getMainFrame() {
        return MainFrame.getInstance();
    }

    /**
     * Convenience method for calling {@link #getMainFrame()}.
     *
     * @return The MainFrame of tjger.
     */
    public MainFrame getTjgerMainFrame() {
        return getMainFrame();
    }

    /**
     * @return The GameManager object.
     */
    public GameManager getGameManager() {
        return getMainFrame().getGameManager();
    }

    /**
     * Repaints the main panel, including the game panel.
     */
    public void refreshMainPanel() {
        MainPanel panel = getMainFrame().getMainPanel();
        if (panel != null) {
            panel.refresh();
        }
    }

    /**
     * Enable/disable the resume game button.
     *
     * @param resumePossible true if it is possible to resume, otherwise false
     */
    public void setEnabledResume(boolean resumePossible) {
        if (btResume != null) {
            btResume.setEnabled(resumePossible);
            setResumeButtonColors(resumePossible);
            setPreventStartGameFooterInfo();
        }
    }

    /**
     * Enable/disable the new game button.
     *
     * @param newGamePossible {@code true} if it is possible to start a new game, otherwise {@code false}.
     */
    public void setEnabledNewGame(boolean newGamePossible) {
        if (btNewGame != null) {
            btNewGame.setEnabled(newGamePossible);
            setNewGameButtonColors(newGamePossible);
            setPreventStartGameFooterInfo();
        }
    }

    /**
     * Sets the footer information about what prevents a game from starting.
     */
    protected void setPreventStartGameFooterInfo() {
        TextView preventGameStartInfo = rootView.findViewById(R.id.prevent_game_start_info);
        if (preventGameStartInfo == null) {
            return;
        }
        preventGameStartInfo.setText(getPreventGameStartInfo());
    }

    /**
     * Sets the text color and background color or image for the resume button.
     *
     * @param resumePossible true if resume is possible, false if not
     */
    protected void setResumeButtonColors(boolean resumePossible) {
        setEnabledButtonColors(btResume, resumePossible);
    }

    /**
     * Set the text color and background color or image for the new game button.
     *
     * @param newGamePossible {@code true} if it is allowed to start a new game.
     */
    protected void setNewGameButtonColors(boolean newGamePossible) {
        setEnabledButtonColors(btNewGame, newGamePossible);
    }

    /**
     * @return The active zoom.
     */
    public int getZoom() {
        return zoomMenu.getZoom();
    }

    /**
     * @param newZoom The new zoom value to set.
     */
    public void setZoom(int newZoom) {
        zoomMenu.setZoom(newZoom);
    }


    /**
     * Set the zoom, that the game field fit's into the window.
     */
    public void setZoomFitToWindow() {
        zoomMenu.onFitWindow();
    }

    /**
     * @return The zoom menu.
     */
    public ZoomMenu getZoomMenu() {
        return zoomMenu;
    }

    /**
     * Shows the dialog for displaying the game instructions
     */
    public void showGameInstructionsDialog() {
        GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(getMainFrame());
        dlg.showGameInstructionsDialog(getMainFrame());
    }

    /**
     * Shows the dialog for displaying game hints.
     */
    public void showGameHintsDialog() {
        GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(getMainFrame());
        GameConfig config = getGameManager().getGameConfig();
        GameEngine engine = getGameManager().getGameEngine();
        dlg.showGameHintsDialog(getMainFrame(), null, engine, config);
    }

    /**
     * Shows a dialog with various game information.
     */
    public void showGameInfoDialog() {
        GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(getMainFrame());
        GameConfig config = getGameManager().getGameConfig();
        GameEngine engine = getGameManager().getGameEngine();
        GameState state = engine.getGameState();
        GameStatistics statistics = getGameManager().getGameStatistics();
        PlayerProfiles playerProfiles = getGameManager().getPlayerProfiles();
        dlg.showGameInfoDialog(getMainFrame(), config, engine, state, statistics, playerProfiles);
    }

    /**
     * Show the new game dialog.
     */
    public void showNewGameDialog() {
        // get the new game dialog and show it
        NewGameDialog dlg = GameDialogFactory.getInstance().createNewDialog();
        HGBaseConfigDialog.show(getMainFrame(), dlg.getClass());
    }

    /**
     * Shows the credits dialog.
     */
    public void showCreditsDlg() {
        GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(getMainFrame());
        dlg.showCreditsDlg(getMainFrame());
    }

    /**
     * Data for the state panel menu.
     */
    static class StatePanelMenu {

        private static final String STATEPANEL_DEFAULT = StatePanelPosition.STATEPANEL_AUTO.name();

        /**
         * @return the current value or the default one
         */
        public String getValue() {
            return HGBaseConfig.get(ConstantValue.CONFIG_STATEPANEL, getDefaultValue());
        }

        /**
         * @return the default value
         */
        public String getDefaultValue() {
            return STATEPANEL_DEFAULT;
        }

        /**
         * @return a list with all possible positions
         */
        public String[] getPositions() {
            return HGBaseTools.toStringArray(StatePanelPosition.values());
        }

        public enum StatePanelPosition {
            STATEPANEL_NONE, STATEPANEL_AUTO, STATEPANEL_BOTTOM, STATEPANEL_TOP, STATEPANEL_RIGHT, STATEPANEL_LEFT
        }
    }

    /**
     * Data for the game speed menu.
     */
    static class GameSpeedMenu {

        public static final String GAMESPEED_CONFIG = "gamespeed";
        private static final String GAMESPEED_DEFAULT = GameSpeedOptions.GAMESPEED_NORMAL.name();

        /**
         * @return the current value or the default one
         */
        public String getValue() {
            return HGBaseConfig.get(GAMESPEED_CONFIG, getDefaultValue());
        }

        /**
         * @return the default value
         */
        public String getDefaultValue() {
            return GAMESPEED_DEFAULT;
        }

        /**
         * @return all game speed options
         */
        public String[] getOptions() {
            return HGBaseTools.toStringArray(GameSpeedOptions.values());
        }

        public enum GameSpeedOptions {
            GAMESPEED_SLOW(2.0f), GAMESPEED_NORMAL(1.0f), GAMESPEED_FAST(0.5f);

            private final float value;

            GameSpeedOptions(float value) {
                this.value = value;
            }

            public float getValue() {
                return value;
            }
        }
    }

}
