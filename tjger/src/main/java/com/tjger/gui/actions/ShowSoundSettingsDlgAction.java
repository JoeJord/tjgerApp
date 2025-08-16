package com.tjger.gui.actions;

import com.tjger.MainFrame;
import com.tjger.gui.internal.SoundsDlg;

import at.hagru.hgbase.gui.menu.actions.StartConfigDialogAction;

/**
 * Shows the sounds dialog.
 */
public class ShowSoundSettingsDlgAction extends StartConfigDialogAction {
    /**
     * Constructs a new instance.
     *
     * @param mainFrame The main frame.
     */
    public ShowSoundSettingsDlgAction(MainFrame mainFrame) {
        super(mainFrame, SoundsDlg.class);
    }
}
