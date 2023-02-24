package com.tjger.gui.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tjger.MainFrame;
import com.tjger.R;
import com.tjger.game.completed.GameConfig;
import com.tjger.gui.GameDialogs;
import com.tjger.gui.completed.Arrangement;
import com.tjger.gui.completed.Background;
import com.tjger.gui.completed.BackgroundColor;
import com.tjger.gui.completed.Board;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.Cover;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PieceSet;
import com.tjger.lib.ConstantValue;

import java.util.ArrayList;
import java.util.List;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.config.HGBaseColorPreference;
import at.hagru.hgbase.gui.config.HGBaseConfigStateDialog;
import at.hagru.hgbase.gui.config.HGBaseConfigTools;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.IntBooleanStringMap;

/**
 * The dialog for choosing the game parts.
 *
 * @author hagru
 */
public class PartsDlg extends HGBaseConfigStateDialog implements OnSharedPreferenceChangeListener {
    private static final String ARRANGE_USERDEFINED_ID = "arrangement_userdefined";
    private static final int INDEX_BACKCOLOR = 0; // the index in the configColorList
    private static PreviewPanel pnPreview;
    private final IntBooleanStringMap indexMapStandard;
    private final IntBooleanStringMap indexMapUserDef;
    private final String[] userParts;
    private final String[] userPartSets;
    private final String[] cardSetTypes;
    private final String[] colorTypes;
    private PartsComboBox[] configComboList;
    private HGBaseColorPreference[] configColorList;
    private Color newBackColor;
    private boolean backColorDefined;
    private boolean completeArrangement;
    private boolean onChangeArrangement;

    public PartsDlg() {
        super(HGBaseText.getText("settings_parts").replace('.', ' '));
        this.onChangeArrangement = false;
        this.indexMapStandard = new IntBooleanStringMap();
        this.indexMapUserDef = new IntBooleanStringMap();
        // Get the types of the user defined parts
        GameConfig config = GameConfig.getInstance();
        cardSetTypes = config.getCardSetTypes();
        userParts = config.getPartTypes();
        userPartSets = config.getPartSetTypes();
        colorTypes = config.getColorTypes();
        // add a listener to react on changes of the preference
        HGBaseConfig.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected boolean canLeave(Preference preference, String s) {
        (new Handler()).post(this::setStateMessage); // The status message must be displayed with a slight delay, otherwise it cannot be determined correctly.
        return true;
    }

    /**
     * Sets the state message.
     */
    private void setStateMessage() {
        Integer warningMsg = getWarningMessage();
        if (warningMsg != null) {
            setWarnMessage(HGBaseText.getText(warningMsg));
        } else {
            setWarnMessage("");
        }
    }

    /**
     * Returns the message that should be displayed as a warning.
     *
     * @return The message that should be displayed as a warning.
     */
    private Integer getWarningMessage() {
        return getTeaserPartWarning();
    }

    /**
     * Returns the warning message if a teaser part is selected or {@code null} if no warning is needed.
     *
     * @return The warning message if a teaser part is selected or {@code null} if no warning is needed.
     */
    private Integer getTeaserPartWarning() {
        GameConfig gameConfig = GameConfig.getInstance();
        return (!gameConfig.isProVersion() && gameConfig.isProTeaserPartSelected()) ? R.string.teaser_part_warning : null;
    }

    @Override
    public void onBackPressed() {
        MainFrame.getInstance().checkNewGame(); // Check if it is allowed to start a new game with the current selection.
        super.onBackPressed();
    }

    @Override
    protected void createComponents() {
        onChangeArrangement = true;
        final GameConfig config = GameConfig.getInstance();
        completeArrangement = (config.isCompleteArrangement() && config.getArrangements().length > 0);
        List<PartsComboBox> cbList = new ArrayList<>();
        ArrayList<HGBaseColorPreference> ccList = new ArrayList<>();
        // get the standard combo boxes
        addComboBox(ConstantValue.CONFIG_ARRANGEMENT, config.getArrangements(), config.getActiveArrangement(), cbList, true);
        BackgroundColor backColor = config.getBackgroundColor();
        if (backColor != null) {
            backColorDefined = true;
            newBackColor = backColor.getActiveColor();
            addColorChooser(ConstantValue.CONFIG_BACKCOLOR, backColor.getDefaultColor(), backColor.getActiveColor(), ccList);
        }
        addComboBox(ConstantValue.CONFIG_BACKGROUND, config.getBackgrounds(), config.getActiveBackground(), cbList, true);
        addComboBox(ConstantValue.CONFIG_BOARD, config.getBoards(), config.getActiveBoard(), cbList, true);
        addComboBox(ConstantValue.CONFIG_COVER, config.getCovers(), config.getActiveCover(), cbList, true);

        for (String cardType : cardSetTypes) {
            addComboBox(cardType, config.getCardSets(cardType), config.getActiveCardSet(cardType), cbList, true);
        }
        addComboBox(ConstantValue.CONFIG_PIECESET, config.getPieceSets(), config.getActivePieceSet(), cbList, true);
        // get the user defined combo boxes
        for (String userPartSet : userPartSets) {
            addComboBox(userPartSet, config.getPartSets(userPartSet), config.getActivePartSet(userPartSet), cbList, false);
        }
        for (String userPart : userParts) {
            addComboBox(userPart, config.getParts(userPart), config.getActivePart(userPart), cbList, false);
        }
        // get the color choosers
        for (String colorType : colorTypes) {
            addColorChooser(colorType, config.getDefaultColor(colorType), config.getActiveColor(colorType), ccList);
        }
        // add the combo boxes and the color chooser to the panel
        configComboList = cbList.toArray(new PartsComboBox[0]);
        configColorList = ccList.toArray(new HGBaseColorPreference[0]);

        onChangeArrangement = false;
        changeArrangement();
        // the preview panel
        GameDialogs dlg = GameDialogFactory.getInstance().createGameDialogs(this);
        int previewWidth = getPreviewWidth();
        int previewHeight = getPreviewHeight();
        pnPreview = dlg.getPreviewPanel(this, previewWidth, previewHeight);
        HGBaseGuiTools.setBlackBorder(pnPreview);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (ConstantValue.CONFIG_ARRANGEMENT.equals(key)) {
            changeArrangement();
        } else {
            testChangedArrangement(getSelectedArrangement());
        }
        if (ConstantValue.CONFIG_BACKCOLOR.equals(key)) {
            newBackColor = HGBaseConfig.getColor(ConstantValue.CONFIG_BACKCOLOR);
        }
        if (pnPreview != null) {
            pnPreview.invalidate();
        }
    }

    /**
     * @return the width of the preview panel
     */
    private int getPreviewWidth() {
        double factor = HGBaseGuiTools.isScreenLandscape(this) ? 0.4 : 0.7;
        return (int) (HGBaseGuiTools.getScreenSize(this).x * factor);
    }

    /**
     * @return the height of the preview panel
     */
    private int getPreviewHeight() {
        return (int) (getPreviewWidth() * 0.75);
    }

    /**
     * Returns the panel's index of the given part type.
     *
     * @param id           The part type (either standard or user defined).
     * @param standardType <code>true</code> for a standard type, <code>false</code> for a user defined one.
     * @return The index or a number < 0.
     */
    public int indexOf(String id, boolean standardType) {
        if (standardType) {
            return (indexMapStandard.existsKey(id)) ? indexMapStandard.getInt(id) : -1;
        } else {
            return (indexMapUserDef.existsKey(id)) ? indexMapUserDef.getInt(id) : -1;
        }
    }

    /**
     * Returns the panel's index of the given color chooser type.
     *
     * @param id The color chooser type.
     * @return The index or a number < 0.
     */
    public int indexOfColorChooser(String id) {
        return indexMapUserDef.getInt(id);
    }

    /**
     * Change all parts depending on the current arrangement.
     */
    private void changeArrangement() {
        Arrangement a = getSelectedArrangement();
        if (a != null) {
            onChangeArrangement = true;
            newBackColor = a.getBackgroundColor();
            changePartOfArrangement(indexOf(ConstantValue.CONFIG_BACKGROUND, true), a.getBackground());
            changePartOfArrangement(indexOf(ConstantValue.CONFIG_BOARD, true), a.getBoard());
            changePartOfArrangement(indexOf(ConstantValue.CONFIG_PIECESET, true), a.getPieceSet());
            changePartOfArrangement(indexOf(ConstantValue.CONFIG_COVER, true), a.getCover());
            for (String cardSetType : cardSetTypes) {
                changePartOfArrangement(indexOf(cardSetType, true), a.getCardSet(cardSetType));
            }
            for (String userPart : userParts) {
                changePartOfArrangement(indexOf(userPart, false), a.getPart(userPart));
            }
            for (String userPartSet : userPartSets) {
                changePartOfArrangement(indexOf(userPartSet, false), a.getPartSet(userPartSet));
            }
            for (int i = 0; i < configColorList.length; i++) {
                Color c = getArrangementColorByIndex(a, i);
                if (c != null) {
                    configColorList[i].setColor(c);
                }
            }
            onChangeArrangement = false;
        }
    }

    /**
     * Returns the color specified in the given arrangement by the color index.
     * This may be also the background color, if one is defined.
     *
     * @param a     the arrangement
     * @param index the index of the color type
     * @return the color or null
     */
    private Color getArrangementColorByIndex(Arrangement a, int index) {
        if (backColorDefined && index == INDEX_BACKCOLOR) {
            return a.getBackgroundColor();
        } else {
            index = (backColorDefined) ? index - 1 : index;
            return a.getColor(colorTypes[index]);
        }
    }

    /**
     * @param index The index of the combo box.
     * @param part  The part to select.
     */
    private void changePartOfArrangement(int index, Part part) {
        if (index >= 0 && configComboList != null) {
            configComboList[index].setSelectedItem(part);
        }
    }

    /**
     * @return The selected arrangement.
     */
    public Arrangement getSelectedArrangement() {
        int indexCombo = indexOf(ConstantValue.CONFIG_ARRANGEMENT, true);
        if (indexCombo >= 0 && configComboList != null) {
            Object oSel = configComboList[indexCombo].getSelectedItem();
            if (oSel instanceof Arrangement) {
                return (Arrangement) oSel;
            }
        }
        return null;
    }

    /**
     * @return The selected Background.
     */
    public Background getSelectedBackground() {
        if (indexOf(ConstantValue.CONFIG_BACKGROUND, true) >= 0) {
            return (Background) getSelectedPart(indexOf(ConstantValue.CONFIG_BACKGROUND, true));
        } else {
            return null;
        }
    }

    /**
     * @return The selected Board.
     */
    public Board getSelectedBoard() {
        if (indexOf(ConstantValue.CONFIG_BOARD, true) >= 0) {
            return (Board) getSelectedPart(indexOf(ConstantValue.CONFIG_BOARD, true));
        } else {
            return null;
        }
    }

    /**
     * @param index Index of the combobox.
     * @return The selected item.
     */
    public Part getSelectedPart(int index) {
        return configComboList[index].getSelectedItem();
    }

    /**
     * Returns the selected color of the specified color chooser.
     *
     * @param index The index of the color chooser.
     * @return The selected color.
     */
    public Color getSelectedColor(int index) {
        return configColorList[index].getColor();
    }

    /**
     * @return The selected background color.
     */
    public Color getBackgroundColor() {
        return this.newBackColor;
    }

    /**
     * Adds a panel for choosing the active part.
     * If there exists no parts, no panel is added.
     * If the panel is added, the index is stored in index map with the id as key.
     *
     * @param id           The name of the parts' type (and id for the label).
     * @param parts        The array with the possible parts.
     * @param activePart   The active part of the given type.
     * @param cbList       The list to add the config combobox.
     * @param standardType True if a standard type is added, false for a user defined one.
     */
    private void addComboBox(String id, Part[] parts, Part activePart, List<PartsComboBox> cbList, boolean standardType) {
        parts = getOnlyVisible(parts);
        if (parts.length > 0) {
            // create the preference object
            PartsComboBox cbHelp = new PartsComboBox(id, parts, activePart);
            cbList.add(cbHelp);
            addPreference(cbHelp.getPreference());
            if (parts.length == 1) {
                cbHelp.getPreference().setEnabled(false);
            }
            if (completeArrangement && !id.equals(ConstantValue.CONFIG_ARRANGEMENT)) {
                cbHelp.getPreference().setEnabled(false);
            }
            // store the index of the new part
            IntBooleanStringMap indexMap = (standardType) ? indexMapStandard : indexMapUserDef;
            indexMap.set(id, cbList.size() - 1);
        }
    }

    /**
     * @param colorType    The color type.
     * @param defaultColor The default color, can be null.
     * @param activeColor  The current set color.
     * @param ccList       The list to add the config object.
     */
    private void addColorChooser(String colorType, Color defaultColor, Color activeColor, List<HGBaseColorPreference> ccList) {
        HGBaseColorPreference ccColor = HGBaseConfigTools.createColorPreference(this, colorType, activeColor);
        ccList.add(ccColor);
        addPreference(ccColor);
        // store the index of the new color chooser
        indexMapUserDef.set(colorType, ccList.indexOf(ccColor));
    }

    /**
     * @param parts An array with parts.
     * @return A new array with only visible parts.
     */
    private Part[] getOnlyVisible(Part[] parts) {
        List<Part> visibleList = new ArrayList<>();
        for (Part part : parts) {
            if (!part.isHidden()) {
                visibleList.add(part);
            }
        }
        return visibleList.toArray(new Part[0]);
    }

    /**
     * Test if the user has changed a part of the arrangement.
     *
     * @param arrange The arrangement to test.
     */
    protected void testChangedArrangement(Arrangement arrange) {
        if (!onChangeArrangement && indexOf(ConstantValue.CONFIG_ARRANGEMENT, true) >= 0 && !isGivenArrangement(arrange)) {
            GameConfig config = GameConfig.getInstance();
            int selIndex = -1;
            Arrangement[] all = config.getArrangements();
            for (int i = 0; all != null && i < all.length && selIndex == -1; i++) {
                if (isGivenArrangement(all[i])) {
                    selIndex = i;
                }
            }
            if (all == null || selIndex == -1) {
                configComboList[indexOf(ConstantValue.CONFIG_ARRANGEMENT, true)].setSelectedItem(null);
            } else {
                configComboList[indexOf(ConstantValue.CONFIG_ARRANGEMENT, true)].setSelectedItem(all[selIndex]);
            }
        }
    }

    /**
     * Test if the given arrangement is that one, that is selected.
     *
     * @param arrangement The arrangement to test.
     * @return True if the selected values fit to the given arrangement.
     */
    private boolean isGivenArrangement(Arrangement arrangement) {
        if (arrangement == null) {
            return false;
        }
        if (!belongsStandardPartToArrangement(arrangement)) {
            return false;
        }
        if (!belongsCardsetToArrangement(arrangement)) {
            return false;
        }
        if (!belongsUserPartToArrangement(arrangement)) {
            return false;
        }
        if (!belongsUserPartsetToArrangement(arrangement)) {
            return false;
        }
        return belongsColorToArrangement(arrangement);
    }

    /**
     * Returns {@code true} if all of the selected standard parts belong to the specified arrangement.
     *
     * @param arrangement The arrangement to check.
     * @return {@code true} if all of the selected standard parts belong to the specified arrangement.
     */
    private boolean belongsStandardPartToArrangement(Arrangement arrangement) {
        Background back = arrangement.getBackground();
        Board board = arrangement.getBoard();
        PieceSet piece = arrangement.getPieceSet();
        Cover cover = arrangement.getCover();
        return isPartOk(back, indexOf(ConstantValue.CONFIG_BACKGROUND, true)) && isPartOk(board, indexOf(ConstantValue.CONFIG_BOARD, true)) && isPartOk(piece, indexOf(ConstantValue.CONFIG_PIECESET, true)) && isPartOk(cover, indexOf(ConstantValue.CONFIG_COVER, true));
    }

    /**
     * Returns {@code true} if all of the selected card sets belong to the specified arrangement.
     *
     * @param arrangement The arrangement to check.
     * @return {@code true} if all of the selected card sets belong to the specified arrangement.
     */
    private boolean belongsCardsetToArrangement(Arrangement arrangement) {
        for (String cardSetType : cardSetTypes) {
            CardSet cards = arrangement.getCardSet(cardSetType);
            if (!isPartOk(cards, indexOf(cardSetType, true))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all of the selected user parts belong to the specified arrangement.
     *
     * @param arrangement The arrangement to check.
     * @return {@code true} if all of the selected user parts belong to the specified arrangement.
     */
    private boolean belongsUserPartToArrangement(Arrangement arrangement) {
        for (String userPart : userParts) {
            if (!isPartOk(arrangement.getPart(userPart), indexOf(userPart, false))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all of the selected user part sets belong to the specified arrangement.
     *
     * @param arrangement The arrangement to check.
     * @return {@code true} if all of the selected user part sets belong to the specified arrangement.
     */
    private boolean belongsUserPartsetToArrangement(Arrangement arrangement) {
        for (String userPartSet : userPartSets) {
            if (!isPartOk(arrangement.getPartSet(userPartSet), indexOf(userPartSet, false))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the selected color belongs to the specified arrangement.
     *
     * @param arrangement The arrangement to check.
     * @return {@code true} if the selected color belongs to the specified arrangement.
     */
    private boolean belongsColorToArrangement(Arrangement arrangement) {
        if (configColorList != null) {
            for (int i = 0; i < configColorList.length; i++) {
                Color c = getArrangementColorByIndex(arrangement, i);
                if (c != null && !c.equals(configColorList[i].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param arrangementPart A part of the arrangement.
     * @param index           The index of the combo box where the equals part should be selected.
     * @return False if there exists a part and this is not selected in the combo box.
     */
    private boolean isPartOk(Part arrangementPart, int index) {
        return arrangementPart == null || index <= 0 || arrangementPart.equals(configComboList[index].getSelectedItem());
    }

    /**
     * Holds the list preference (instead of a combo box) and stores all necessary parts data.
     */
    private class PartsComboBox {

        private final ListPreference preference;
        private final Part[] parts;

        public PartsComboBox(String id, Part[] parts, Part activePart) {
            this.parts = parts;
            String[] values = HGBaseTools.toStringIdArray(parts);
            String defaultValue = (activePart == null) ? "" : activePart.getId();
            this.preference = HGBaseConfigTools.createListPreference(PartsDlg.this, id, values, defaultValue, true);
        }

        /**
         * @return the selected part
         */
        public Part getSelectedItem() {
            String partId = preference.getValue();
            return (HGBaseTools.hasContent(partId)) ? HGBaseTools.findItemById(parts, partId) : null;
        }

        /**
         * @param partToSelect select the given part
         */
        public void setSelectedItem(Part partToSelect) {
            String id = (partToSelect == null) ? null : partToSelect.getId();
            preference.setValue(id);
            if (id == null && ConstantValue.CONFIG_ARRANGEMENT.equals(preference.getKey())) {
                HGBaseConfig.set(preference.getKey(), ARRANGE_USERDEFINED_ID);
            }
        }

        /**
         * @return the preference object for UI interaction
         */
        public ListPreference getPreference() {
            return preference;
        }
    }

}
