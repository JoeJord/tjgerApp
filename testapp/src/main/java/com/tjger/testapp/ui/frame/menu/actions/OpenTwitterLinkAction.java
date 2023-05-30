package com.tjger.testapp.ui.frame.menu.actions;

import android.app.Activity;
import android.view.MenuItem;

import com.tjger.MainFrame;
import com.tjger.gui.actions.AbstractTjgerMenuAction;

import at.hagru.hgbase.android.HGBaseAppTools;
import at.hagru.hgbase.android.LinkManager;

/**
 * Menu action to show Twitter link.
 *
 * @author Josef Jordan
 */
public class OpenTwitterLinkAction extends AbstractTjgerMenuAction {
    /**
     * The name of the Twitter link in the links file.
     */
    private static final String TWITTER_LINK_NAME = "Twitter";

    /**
     * Constructs a new instance.
     *
     * @param mainFrame The main frame.
     */
    public OpenTwitterLinkAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    /**
     * Opens the Twitter link.
     *
     * @param activity The activity that opens the link.
     */
    public static void openLink(Activity activity) {
        HGBaseAppTools.openLink(activity, LinkManager.getInstance().getLinkUrl(TWITTER_LINK_NAME));
    }

    /**
     * Returns {@code true} if the link should be shown in the current application version (pro or free).
     *
     * @param isProVersion Flag, if the application is currently the pro version.
     * @return {@code true} if the link should be shown in the current application version (pro or free).
     */
    public static boolean showLink(boolean isProVersion) {
        return LinkManager.showLink(isProVersion, LinkManager.getInstance().getLinkAppType(TWITTER_LINK_NAME));
    }

    @Override
    public void perform(int id, MenuItem item) {
        openLink(getMainFrame());
    }
}
