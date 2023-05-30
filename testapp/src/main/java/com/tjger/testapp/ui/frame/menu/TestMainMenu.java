package com.tjger.testapp.ui.frame.menu;

import com.tjger.MainMenu;
import com.tjger.testapp.R;
import com.tjger.testapp.ui.frame.menu.actions.OpenFacebookLinkAction;
import com.tjger.testapp.ui.frame.menu.actions.OpenTwitterLinkAction;

public class TestMainMenu extends MainMenu {
    /**
     * The menu id of the Facebook menu item.
     */
    public static final String MENU_ID_FACEBOOK = "link_facebook";
    /**
     * The menu id of the Twitter menu item.
     */
    public static final String MENU_ID_TWITTER = "link_twitter";

    @Override
    protected void createButtonsInsteadOfActionBar() {
        super.createButtonsInsteadOfActionBar();
        if ((isVisible()) && (!isActionBarVisible())) {
            int iconIndex = 2;
            boolean isProVersion = getMainFrame().isProVersion();
            createMenuIcon(MENU_ID_HELP_INSTRUCTIONS, R.drawable.help_instructions, iconIndex++, v -> showGameInstructionsDialog());
            if (OpenFacebookLinkAction.showLink(isProVersion)) {
                createMenuIcon(MENU_ID_FACEBOOK, R.drawable.facebook, iconIndex++, v -> OpenFacebookLinkAction.openLink(getMainFrame()));
            }
            if (OpenTwitterLinkAction.showLink(isProVersion)) {
                createMenuIcon(MENU_ID_TWITTER, R.drawable.twitter, iconIndex, v -> OpenTwitterLinkAction.openLink(getMainFrame()));
            }
        }
    }
}
