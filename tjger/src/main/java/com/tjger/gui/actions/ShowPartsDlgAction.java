package com.tjger.gui.actions;

import com.tjger.MainFrame;
import com.tjger.gui.internal.PartsDlg;

import at.hagru.hgbase.gui.menu.actions.StartConfigDialogAction;

/**
 * Shows the parts dialog.
 * 
 * @author hagru
 */
public class ShowPartsDlgAction extends StartConfigDialogAction {
    
    public ShowPartsDlgAction(MainFrame mainFrame) {
        super(mainFrame, PartsDlg.class);
    }

}
