package com.tjger.gui.internal;

import java.io.IOException;

import com.tjger.R;
import com.tjger.game.completed.GameManager;
import com.tjger.gui.GameDialogs;
import com.tjger.lib.ConstantValue;

import android.app.Activity;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.HGBaseHTMLPageWebView;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Create a class for the hint panel. This is useful because this class can remember some states.
 * 
 * @author hagru
 */
public class HintPanel extends ScrollView {

    private GameDialogs gameDialog;
    private Uri[] hintUrls;
    private int numHints;
    private int indexShow;
    private TextView labelPosition;
    private CheckBox chkNotShowAgain;
    private boolean isNotShowAgain;
    private Button btNext;
    private Button btPrev;
    private HGBaseHTMLPageWebView htmlPage;

    /**
     * @param hintUrls an array with URLs to hints, must not be null
     */
    public HintPanel(GameDialogs gameDialog, Uri[] hintUrls) {
        super(gameDialog.getActivity());
        this.gameDialog = gameDialog;
        this.hintUrls = HGBaseTools.clone(hintUrls);
        this.numHints = hintUrls.length;
        Activity activity = gameDialog.getActivity();
        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
        														 ViewGroup.LayoutParams.MATCH_PARENT));
        addView(rootLayout);
        setFillViewport(true);
        // create the panels and buttons
        try {
            indexShow = gameDialog.getIndexOfIntialHint(numHints - 1);
            htmlPage = gameDialog.createHtmlPageForHint(hintUrls[indexShow]);
            rootLayout.addView(htmlPage);
            if (GameManager.getInstance().getGameConfig().getHintsSettingKeys().length > 0) {
	            chkNotShowAgain = HGBaseGuiTools.createCheckBox(activity, HGBaseText.getText("dlg_notshowagain"), null);            
	            isNotShowAgain = HGBaseConfig.getBoolean(ConstantValue.CONFIG_HINT_DONTSHOW, false);
	            chkNotShowAgain.setChecked(isNotShowAgain);
	            chkNotShowAgain.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						isNotShowAgain = isChecked;
					}
	            });
	            rootLayout.addView(chkNotShowAgain);
            }
            if (hintUrls.length > 1) {
                // create a dummy panel that allows resizing to full screen
                rootLayout.addView(new View(activity));
	            // create a navigation panel
	            LinearLayout pnNavigation = HGBaseGuiTools.createLinearLayout(activity, true);
	            pnNavigation.setGravity(Gravity.CENTER);
	            // the previous button
	            btPrev = HGBaseGuiTools.createButton(activity, null, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showAnotherPage(-1);
					}
				});
	            btPrev.setBackground(HGBaseResources.getDrawable(R.drawable.prev));
	            pnNavigation.addView(btPrev);
	            // show which hint is displayed
	            labelPosition = HGBaseGuiTools.createViewForMessage(activity, "");
	            pnNavigation.addView(labelPosition);
	            // the next button
	            btNext = HGBaseGuiTools.createButton(activity, null, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showAnotherPage(+1);
					}
				});
	            btNext.setBackground(HGBaseResources.getDrawable(R.drawable.next));
	            pnNavigation.addView(btNext);
	            rootLayout.addView(pnNavigation);
            }
            checkState();
        } catch (IOException e) {
            HGBaseLog.logError(e.getMessage());
        }
    }

    /**
     * Shows a next or previous page.
     * 
     * @param step The step from the current page (normally -1 or 1).
     */
    private void showAnotherPage(int step) {
        int newIndex = indexShow + step;
        if (newIndex >= 0 && newIndex < numHints) {
            try {
                gameDialog.showAnotherHtmlPageForHint(htmlPage, hintUrls[newIndex]);
                indexShow = newIndex;
            } catch (IOException e) {
                HGBaseLog.logWarn("Could not show hint: " + e.getMessage());
            }
            checkState();
        }
    }

    /**
     * Enables/disables buttons and shows the hint position.
     */
    private void checkState() {
    	if (labelPosition != null) {
	        labelPosition.setText(GameDialogs.SPACING + (indexShow + 1) + " / " + numHints + GameDialogs.SPACING);
	        btPrev.setEnabled(indexShow > 0);
	        btNext.setEnabled(indexShow < numHints - 1);
    	}
    }

    /**
     * @return true if the check box is selected that the hint shall not be shown again
     */
    public boolean isNotShowAgain() {
    	return isNotShowAgain;
    }
}