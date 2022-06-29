package com.tjger.gui.actions;

import com.tjger.MainFrame;
import com.tjger.MainMenu;
import com.tjger.R;

import android.widget.Button;
import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.gui.config.HGBaseConfigMenuAction;

/**
 * Changes sound configuration by clicking on the menu entry. This menu should be displayed is icon in the action bar.
 * 
 * @author hagru
 */
public class SoundConfigurationAction extends HGBaseConfigMenuAction {

    public SoundConfigurationAction(MainFrame activity) {
        super(activity, MainMenu.MENU_ID_SETTINGS_SOUND, R.drawable.sound_on, R.drawable.sound_off);
    }
    
    @Override
    public void setIconByConfiguration() {
        super.setIconByConfiguration();
        MainMenu mainMenu = ((MainFrame) getActivity()).getMainMenu();
        if (mainMenu != null) {
            Button btSound = mainMenu.getSoundSettingsButton();
            if (btSound != null) {
                int imageId = getCurrentConfiguration() ? getIconOn() : getIconOff();
                btSound.setBackground(HGBaseResources.getDrawable(imageId));
            }
        }
    }

}
