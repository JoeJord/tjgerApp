package com.tjger.testapp.ui.frame;

import com.tjger.MainFrame;
import com.tjger.MainPanel;
import com.tjger.lib.GameConfigurationException;
import com.tjger.testapp.R;
import com.tjger.testapp.ui.frame.menu.TestMainMenu;
import com.tjger.testapp.ui.frame.menu.actions.OpenFacebookLinkAction;
import com.tjger.testapp.ui.frame.menu.actions.OpenTwitterLinkAction;
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
    protected void onCreatePreWelcome() {
        super.onCreatePreWelcome();
        setActionBarVisible(false);
    }

    @Override
    protected void registerOptionsMenuActions() {
        registerAction(TestMainMenu.MENU_ID_FACEBOOK, new OpenFacebookLinkAction(this));
        registerAction(TestMainMenu.MENU_ID_TWITTER, new OpenTwitterLinkAction(this));
    }

    @Override
    protected void onCreateDuringWelcome() {
        super.onCreateDuringWelcome();
        setPanels(new TestMainMenu(), new MainPanel(), new TestGamePanel(this), null);
    }

    @Override
    protected boolean isCreateDefaultStatusBarActive() {
        return false;
    }
}
