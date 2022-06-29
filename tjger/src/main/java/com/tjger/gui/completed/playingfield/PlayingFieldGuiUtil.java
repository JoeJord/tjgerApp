package com.tjger.gui.completed.playingfield;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.PlayingFieldManager;

import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.gui.config.HGBaseConfigTools;

/**
 * Utility classes for gui related actions related to playing fields that are not covered by the painting 
 * utility class {@link PlayingFieldPaintUtil}.
 * 
 * @author hagru
 */
public final class PlayingFieldGuiUtil {

    /**
     * Avoid instantiation of this class.
     */
    public PlayingFieldGuiUtil() {
        super();
    }

    /**
     * Sets the size of the main panel based on the playing field and updates the zoom factor.
     * 
     * @param playingField the playing field to get the size from
     * @param forceUpdate true to update also if the given playing field is null (size will be zero)
     */
    public static void setPanelSizeByPlayingField(PlayingField playingField, boolean forceUpdate) {
        if (playingField != null || forceUpdate) {
            GameConfig config = GameConfig.getInstance();
            Dimension size = (playingField == null)? new Dimension(0, 0) : playingField.getSize();
            config.setFieldWidth(size.width);
            config.setFieldHeight(size.height);
            GameManager.getInstance().getMainFrame().getMainMenu().refreshMainPanel();
        }
    }
    
    /**
     * Creates a configuration combo box with all playing fields. If there is only one field available, the 
     * combo box will be disabled.
     * 
     * @param activity the preference activity
     * @param key the option key for the configuration
     * @param defaultField the default field that shall be selected if not already set by configuration
     * @return the new created configuration combo box
     */
    public static ListPreference createConfigComboBox(PreferenceActivity activity, String key, String defaultField) {
        String[] availableFields = PlayingFieldManager.getInstance().getFieldNames();
        ListPreference cbPlayingField = HGBaseConfigTools.createListPreference(activity, key, availableFields, defaultField);
        cbPlayingField.setEnabled(availableFields.length > 1);
        return cbPlayingField;
    }

}
