package com.tjger.gui.completed;

import java.util.ArrayList;
import java.util.List;

import com.tjger.game.GamePlayer;
import com.tjger.lib.ImageUtil;
import com.tjger.lib.PartUtil;
import com.tjger.lib.PlayerUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.HGBaseMultiTextPanel;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Creates a panel that displays the information of a single player.
 *
 * @author hagru
 */
public class PlayerInfoPanel extends FrameLayout {
	
	final static public int PANEL_HEIGHT = HGBaseMultiTextPanel.STATE_BAR_HEIGHT;

    final public static int TYPE = 1;    // the player's type image
    //final public static int ICON = 2;    // the player's image
    final public static int NAME = 4;    // the player's name, do not use with NAMETYPE
    final public static int TURN = 8;    // the score of the current turn
    final public static int ROUND = 16;  // the score of the current round
    final public static int SCORE = 32;  // the total score
    final public static int GAMES = 64;  // the number of played games
    final public static int WON = 128;   // the number of won games
    final public static int PIECE = 256;    // the player's piece color
    final public static int NAMETYPE = 512; // the player's name and type as one field, do not use with NAME

    private HGBaseMultiTextPanel pnPlayer;
    private int indexType = -1;
    private int indexPiece = -1;
    private int indexName = -1;
    private int indexNameType = -1;
    private int indexScoreTurn = -1;
    private int indexScoreRound = -1;
    private int indexScoreGame = -1;
    private int indexGames = -1;
    private int indexWon = -1;
    private final int fields;
	private final int textColor;
	private final boolean showInDialog; // if panel is shown in a dialog, extra space is needed

    public PlayerInfoPanel(Activity activity, GamePlayer player, int fields) {
        this(activity, player, fields, Color.WHITE);
    }

    public PlayerInfoPanel(Activity activity, GamePlayer player, int fields, int textColor) {
        this(activity, player, fields, textColor, false);
    }

    public PlayerInfoPanel(Activity activity, GamePlayer player, int fields, int textColor, boolean showInDialog) {
        super(activity);
        this.fields = fields;
        this.textColor = textColor;
        this.showInDialog = showInDialog;
        createRow(activity);
        setInformation(player);
    }
    
    /**
     * Returns the {@link HGBaseMultiTextPanel} that is the graphical component behind the info panel.<p>
     * NOTE: The returned panel should be used with care as this could cause unexpected behaviour.
     * 
     * @return the multi text panel that is the graphical component behind this info panel
     */
    public HGBaseMultiTextPanel getMultiTextPanel() {
        return pnPlayer;
    }

    /**
     * Looks which fields are displayed.
     *
     * @return The widths of the single panels.
     */
    private int[] getPanelWidths() {
        int imgWidth = HGBaseGuiTools.getFieldHeight() + 10;
        int showIndex = 0;
        List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(5));
        showIndex++;
        // for the player's type icon
        if ((fields&TYPE)==TYPE) {
            list.add(Integer.valueOf(imgWidth));
            indexType = showIndex;
            showIndex++;
        }
        // for the player's information
        if ((fields&PIECE)==PIECE) {
            list.add(Integer.valueOf(imgWidth));
            //list.add(Integer.valueOf(10));
            indexPiece = showIndex;
            showIndex+=2;
        }
        if ((fields&NAME)==NAME) {
            list.add(Integer.valueOf(0));
            indexName = showIndex;
            showIndex++;
        }
        if ((fields&NAMETYPE)==NAMETYPE) {
            list.add(Integer.valueOf(0));
            indexNameType = showIndex;
            showIndex++;
        }
        // for the statistics
        if ((fields&TURN)==TURN) {
            list.add(Integer.valueOf(50));
            indexScoreTurn = showIndex;
            showIndex++;
        }
        if ((fields&ROUND)==ROUND) {
            list.add(Integer.valueOf(50));
            indexScoreRound = showIndex;
            showIndex++;
        }
        if ((fields&SCORE)==SCORE) {
            list.add(Integer.valueOf(50));
            indexScoreGame = showIndex;
            showIndex++;
        }
        if ((fields&GAMES)==GAMES) {
            list.add(Integer.valueOf(32));
            indexGames = showIndex;
            showIndex++;
        }
        if ((fields&WON)==WON) {
            list.add(Integer.valueOf(32));
            indexWon = showIndex;
            showIndex++;
        }
        list.add(Integer.valueOf((showInDialog)? 65 : 5));
        showIndex++;
        return HGBaseTools.toIntArray(list);
    }

    /**
     * @param activity the activity where the info panel is displayed
     * @param index Index of the row
     */
    private void createRow(Activity activity) {
        int[] pnWidths = getPanelWidths();
        pnPlayer = new HGBaseMultiTextPanel(activity, pnWidths, false, false, textColor, Color.TRANSPARENT);
        setRightAlignment(indexScoreTurn);
        setRightAlignment(indexScoreRound);
        setRightAlignment(indexScoreGame);
        setRightAlignment(indexGames);
        setRightAlignment(indexWon);
        this.addView(pnPlayer);
    }

    /**
     * Sets the alignment of label for the given to right.
     *
     * @param index The label's index.
     */
    private void setRightAlignment(int index) {
        if (index >= 0) {
            //pnPlayer.getLabel(index).setTextAlignment(TEXT_ALIGNMENT_VIEW_END); API-17
        	pnPlayer.getLabel(index).setGravity(Gravity.END);
        }
    }

    /**
     * @param index Index of the row.
     * @param player The player who's info shell be set.
     * @param engine The game engine.
     */
    public void setInformation(GamePlayer player) {
        if (player == null) {
            resetInformation();
        } else {
            // get the piece color's first image
            Bitmap pieceImage = null;
            String playerColor = player.getPieceColor();
            Piece[] pieces = PartUtil.getPiecesWithColor(playerColor);
            if (pieces.length > 0) {
                pieceImage = pieces[0].getImage();
            } else {
                Card[] cards = PartUtil.getCardsWithColor(playerColor);
                if (cards.length > 0) {
                    pieceImage = cards[0].getImage();
                }
            }
            // get the name type string
            String nameType = PlayerUtil.getPlayerNameType(player);
            // set the information
            setInformation(pieceImage, player.getName(), nameType,
                           player.getType().getImage(), player.getScore(GamePlayer.SCORE_TURN),
                           player.getScore(GamePlayer.SCORE_ROUND), player.getScore(GamePlayer.SCORE_GAME),
                           player.getGamesPlayed(), player.getGamesWon());
        }
    }

    /**
     * @param i Index of the panel where the information shall be reseted.
     */
    public void resetInformation() {
        setInformation(null, "", "", null, HGBaseTools.INVALID_INT, HGBaseTools.INVALID_INT, HGBaseTools.INVALID_INT, 
                                                                    HGBaseTools.INVALID_INT, HGBaseTools.INVALID_INT);
    }

    /**
     * Set the player information.
     *
     * @param iconPiece Icon of the player's piece color.
     * @param playerName Name of the player.
     * @param nameType The name and the type of the player.
     * @param iconType Icon of the player's type.
     * @param scoreTurn Turn score.
     * @param scoreRound Round score.
     * @param scoreGame Game score.
     * @param gamesPlayed Number of played games.
     * @param gamesWon Number of won games.
     */
    private void setInformation(Bitmap iconPiece, String playerName, String nameType, Bitmap iconType, int scoreTurn, int scoreRound, int scoreGame, int gamesPlayed, int gamesWon) {
        if (indexPiece>=0) {
            ImageUtil.setImageOnLabel(pnPlayer.getLabel(indexPiece), iconPiece);
        }
        if (indexName>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexName), playerName);
        }
        if (indexNameType>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexNameType), nameType);
        }
        if (indexType>=0) {
            ImageUtil.setImageOnLabel(pnPlayer.getLabel(indexType), iconType);
        }
        if (indexScoreTurn>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexScoreTurn), getValueText(scoreTurn));
            HGBaseGuiTools.setToolTipText(pnPlayer.getLabel(indexScoreTurn), "tooltip_scoreturn");
        }
        if (indexScoreRound>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexScoreRound), getValueText(scoreRound));
            HGBaseGuiTools.setToolTipText(pnPlayer.getLabel(indexScoreRound), "tooltip_scoreround");
        }
        if (indexScoreGame>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexScoreGame), getValueText(scoreGame));
            HGBaseGuiTools.setToolTipText(pnPlayer.getLabel(indexScoreGame), "tooltip_scoregame");
        }
        if (indexGames>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexGames), getValueText(gamesPlayed));
            HGBaseGuiTools.setToolTipText(pnPlayer.getLabel(indexGames), "tooltip_gamesplayed");
        }
        if (indexWon>=0) {
            HGBaseGuiTools.setTextOnLabel(pnPlayer.getLabel(indexWon), getValueText(gamesWon));
            HGBaseGuiTools.setToolTipText(pnPlayer.getLabel(indexWon), "tooltip_gameswon");
        }
        pnPlayer.postInvalidate();
    }

    /**
     * @param value A int value.
     * @return A string showing this value.
     */
    private String getValueText(int value) {
        if (value == HGBaseTools.INVALID_INT) {
            return "";
        } else {
            return String.valueOf(value);
        }
    }

}
