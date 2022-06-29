package com.tjger.gui.internal;

import com.tjger.gui.GameDialogs;
import com.tjger.gui.NewGameDialog;
import com.tjger.lib.ConstantValue;

import android.app.Activity;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.ClassFactory;

/**
 * The factory creates the new game dialog.
 *
 * @author hagru
 */
public class GameDialogFactory {

    private static GameDialogFactory factory = new GameDialogFactory();
    private String dialogClassNewGame;
    private String dialogClassGameDialogs;

    private GameDialogFactory() {
        super();
    }

    /**
     * @return The one and only instance of the game dialog factory.
     */
    public static GameDialogFactory getInstance() {
        return factory;
    }

    /**
     * @param classPath Path to the class for the new game dialog.
     * @return True, if there exists a valid dialog class.
     */
    public boolean setNewGameDialogClass(String classPath) {
        if (ClassFactory.existsClass(classPath)) {
            this.dialogClassNewGame = classPath;
            return true;
        } else {
        	return false;
        }
    }

    /**
     * @param classPath Path to the class for the new game dialog.
     * @return The new dialog or null.
     */
    private NewGameDialog createNewDialog(String classPath) {
        return ClassFactory.createClass(classPath, NewGameDialog.class, "new game dialog");
    }

    /**
     * @return A new game dialog.
     */
    public NewGameDialog createNewDialog() {
        if (!HGBaseTools.hasContent(dialogClassNewGame)) {
            dialogClassNewGame = ConstantValue.STANDARD_NEWGAMEDIALOG;
        }
        return createNewDialog(dialogClassNewGame);
    }

    /**
     * @param classPath Path to the class for the game dialog class.
     * @return True, if there exists a valid dialog class.
     */
    public boolean setGameDialogsClass(String classPath) {
		if (ClassFactory.existsClass(classPath)) {
			this.dialogClassGameDialogs = classPath;
			return true;
		} else {
		    return false;
		}
    }

    /**
     * @param classPath The classpath to the game dialog.
     * @param activity the activity the dialog is shown from
     * @return A GameDialogs class if classPath is ok.
     */
    private GameDialogs createGameDialogs(String classPath, Activity activity) {
    	GameDialogs dialogs = ClassFactory.createClass(classPath, GameDialogs.class, "game dialogs");
    	if (activity != null) {
    	    dialogs.setActivity(activity);
    	}
    	return dialogs;
    }

    /**
     * @param activity the activity the dialog is shown from
     * @return A game dialogs class.
     */
    public GameDialogs createGameDialogs(Activity activity) {
		if (dialogClassGameDialogs == null) {
			dialogClassGameDialogs = ConstantValue.STANDARD_GAMEDIALOGS;
		}
        return createGameDialogs(dialogClassGameDialogs, activity);
    }

}
