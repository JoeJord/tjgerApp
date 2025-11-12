package com.tjger.gui.actions;

import android.view.MenuItem;

import com.tjger.MainFrame;
import com.tjger.game.completed.GameConfig;

import at.hagru.hgbase.lib.internal.billing.HGBaseBillingHelper;

/**
 * The action to start the flow to purchase the remove advertisement product.
 */
public class RemoveAdvertisementsAction extends AbstractTjgerMenuAction {
    /**
     * Constructs a new instance.
     *
     * @param mainFrame The main frame.
     */
    public RemoveAdvertisementsAction(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    public void perform(int id, MenuItem item) {
        if (getMainFrame().isAdvertisementAvailable()) {
            HGBaseBillingHelper.getInstance().launchPurchase(getMainFrame(), GameConfig.getInstance().getAdvertisementProductId());
        }
    }
}
