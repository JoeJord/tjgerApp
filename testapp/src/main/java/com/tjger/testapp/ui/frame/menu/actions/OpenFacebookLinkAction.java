package com.tjger.testapp.ui.frame.menu.actions;

import android.app.Activity;
import android.view.MenuItem;

import com.tjger.MainFrame;
import com.tjger.gui.actions.AbstractTjgerMenuAction;

import at.hagru.hgbase.android.HGBaseAppTools;
import at.hagru.hgbase.android.LinkManager;

/**
 * Menu action to show Facebook link.
 *
 * @author Josef Jordan
 */
public class OpenFacebookLinkAction extends AbstractTjgerMenuAction {
    /**
     * The name of the Facebook link in the links file.
     */
    private static final String FACEBOOK_LINK_NAME = "Facebook";

    /**
     * Constructs a new instance.
     *
     * @param mainFrame The main frame.
     */
    public OpenFacebookLinkAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    /**
     * Opens the Facebook link.
     *
     * @param activity The activity that opens the link.
     */
    public static void openLink(Activity activity) {
        HGBaseAppTools.openLink(activity, LinkManager.getInstance().getLinkUrl(FACEBOOK_LINK_NAME));
    }

    /**
     * Returns {@code true} if the link should be shown in the current application version (pro or free).
     *
     * @param isProVersion Flag, if the application is currently the pro version.
     * @return {@code true} if the link should be shown in the current application version (pro or free).
     */
    public static boolean showLink(boolean isProVersion) {
        return LinkManager.showLink(isProVersion, LinkManager.getInstance().getLinkAppType(FACEBOOK_LINK_NAME));
    }

    @Override
    public void perform(int id, MenuItem item) {
        openLink(getMainFrame());
    }
}
