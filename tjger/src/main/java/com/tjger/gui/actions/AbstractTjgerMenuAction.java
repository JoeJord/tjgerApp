package com.tjger.gui.actions;

import com.tjger.MainFrame;
import com.tjger.MainMenu;

import at.hagru.hgbase.gui.menu.IMenuAction;

/**
 * An abstract menu action class for the tjger main menu.
 * 
 * @author hagru
 */
public abstract class AbstractTjgerMenuAction implements IMenuAction {
    
    private MainFrame mainFrame;

    protected AbstractTjgerMenuAction(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    /**
     * @return the tjger main menu
     */
    protected MainMenu getMainMenu() {
        return mainFrame.getMainMenu();
    }
    
    /**
     * @return the main frame
     */
    protected MainFrame getMainFrame() {
        return mainFrame;
    }
}
