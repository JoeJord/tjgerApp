package com.tjger.testapp.ui.frame;

import com.tjger.MainFrame;
import com.tjger.MainMenu;
import com.tjger.MainPanel;
import com.tjger.lib.GameConfigurationException;
import com.tjger.testapp.R;
import com.tjger.testapp.ui.game.TestGamePanel;

public class TestMainFrame extends MainFrame {
    /**
     * Constructs a new instance.
     *
     * @throws GameConfigurationException if the configuration is not valid
     */
    public TestMainFrame() throws GameConfigurationException {
        super(R.menu.main);
    }

    @Override
    protected void registerOptionsMenuActions() {
        // Nothing to do
    }

    @Override
    protected void onCreateDuringWelcome() {
        super.onCreateDuringWelcome();
        setPanels(new MainMenu(), new MainPanel(), new TestGamePanel(this), null);
    }
}
