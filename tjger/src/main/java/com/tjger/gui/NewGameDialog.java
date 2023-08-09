package com.tjger.gui;

import android.graphics.Bitmap;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Pair;

import com.tjger.MainFrame;
import com.tjger.R;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.PlayerManager;
import com.tjger.game.completed.PlayerType;
import com.tjger.game.internal.PlayerFactory;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.Piece;
import com.tjger.lib.ConstantValue;
import com.tjger.lib.PartUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import at.hagru.hgbase.gui.config.HGBaseConfigStateDialog;
import at.hagru.hgbase.gui.config.HGBaseConfigTools;
import at.hagru.hgbase.gui.config.HGBaseNumberPickerPreference;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The dialog for starting a new game.<p>
 * This dialog is not shown before starting a new game, but is thought for basic settings for the (next) new games.
 * This dialog allows you to choose the players, extend it for more options.
 *
 * @author hagru
 */
public class NewGameDialog extends HGBaseConfigStateDialog {
    /**
     * Select the number of players.
     */
    protected HGBaseNumberPickerPreference sliderNumPlayers;
    /**
     * Select the number of human players.
     */
    protected HGBaseNumberPickerPreference sliderNumHumans;
    /**
     * Allow to change the name of the (one) human player.
     */
    protected EditTextPreference tfPlayerName;
    /**
     * Allow to change the piece color of the first human player.
     */
    protected ListPreference cbPlayerPieceColor;
    /**
     * Select the computer type (difficulty).
     */
    protected ListPreference cbComputerType;

    public NewGameDialog() {
        super(HGBaseText.getText("dlg_newgame"));
    }

    /**
     * Resets the state of the current game.
     */
    protected void resetGameState() {
        File saveFile = MainFrame.getInstance().getAutosaveFile();
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
            MainFrame.getInstance().checkResumeGame();
        }
    }

    /**
     * Returns {@code true} if the value of the preference is not valid.
     *
     * @param pref The preference that has been changed.
     * @param value The new value for the preference (as {@code String}.
     * @return {@code true} if the value of the preference is not valid.
     */
    protected boolean isValidValue(@NonNull Preference pref, String value) {
        boolean valid = true;
        if (pref.equals(tfPlayerName)) {
            valid = isPlayerNameValid(value);
        }
        return valid;
    }

    /**
     * Returns {@code true} if the specified player name is valid.
     *
     * @param value The value to check.
     * @return {@code true} if the specified player name is valid.
     */
    protected boolean isPlayerNameValid(String value) {
        return !isPlayerNameEmpty(value) && !hasPlayerNameInvalidChar(value);
    }

    /**
     * Returns {@code true} if the specified player name is empty.
     *
     * @param value The value to check.
     * @return {@code true} if the specified player name is empty.
     */
    protected boolean isPlayerNameEmpty(String value) {
        return !HGBaseTools.hasContent(value);
    }

    /**
     * Returns {@code true} if the specified player name contains invalid chars.
     *
     * @param value The value to check.
     * @return {@code true} if the specified player name contains invalid chars.
     */
    protected boolean hasPlayerNameInvalidChar(String value) {
        if (value == null) {
            return false;
        }
        for (String invalidChar : ConstantValue.getNetworkSpecialChars()) {
            if (value.contains(invalidChar)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean canLeave(Preference pref, String value) {
        // reset game state if a preference has changed, because a new game has to be started for such changes
        resetGameState();
        (new Handler()).post(this::setStateMessage); // The status message must be displayed with a slight delay, otherwise it cannot be determined correctly.
        return isValidValue(pref, value);
    }

    /**
     * Sets the state message.
     */
    protected void setStateMessage() {
        String errorMsg = getErrorMessage();
        if (errorMsg != null) {
            setErrorMessage(errorMsg);
            return;
        }
        String warningMsg = getWarningMessage();
        if (warningMsg != null) {
            setWarnMessage(warningMsg);
            return;
        }
        String infoMsg = getInfoMessage();
        if (infoMsg != null) {
            setInfoMessage(infoMsg);
            return;
        }
        setInfoMessage("");
    }

    /**
     * Returns the message that should be displayed as an error or {@code null} if no error is needed.
     *
     * @return The message that should be displayed as an error or {@code null} if no error is needed.
     */
    protected String getErrorMessage() {
        return getPlayerNameErrorMessage();
    }

    /**
     * Returns the error message if the player name is not correct or {@code null} if not error is needed.
     *
     * @return The error message if the player name is not correct or {@code null} if not error is needed.
     */
    protected String getPlayerNameErrorMessage() {
        String playerName = GameConfig.getPlayerName(0);
        if (isPlayerNameEmpty(playerName)) {
            return HGBaseText.getText(R.string.err_neednames);
        }
        if (hasPlayerNameInvalidChar(playerName)) {
            return HGBaseText.getText(R.string.err_namewrong);
        }
        return null;
    }

    /**
     * Returns the message that should be displayed as a warning or {@code null} if no warning is needed.
     *
     * @return The message that should be displayed as a warning or {@code null} if no warning is needed.
     */
    protected String getWarningMessage() {
        return getTeaserWarning();
    }

    /**
     * Returns the warning message if a teaser item is selected or {@code null} if no warning is needed.
     *
     * @return The warning message if a teaser item is selected or {@code null} if no warning is needed.
     */
    protected String getTeaserWarning() {
        GameConfig gameConfig = GameConfig.getInstance();
        return (!gameConfig.isProVersion() && PlayerManager.getInstance().isProTeaserPlayerTypeSelected()) ? HGBaseText.getText(R.string.teaser_player_warning) : null;
    }

    /**
     * Returns the message that should be displayed as an information or {@code null} if no information is needed.
     *
     * @return The message that should be displayed as an information or {@code null} if no information is needed.
     */
    protected String getInfoMessage() {
        return null;
    }

    @Override
    protected void createComponents() {
        createDefaultPanel();
    }

    /**
     * Creates the default panel that is shown for basic game settings.<p>
     * To be overwritten for adding additional basic game settings (e.g. rule settings).
     */
    protected void createDefaultPanel() {
        // by default there is only a player panel
        createPlayerPanel();
    }

    /**
     * Create the player panel.
     */
    protected void createPlayerPanel() {
        final GameConfig config = GameConfig.getInstance();
        final PlayerManager manager = PlayerManager.getInstance();
        // panel for number of players and number of human players
        createNumberPlayersPanel(config);
        // panel for player names and types
        createPlayerNamesPanel(manager);
        // panel for changing the piece color of the human player
        createPlayerColorPanel();
        // panel for the computer types
        createPlayerTypesPanel();
    }

    /**
     * Create the player panel only if there is a difference between minimum and maximum number of players.<p>
     * Create the human player panel only if more than one player is allowed.
     *
     * @param config The game configuration
     */
    protected void createNumberPlayersPanel(final GameConfig config) {
        int min = config.getMinPlayers();
        int max = config.getMaxPlayers();
        if (min < max) {
            sliderNumPlayers = (HGBaseNumberPickerPreference) createNumPlayersPreference(min, max, config.getDefaultPlayers());
            addPreference(sliderNumPlayers);
        }
        if (!config.isOneHumanPlayer()) {
            sliderNumHumans = HGBaseConfigTools.createNumberPickerPreference(this, ConstantValue.CONFIG_NUMHUMANS, config.getMinHumanPlayers(), max, 1, config.getDefaultHumanPlayers(), true);
            addPreference(sliderNumHumans);
        }
    }

    /**
     * Creates the preference to select the number of players.
     *
     * @param min          The minimum number of players.
     * @param max          The maximum number of players.
     * @param defaultValue The default number of players.
     * @return The preference to select the number of players.
     */
    protected Preference createNumPlayersPreference(int min, int max, int defaultValue) {
        return HGBaseConfigTools.createNumberPickerPreference(this, ConstantValue.CONFIG_NUMPLAYERS, min, max, 1, defaultValue, true);
    }

    /**
     * Create only a panel to change the name of the one human player.
     *
     * @param manager the player manager
     */
    protected void createPlayerNamesPanel(PlayerManager manager) {
        tfPlayerName = (EditTextPreference) createHumanPlayerNamePreference(manager.getDefaultHumanPlayerName());
        addPreference(tfPlayerName);
    }

    /**
     * Creates the preference to enter the name of the human player.
     *
     * @param defaultName The default name for the human player.
     * @return The preference to enter the name of the human player.
     */
    protected Preference createHumanPlayerNamePreference(String defaultName) {
        return HGBaseConfigTools.createTextPreference(this, ConstantValue.CONFIG_PLAYERNAME, defaultName, true, true);
    }

    /**
     * Create a panel to allow for the human player selecting a piece (or card) color.
     */
    protected void createPlayerColorPanel() {
        Pair<List<String>, List<Bitmap>> playerColors = getImageList();
        if (playerColors.first.size() > 1) {
            String[] colorNames = playerColors.first.toArray(new String[0]);
            Bitmap[] colorImages = playerColors.second.toArray(new Bitmap[0]);
            cbPlayerPieceColor = HGBaseConfigTools.createListPreference(this, ConstantValue.CONFIG_PLAYERPIECECOLOR, colorNames, colorImages, colorNames[0]);
            addPreference(cbPlayerPieceColor);
        }
    }

    /**
     * @param color the piece/card color
     * @return the image id or null
     */
    private Pair<String, Bitmap> getImage(String color) {
        Piece[] pieces = PartUtil.getPiecesWithColor(color);
        if (pieces.length > 0) {
            return new Pair<>(color, pieces[0].getImage());
        } else {
            Card[] cards = PartUtil.getCardsWithColor(color);
            if (cards.length > 0) {
                return new Pair<>(color, cards[0].getImage());
            } else {
                return null;
            }
        }
    }

    /**
     * @return a list with piece/card images, where as the string is the id
     */
    protected Pair<List<String>, List<Bitmap>> getImageList() {
        boolean found = true;
        int index = 0;
        List<String> nameList = new ArrayList<>();
        List<Bitmap> bitmapList = new ArrayList<>();
        String pieceColor = GameConfig.getInstance().getPlayerPieceColor();
        while (found) {
            Pair<String, Bitmap> newImg = getImage(pieceColor + index);
            if (newImg != null) {
                nameList.add(newImg.first);
                bitmapList.add(newImg.second);
            } else {
                found = false;
            }
            index++;
        }
        return new Pair<>(nameList, bitmapList);
    }

    /**
     * Create a panel to select the difficulty by taking the possible computer types.
     * The panel will only be created if more than one computer types are available.
     */
    public void createPlayerTypesPanel() {
        PlayerType[] supportTypes = PlayerFactory.getInstance().getComputerPlayerTypes();
        if (supportTypes.length > 1) {
            String[] typeIds = getComputerPlayerTypesIds(supportTypes);
            Bitmap[] typeImages = getComputerPlayerTypesImages(supportTypes);
            cbComputerType = HGBaseConfigTools.createListPreference(this, ConstantValue.CONFIG_COMPUTERTYPE, typeIds, typeImages, true, typeIds[0]);
            addPreference(cbComputerType);
        }
    }

    /**
     * Returns an array with the ids of the supported computer player types.
     *
     * @return An array with the ids of the supported computer player types.
     */
    protected String[] getComputerPlayerTypesIds() {
        PlayerType[] supportTypes = PlayerFactory.getInstance().getComputerPlayerTypes();
        return getComputerPlayerTypesIds(supportTypes);
    }

    /**
     * Returns an array with the ids of the supported computer player types.
     *
     * @param supportTypes An array of the supported computer player types.
     * @return An array with the ids of the supported computer player types.
     */
    protected String[] getComputerPlayerTypesIds(PlayerType[] supportTypes) {
        return HGBaseTools.toStringIdArray(supportTypes);
    }

    /**
     * Returns an array with the images of the supported computer player type.
     *
     * @return An array with the images of the supported computer player type.
     */
    protected Bitmap[] getComputerPlayerTypesImages() {
        PlayerType[] supportTypes = PlayerFactory.getInstance().getComputerPlayerTypes();
        return getComputerPlayerTypesImages(supportTypes);
    }

    /**
     * Returns an array with the images of the supported computer player type.
     *
     * @param supportTypes An array of the supported computer player types.
     * @return An array with the images of the supported computer player type.
     */
    protected Bitmap[] getComputerPlayerTypesImages(PlayerType[] supportTypes) {
        Bitmap[] typeImages = new Bitmap[supportTypes.length];
        for (int i = 0; i < supportTypes.length; i++) {
            typeImages[i] = supportTypes[i].getImage();
        }
        return typeImages;
    }

    @Override
    protected void okPressed() {
        PlayerManager.getInstance().initPlayers();
    }

    @Override
    public void onBackPressed() {
        MainFrame.getInstance().checkNewGame(); // Check if it is allowed to start a new game with the current selection.
        super.onBackPressed();
    }
}
