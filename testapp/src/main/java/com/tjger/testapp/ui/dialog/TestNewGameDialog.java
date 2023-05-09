package com.tjger.testapp.ui.dialog;

import android.graphics.Bitmap;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.PlayerType;
import com.tjger.game.internal.PlayerFactory;
import com.tjger.gui.NewGameDialog;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.gui.config.HGBaseConfigTools;

public class TestNewGameDialog extends NewGameDialog {
    @Override
    public void createPlayerTypesPanel() {
        boolean showMultipleTypes = true;
        if (!showMultipleTypes) {
            super.createPlayerTypesPanel();
            return;
        }

        final GameConfig config = GameConfig.getInstance();
        PlayerType[] supportTypes = PlayerFactory.getInstance().getComputerPlayerTypes();
        String[] typeIds = getComputerPlayerTypesIds(supportTypes);
        Bitmap[] typeImages = getComputerPlayerTypesImages(supportTypes);

        // Add for each possible player a panel for the type.
        int maxPlayers = config.getMaxPlayers();
        for (int playerIndex = 0; playerIndex < maxPlayers; playerIndex++) {
            createPlayerTypePanel(playerIndex, typeIds, typeImages);
        }
    }

    /**
     * Creates a panel to select the type for the specified player.
     *
     * @param playerIndex The index of the player.
     * @param typeIds     The array with the ids of the types.
     * @param typeImages  The array with the images of the types.
     */
    private void createPlayerTypePanel(int playerIndex, String[] typeIds, Bitmap[] typeImages) {
        PlayerType defaultComputerType = PlayerFactory.getInstance().getDefaultComputerType();
        addPreference(HGBaseConfigTools.createListPreference(this, ConstantValue.CONFIG_COMPUTERTYPE + playerIndex, typeIds, typeImages, true, (defaultComputerType != null) ? defaultComputerType.getId() : typeIds[0], true));
    }
}
