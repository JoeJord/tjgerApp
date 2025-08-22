package com.tjger.gui.internal;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.GameDialogs;
import com.tjger.gui.completed.Arrangement;
import com.tjger.gui.completed.Background;
import com.tjger.gui.completed.BackgroundColor;
import com.tjger.gui.completed.Board;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.Cover;
import com.tjger.gui.completed.GameElement;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.PieceSet;
import com.tjger.lib.ConstantValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.config.HGBaseColorPreference;
import at.hagru.hgbase.gui.config.HGBaseConfigTools;
import at.hagru.hgbase.lib.HGBaseText;

/**
 * The dialog for choosing the game parts.
 *
 * @author hagru
 */
public class PartsDlg extends GameElementsDlg<Arrangement> {
    /**
     * The list of the card sets.
     */
    private final String[] cardSetTypes;
    /**
     * The list of the user defined part sets.
     */
    private final String[] userPartSets;
    /**
     * The list of the user defined parts.
     */
    private final String[] userParts;
    /**
     * The list of the user defined colors.
     */
    private final String[] colorTypes;
    /**
     * The current background color.
     */
    private Color backgroundColor;
    /**
     * The preview panel
     */
    private PreviewPanel pnPreview;

    /**
     * Constructs a new instance.
     */
    public PartsDlg() {
        super(HGBaseText.getText("settings_parts").replace('.', ' '), ConstantValue.CONFIG_ARRANGEMENT);
        GameConfig config = GameConfig.getInstance();
        cardSetTypes = config.getCardSetTypes();
        userPartSets = config.getPartSetTypes();
        userParts = config.getPartTypes();
        colorTypes = config.getColorTypes();
    }

    @Override
    protected void createComponents() {
        super.createComponents();
        addPreviewPanel();
    }

    /**
     * Adds the preview panel to the dialog.
     */
    protected void addPreviewPanel() {
        createPreviewPanel();
        int previewWidth = getPreviewWidth();
        int previewHeight = getPreviewHeight();
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent();
        if (root != null) {
            // use a trick to get the linear layout where the preferences are added
            if (HGBaseGuiTools.isScreenLandscape(this)) {
                // do some special handling to put the panel to the right of the preferences list in landscape orientation
                LinearLayout warpLayout = HGBaseGuiTools.createLinearLayout(this, true);
                View listView = root.getChildAt(0);
                root.removeAllViews();
                LinearLayout.LayoutParams lp = HGBaseGuiTools.createLinearLayoutParams(true, true);
                lp.width = HGBaseGuiTools.getScreenSize(this).x - previewWidth;
                warpLayout.addView(listView, lp);
                root.addView(warpLayout);
                root = warpLayout;
                previewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(previewWidth, previewHeight);
            lp.gravity = Gravity.CENTER;
            root.addView(pnPreview, lp);
        } else {
            // if the linear layout is not found, add the view as frame layout (will possibly overlap other items)
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(previewWidth, previewHeight);
            lp.gravity = (HGBaseGuiTools.isScreenLandscape(this)) ? Gravity.END : Gravity.BOTTOM;
            addContentView(pnPreview, lp);
        }
    }

    @Override
    public boolean isCompleteArrangement() {
        GameConfig config = GameConfig.getInstance();
        return (config.isCompleteArrangement() && config.getArrangements().length > 0);
    }

    @Override
    protected List<ConfigItem> loadConfigItems() {
        GameConfig config = GameConfig.getInstance();
        List<ConfigItem> configItems = new ArrayList<>();
        addArrangements(configItems, config);
        addBackgroundColor(configItems, config);
        addBackgrounds(configItems, config);
        addBoards(configItems, config);
        addCovers(configItems, config);
        addCardSets(configItems, config);
        addPieceSets(configItems, config);
        addPartSets(configItems, config);
        addParts(configItems, config);
        addColorChoosers(configItems, config);
        return configItems;
    }

    /**
     * Adds a combobox for the arrangements.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addArrangements(List<ConfigItem> configItems, GameConfig config) {
        configItems.add(createGameElementComboBox(ConstantValue.CONFIG_ARRANGEMENT, config.getArrangements(), config.getActiveArrangement()));
    }

    /**
     * Adds a color chooser for the background color.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addBackgroundColor(List<ConfigItem> configItems, GameConfig config) {
        BackgroundColor bgColor = config.getBackgroundColor();
        if (bgColor == null) {
            return;
        }
        backgroundColor = bgColor.getActiveColor();
        configItems.add(createColorChooser(ConstantValue.CONFIG_BACKCOLOR, bgColor.getDefaultColor(), backgroundColor));
    }

    /**
     * Adds a combobox for the backgrounds.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addBackgrounds(List<ConfigItem> configItems, GameConfig config) {
        configItems.add(createGameElementComboBox(ConstantValue.CONFIG_BACKGROUND, config.getBackgrounds(), config.getActiveBackground()));
    }

    /**
     * Adds a combobox for the boards.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addBoards(List<ConfigItem> configItems, GameConfig config) {
        configItems.add(createGameElementComboBox(ConstantValue.CONFIG_BOARD, config.getBoards(), config.getActiveBoard()));
    }

    /**
     * Adds a combobox for the covers.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addCovers(List<ConfigItem> configItems, GameConfig config) {
        configItems.add(createGameElementComboBox(ConstantValue.CONFIG_COVER, config.getCovers(), config.getActiveCover()));
    }

    /**
     * Adds a combobox for the card sets.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addCardSets(List<ConfigItem> configItems, GameConfig config) {
        Arrays.asList(cardSetTypes).forEach(cardSetType -> configItems.add(createGameElementComboBox(cardSetType, config.getCardSets(cardSetType), config.getActiveCardSet(cardSetType))));
    }

    /**
     * Adds a combobox for the piece sets.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addPieceSets(List<ConfigItem> configItems, GameConfig config) {
        configItems.add(createGameElementComboBox(ConstantValue.CONFIG_PIECESET, config.getPieceSets(), config.getActivePieceSet()));
    }

    /**
     * Adds a combobox for the user defined part sets.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addPartSets(List<ConfigItem> configItems, GameConfig config) {
        Arrays.asList(userPartSets).forEach(partSetType -> configItems.add(createGameElementComboBox(partSetType, config.getPartSets(partSetType), config.getActivePartSet(partSetType))));
    }

    /**
     * Adds a combobox for the user defined parts.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addParts(List<ConfigItem> configItems, GameConfig config) {
        Arrays.asList(userParts).forEach(partType -> configItems.add(createGameElementComboBox(partType, config.getParts(partType), config.getActivePart(partType))));
    }

    /**
     * Adds a color chooser for the user defined color.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    private void addColorChoosers(List<ConfigItem> configItems, GameConfig config) {
        Arrays.asList(colorTypes).forEach(colorType -> configItems.add(createColorChooser(colorType, config.getDefaultColor(colorType), config.getActiveColor(colorType))));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);

        ConfigColorChooser colorChooser = getColorChooser(key);
        if (colorChooser != null) {
            if (ConstantValue.CONFIG_BACKCOLOR.equals(key)) {
                onBackgroundColorChanged(colorChooser);
            } else {
                onColorChooserChanged(colorChooser);
            }
        }
    }

    @Override
    protected void onArrangementChanged() {
        super.onArrangementChanged();
        refreshPreview();
    }

    @Override
    protected void onGameElementComboBoxChanged(GameElementsDlg<Arrangement>.GameElementComboBox combobox) {
        super.onGameElementComboBoxChanged(combobox);
        refreshPreview();
    }

    /**
     * Handles the change of the chosen background color.
     *
     * @param colorChooser The color chooser that was changed.
     */
    private void onBackgroundColorChanged(ConfigColorChooser colorChooser) {
        backgroundColor = colorChooser.getColor();
        onColorChooserChanged(colorChooser);
    }

    /**
     * Handles the change of the chosen color.
     *
     * @param colorChooser The color chooser that was changed.
     */
    private void onColorChooserChanged(ConfigColorChooser colorChooser) {
        if (isArrangementElement(colorChooser.getOption())) {
            testArrangementModified();
        }
        refreshPreview();
    }

    /**
     * Create a color chooser for the specified color type.
     *
     * @param colorType    The type of the color.
     * @param defaultColor The default color.
     * @param activeColor  The current active color of the type.
     * @return The created color chooser.
     */
    private ConfigColorChooser createColorChooser(String colorType, Color defaultColor, Color activeColor) {
        return new ConfigColorChooser(this, colorType, (activeColor != null) ? activeColor : defaultColor);
    }

    /**
     * Returns the width of the preview panel.
     *
     * @return The width of the preview panel.
     */
    protected int getPreviewWidth() {
        double factor = HGBaseGuiTools.isScreenLandscape(this) ? 0.4 : 0.7;
        return (int) (HGBaseGuiTools.getScreenSize(this).x * factor);
    }

    /**
     * Returns the height of the preview panel.
     *
     * @return the height of the preview panel,
     */
    protected int getPreviewHeight() {
        return (int) (getPreviewWidth() * 0.75);
    }

    /**
     * Creates the preview panel.
     */
    protected void createPreviewPanel() {
        GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(this);
        pnPreview = dlg.getPreviewPanel(this, getPreviewWidth(), getPreviewHeight());
        HGBaseGuiTools.setBlackBorder(pnPreview);
    }

    /**
     * Refreshes the preview panel.
     */
    private void refreshPreview() {
        if (pnPreview == null) {
            return;
        }
        pnPreview.invalidate();
    }

    /**
     * Returns the color chooser for the specified type or {@code null}.
     *
     * @param type The type of the color chooser.
     * @return The color chooser for the specified type or {@code null}.
     */
    private ConfigColorChooser getColorChooser(String type) {
        ConfigItem configItem = getConfigItem(type);
        if (configItem instanceof ConfigColorChooser) {
            return (ConfigColorChooser) configItem;
        }
        return null;
    }

    /**
     * Returns the selected color of the color chooser for the specified type or {@code null}.
     *
     * @param type The type of the color chooser.
     * @return The selected color of the color chooser for the specified type or {@code null}.
     */
    private Color getSelectedColorChooserValue(String type) {
        ConfigColorChooser colorChooser = getColorChooser(type);
        return (colorChooser != null) ? colorChooser.getColor() : null;
    }

    /**
     * Returns the currently selected background color.
     *
     * @return The currently selected background color.
     */
    public Color getSelectedBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the currently selected background.
     *
     * @return The currently selected background.
     */
    public Background getSelectedBackground() {
        return (Background) getSelectedComboboxValue(ConstantValue.CONFIG_BACKGROUND);
    }

    /**
     * Returns the currently selected board.
     *
     * @return The currently selected board.
     */
    private Board getSelectedBoard() {
        return (Board) getSelectedComboboxValue(ConstantValue.CONFIG_BOARD);
    }

    /**
     * Returns the currently selected pieceset.
     *
     * @return The currently selected pieceset.
     */
    private PieceSet getSelectedPieceSet() {
        return (PieceSet) getSelectedComboboxValue(ConstantValue.CONFIG_PIECESET);
    }

    /**
     * Returns the currently selected cover.
     *
     * @return The currently selected cover.
     */
    private Cover getSelectedCover() {
        return (Cover) getSelectedComboboxValue(ConstantValue.CONFIG_COVER);
    }

    /**
     * Returns the currently selected cardset of the specified type.
     *
     * @param type The type of the cardset.
     * @return The currently selected cardset of the specified type.
     */
    private CardSet getSelectedCardSet(String type) {
        return (CardSet) getSelectedComboboxValue(type);
    }

    /**
     * Returns the currently selected partset of the specified type.
     *
     * @param type The type of the partset.
     * @return The currently selected partset of the specified type.
     */
    private PartSet getSelectedPartSet(String type) {
        return (PartSet) getSelectedComboboxValue(type);
    }

    /**
     * Returns the currently selected part of the specified type.
     *
     * @param type The type of the part.
     * @return The currently selected part of the specified type.
     */
    private Part getSelectedPart(String type) {
        return (Part) getSelectedComboboxValue(type);
    }

    /**
     * Returns the currently selected color of the specified type.
     *
     * @param type The type of the color.
     * @return The currently selected color of the specified type.
     */
    private Color getSelectedColor(String type) {
        return getSelectedColorChooserValue(type);
    }

    /**
     * Returns {@code true} if the specified type is a cardset.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is a cardset.
     */
    private boolean isCardSet(String type) {
        return Arrays.asList(cardSetTypes).contains(type);
    }

    /**
     * Returns {@code true} if the specified type is a user defined partset.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is a user defined partset.
     */
    private boolean isUserPartSet(String type) {
        return Arrays.asList(userPartSets).contains(type);
    }

    /**
     * Returns {@code true} if the specified type is a user defined part.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is a user defined part.
     */
    private boolean isUserPart(String type) {
        return Arrays.asList(userParts).contains(type);
    }

    /**
     * Returns {@code true} if the specified type is a color.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is a color.
     */
    private boolean isColor(String type) {
        return Arrays.asList(colorTypes).contains(type);
    }

    @Override
    protected Set<String> getArrangementTypes() {
        return Arrays.stream(GameConfig.getInstance().getArrangements()).flatMap(arrangement -> arrangement.getTypes().stream()).collect(Collectors.toSet());
    }

    @Override
    protected Object getArrangementValue(Arrangement arrangement, String type) {
        switch (type) {
            case ConstantValue.CONFIG_BACKCOLOR:
                return arrangement.getBackgroundColor();
            case ConstantValue.CONFIG_BACKGROUND:
                return arrangement.getBackground();
            case ConstantValue.CONFIG_BOARD:
                return arrangement.getBoard();
            case ConstantValue.CONFIG_PIECESET:
                return arrangement.getPieceSet();
            case ConstantValue.CONFIG_COVER:
                return arrangement.getCover();
            default:
                if (isCardSet(type)) {
                    return arrangement.getCardSet(type);
                } else if (isUserPartSet(type)) {
                    return arrangement.getPartSet(type);
                } else if (isUserPart(type)) {
                    return arrangement.getPart(type);
                } else if (isColor(type)) {
                    return arrangement.getColor(type);
                }
        }
        return null;
    }

    @Override
    protected Arrangement[] getAvailableArrangements() {
        return GameConfig.getInstance().getArrangements();
    }

    @Override
    protected boolean isArrangementElement(Arrangement arrangement, String type) {
        switch (type) {
            case ConstantValue.CONFIG_BACKCOLOR:
                return Objects.equals(arrangement.getBackgroundColor(), getSelectedBackgroundColor());
            case ConstantValue.CONFIG_BACKGROUND:
                return isArrangementElement(arrangement.getBackground(), getSelectedBackground());
            case ConstantValue.CONFIG_BOARD:
                return isArrangementElement(arrangement.getBoard(), getSelectedBoard());
            case ConstantValue.CONFIG_PIECESET:
                return isArrangementElement(arrangement.getPieceSet(), getSelectedPieceSet());
            case ConstantValue.CONFIG_COVER:
                return isArrangementElement(arrangement.getCover(), getSelectedCover());
            default:
                if (isCardSet(type)) {
                    return isArrangementElement(arrangement.getCardSet(type), getSelectedCardSet(type));
                } else if (isUserPartSet(type)) {
                    return isArrangementElement(arrangement.getPartSet(type), getSelectedPartSet(type));
                } else if (isUserPart(type)) {
                    return isArrangementElement(arrangement.getPart(type), getSelectedPart(type));
                } else if (isColor(type)) {
                    return Objects.equals(arrangement.getColor(type), getSelectedColor(type));
                }
        }
        return false; // Unknown type is not an element of the arrangement.
    }

    @Override
    protected void setConfigItemValue(String type, Object value) {
        ConfigItem configItem = getConfigItem(type);
        if (configItem instanceof GameElementsDlg.GameElementComboBox) {
            ((GameElementsDlg<?>.GameElementComboBox) configItem).setSelectedItem((GameElement) value);
        } else if ((configItem instanceof ConfigColorChooser) && (value instanceof Color)) {
            ((ConfigColorChooser) configItem).setColor((Color) value);
        }
    }

    @Override
    protected Object getConfigItemValue(String type) {
        ConfigItem configItem = getConfigItem(type);
        if (configItem instanceof GameElementsDlg.GameElementComboBox) {
            return ((GameElementsDlg<?>.GameElementComboBox) configItem).getSelectedItem();
        } else if (configItem instanceof ConfigColorChooser) {
            return ((ConfigColorChooser) configItem).getColor();
        }
        return null;
    }

    @Override
    protected boolean isProTeaserElementSelected(GameConfig config) {
        return config.isProTeaserPartSelected();
    }

    /**
     * Returns the supplier for the parts.
     *
     * @return The supplier for the parts.
     */
    public Function<String, Object> getPartSupplier() {
        return type -> {
            if (ConstantValue.CONFIG_BACKCOLOR.equals(type)) {
                return getSelectedBackgroundColor();
            } else {
                return getSelectedComboboxValue(type);
            }
        };
    }

    /**
     * Configuration item for choosing a color.
     */
    protected static class ConfigColorChooser implements ConfigItem {
        /**
         * The preference object.
         */
        private final HGBaseColorPreference preference;

        /**
         * Constructs a new instance.
         *
         * @param activity    The activity on which the color chooser is shown.
         * @param colorType   The type of the color.
         * @param activeColor The current active color of the type.
         * @noinspection deprecation
         */
        public ConfigColorChooser(PreferenceActivity activity, String colorType, Color activeColor) {
            this.preference = HGBaseConfigTools.createColorPreference(activity, colorType, activeColor);
        }

        /**
         * @noinspection deprecation
         */
        @Override
        public Preference getPreference() {
            return preference;
        }

        /**
         * @noinspection deprecation
         */
        @Override
        public String getOption() {
            return preference.getKey();
        }

        /**
         * Returns the currently set color.
         *
         * @return The currently set color.
         */
        public Color getColor() {
            return preference.getColor();
        }

        /**
         * Sets the current color.
         *
         * @param color The color to set.
         */
        public void setColor(Color color) {
            preference.setColor(color);
        }
    }
}
