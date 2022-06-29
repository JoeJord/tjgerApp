package com.tjger.gui.internal;

import com.tjger.MainFrame;

import android.graphics.Color;
import android.view.Gravity;
import at.hagru.hgbase.HGBaseActivity;
import at.hagru.hgbase.gui.HGBaseAboutDlg;
import at.hagru.hgbase.gui.HGBaseDialog;
import at.hagru.hgbase.gui.HGBaseHTMLPageTextView;
import at.hagru.hgbase.lib.HGBaseText;

/**
 * Shows the tjger dialog.
 *
 * @author hagru
 */
public final class TjgerAboutDlg  {
	
	private static final String TJGER_LINK = "http://www.tjger.com";
	
	/**
	 * Prevent instantiation.
	 */
	private TjgerAboutDlg() {
		super();
	}

    public static void show(MainFrame pane) {
    	String html = buildHtmlContent(pane);
    	HGBaseHTMLPageTextView panel = new HGBaseHTMLPageTextView(pane, html);
    	panel.setGravity(Gravity.CENTER);
    	panel.setBackgroundColor(Color.WHITE);
        HGBaseDialog.showOkDialog(pane, panel, HGBaseText.getText("help_tjger"));
    }
    
	/**
	 * Build the HTML content from the standard values.
	 * 
	 * @param activity the corresponding activity
	 * @return the HTML content
	 * @see #show()
	 */
	protected static String buildHtmlContent(HGBaseActivity activity) {
		String htmlBody =
                "<html><body>" +
                "<a href=\"" + TJGER_LINK + "\">" + 
                "<img src=\"" + "tjger_about" + "\">" +
                "</a>"+
                HGBaseAboutDlg.getHtmlForLinkImages(activity) +
                "</body></html>";
        return htmlBody;
	}    
       
}
