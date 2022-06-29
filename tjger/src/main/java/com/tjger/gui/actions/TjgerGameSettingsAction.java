package com.tjger.gui.actions;

import com.tjger.MainFrame;

import android.view.MenuItem;

/**
 * Show the "new game dialog" to set parameters for new games. 
 */
public class TjgerGameSettingsAction extends AbstractTjgerMenuAction {

	public TjgerGameSettingsAction(MainFrame mainFrame) {
		super(mainFrame);
	}

	@Override
	public void perform(int id, MenuItem item) {
		getMainMenu().showNewGameDialog();
	} 

}
