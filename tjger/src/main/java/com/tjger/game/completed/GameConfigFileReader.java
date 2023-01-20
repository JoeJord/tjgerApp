package com.tjger.game.completed;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.completed.imagereader.AbstractImageEffectReader;
import com.tjger.game.completed.imagereader.ImageReflectionReader;
import com.tjger.game.completed.imagereader.ImageShodawReader;
import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.game.completed.playingfield.PlayingFieldFileOperator;
import com.tjger.game.completed.playingfield.PlayingFieldManager;
import com.tjger.game.internal.PlayerFactory;
import com.tjger.game.internal.RulesFactory;
import com.tjger.game.internal.StateFactory;
import com.tjger.gui.completed.Arrangement;
import com.tjger.gui.completed.Background;
import com.tjger.gui.completed.BackgroundColor;
import com.tjger.gui.completed.Board;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.ColorValuePart;
import com.tjger.gui.completed.Cover;
import com.tjger.gui.completed.ImageEffect;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.Piece;
import com.tjger.gui.completed.PieceSet;
import com.tjger.gui.internal.GameDialogFactory;
import com.tjger.lib.ConstantValue;

import android.graphics.Bitmap;

import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.lib.HGBaseFileTools;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.ClassFactory;
import at.hagru.hgbase.lib.internal.IntBooleanStringMap;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Helper class for reading the configuration file.<p<
 * THe file has to be named "tjger.xml" and placed into the /res/raw directory.
 *
 * @author Harald
 */
class GameConfigFileReader {

    private static final String TJGER_FILE_NAME = "tjger"; // the file extension (.xml) is added automatically by Android

    private static final String CONFIG_TJGER = "tjger";
    static final String CONFIG_NAME = "name";
    static final String CONFIG_IMAGE = "image";
    private static final String CONFIG_CLASS = "class";
    private static final String CONFIG_APP = "app";
    private static final String CONFIG_PRO = "pro";
    private static final String CONFIG_FULLSCREENMODE = "fullscreenmode";
    private static final String CONFIG_ADVERTISEMENTS = "advertisements";
    private static final String CONFIG_ADVERTISEMENTACTIVE = "active";
    private static final String CONFIG_ADVERTISEMENTURL = "url";
    private static final String CONFIG_ADVERTISEMENTERRORPAGEURL = "errorpageurl";
    private static final String CONFIG_ADVERTISEMENTWIDTHPERCENT = "widthpercent";
    private static final String CONFIG_ADVERTISEMENTHEIGHTPERCENT = "heightpercent";
    private static final String CONFIG_SETTINGS = "settings";
    private static final String CONFIG_RULES = "rules";
    private static final String CONFIG_HINTS = "hints";
    private static final String CONFIG_NETWORK = "network";
    private static final String CONFIG_PORT = "port";
    private static final String CONFIG_LOCAL = "local";
    private static final String CONFIG_GAMESTATE = "gamestate";
    private static final String CONFIG_XML_ROOT = "xmlroot";
    private static final String CONFIG_GAMES = "games";
    private static final String CONFIG_GAME = "game";
    private static final String CONFIG_ROUND = "round";
    private static final String CONFIG_TURN = "turn";
    private static final String CONFIG_MOVE = "move";
    private static final String CONFIG_NEWGAME = "newgame";
    private static final String CONFIG_NEWROUND = "newround";
    private static final String CONFIG_NEWTURN = "newturn";
    private static final String CONFIG_AFTERMOVE = "aftermove";
    private static final String CONFIG_GAMEDIALOGS = "gamedialogs";
    private static final String CONFIG_INFODIALOGS = "infodialogs";
    private static final String CONFIG_NEWGAMEDIALOG = "newgamedialog";
    private static final String CONFIG_CHANGED = "changed";
    private static final String CONFIG_STATISTICS = "statistics";
    private static final String CONFIG_SCORES = "scores";
    private static final String CONFIG_HIGHSCORE = "highscore";
    private static final String CONFIG_ONLYFIRST = "onlyfirst";
    private static final String CONFIG_LOWERSCOREISBETTER = "lowerscoreisbetter";
    private static final String CONFIG_SCROLLWHEN = "scrollwhen";
    private static final String CONFIG_ZOOM = "zoom";
    private static final String CONFIG_IGNOREZOOM = "ignorezoom";
    private static final String CONFIG_PATH = "path";
    static final String CONFIG_TYPE = "type";
    static final String CONFIG_VALUE = "value";
    private static final String CONFIG_FALSE = "false";
    private static final String CONFIG_TRUE = "true";
    private static final String CONFIG_DEFAULT = "default";
    private static final String CONFIG_DELAY = "delay";
    private static final String CONFIG_INTERRUPT = "interrupt";
    private static final String CONFIG_HIDDEN = "hidden";
    private static final String CONFIG_ORDERBY = "orderby";
    private static final String CONFIG_MIN = "min";
    private static final String CONFIG_MAX = "max";
    private static final String CONFIG_ORDER = "order";
    private static final String CONFIG_CLOCKWISE = "clockwise";
    private static final String CONFIG_COUNTERCLOCKWISE = "counterclockwise";
    private static final String CONFIG_ANTICLOCKWISE = "anticlockwise";
    private static final String CONFIG_EXTENSION = "extension";
    private static final String CONFIG_COMPLETE = "complete";
    private static final String CONFIG_PLAYERS = "players";
    private static final String CONFIG_ONEHUMAN = "onehuman";
    private static final String CONFIG_WITHOUTHUMAN = "withouthuman";
    private static final String CONFIG_PLAYER = "player";
    private static final String CONFIG_GAMEFIELD = "gamefield";
    private static final String CONFIG_HEIGHT = "height";
    private static final String CONFIG_WIDTH = "width";
    private static final String CONFIG_BACKGROUNDS = "backgrounds";
    private static final String CONFIG_BACKGROUND = "background";
    private static final String CONFIG_BACKCOLOR = "backcolor";
    private static final String CONFIG_REPEAT = "repeat";
    private static final String CONFIG_YPOS = "ypos";
    private static final String CONFIG_XPOS = "xpos";
    private static final String CONFIG_BOARDS = "boards";
    private static final String CONFIG_BOARD = "board";
    private static final String CONFIG_PIECESET = "pieceset";
    private static final String CONFIG_PIECES = "pieces";
    private static final String CONFIG_PIECE = "piece";
    private static final String CONFIG_PIECECOLOR = "piececolor";
    private static final String CONFIG_DEFAULTCOMPUTER = "defaultcomputer";
    static final String CONFIG_CARDSET = "cardset";
    private static final String CONFIG_CARDS = "cards";
    private static final String CONFIG_CARD = "card";
    private static final String CONFIG_COVERS = "covers";
    private static final String CONFIG_COVER = "cover";
    private static final String CONFIG_PARTSET = "partset";
    private static final String CONFIG_PARTS = "parts";
    private static final String CONFIG_PART = "part";
    private static final String CONFIG_EXT_PART = ".part";
    private static final String CONFIG_EXTEND = "extend";
    private static final String CONFIG_PARTCLASS = "partclass";
    private static final String CONFIG_SETCLASS = "setclass";
    private static final String CONFIG_CVPCLASS = "cvpclass";
    private static final String CONFIG_COLORS = "colors";
    static final String CONFIG_COLOR = "color";
    private static final String CONFIG_ARRANGEMENTS = "arrangements";
    private static final String CONFIG_ARRANGEMENT = "arrangement";
    private static final String CONFIG_PLAYING_FIELDS = "playingfields";
    private static final String CONFIG_PLAYING_FIELD = "playingfield";
    private static final String CONFIG_FILE = "file";
    private static final String CONFIG_MAIN_MENU = "mainmenu";
    private static final String CONFIG_SCALE_TYPE = "scaletype";

    private static BackgroundColor backgroundColor = null;
    private static final List<Background> backgroundList = new ArrayList<>();
    private static final List<Board> boardList = new ArrayList<>();
    private static final List<Cover> coverList = new ArrayList<>();
    private static final List<PieceSet> pieceSetList = new ArrayList<>();
    private static final List<Arrangement> arrangementList = new ArrayList<>();

    private static final Map<String, AbstractImageEffectReader<? extends ImageEffect>> effectReaders = new HashMap<>();

    static {
        effectReaders.put(GameConfig.CONFIG_SHADOW, new ImageShodawReader());
        effectReaders.put(GameConfig.CONFIG_REFLECTION, new ImageReflectionReader());
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private GameConfigFileReader() {
    }

    /**
     * Read the xml file with the game configuration and sets
     * the attributes of the GameConfig object.
     */
    public static void read(final GameConfig config) {
        // init values
        initConfigValues(config);
        // get the file as document structure
        int tjgerResId = HGBaseResources.getResourceIdByName(TJGER_FILE_NAME, HGBaseResources.RAW);
        if (tjgerResId <= 0) {
            config.hasErrors = true;
            HGBaseLog.logError("The game configuration file '/res/raw/" + TJGER_FILE_NAME + "' was not found!");
            return;
        }
        Element root = HGBaseXMLTools.readXML(HGBaseFileTools.openRawResourceFileStream(tjgerResId));
        if (root == null) {
            config.hasErrors = true;
            HGBaseLog.logError("The game configuration file is not xml conform!");
            return;
        }
        // read the document structure
        ChildNodeIterator.run(new ChildNodeIterator(root, CONFIG_TJGER, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                GameConfig config = (GameConfig) obj;
                switch (node.getNodeName()) {
                    case CONFIG_SETTINGS:
                        readSettings(node, config);
                        break;
                    case CONFIG_PLAYERS:
                        readPlayers(node, config);
                        break;
                    case CONFIG_BACKGROUNDS:
                        readBackgrounds(node, config);
                        break;
                    case CONFIG_BOARDS:
                        readBoards(node, config);
                        break;
                    case CONFIG_COVERS:
                        readCovers(node, config);
                        break;
                    case CONFIG_CARDS:
                        readCardSets(node, config);
                        break;
                    case CONFIG_PIECES:
                        readPieceSets(node, config);
                        break;
                    case CONFIG_PARTS:
                        readParts(node, config);
                        break;
                    case CONFIG_COLORS:
                        readColors(node, config);
                        break;
                    case CONFIG_ARRANGEMENTS:
                        readArrangements(node, config);
                        break;
                    case CONFIG_PLAYING_FIELDS:
                        readPlayingFields(node, config);
                        break;
                    default: // NOCHECK: unknown node
                }
            }
        });
        // set lists
        config.backgroundColor = backgroundColor;
        config.backgrounds = backgroundList.toArray(new Background[0]);
        config.boards = boardList.toArray(new Board[0]);
        config.covers = coverList.toArray(new Cover[0]);
        config.pieceSets = pieceSetList.toArray(new PieceSet[0]);
        config.arrangements = arrangementList.toArray(new Arrangement[0]);
        // test if needed values are not set
        testForErrors(config);
    }

    /**
     * Fills the hash maps with the information for the images of the given
     * piece type.
     *
     * @param pieceType The piece type.
     * @param node      The xml node with the information (extension, path).
     * @param config    The GameConfig object.
     */
    private static void fillImageInformation(String pieceType, Node node, GameConfig config) {
        config.extensionMap.put(pieceType, HGBaseXMLTools.getAttributeValue(node, CONFIG_EXTENSION));
        config.pathMap.put(pieceType, HGBaseXMLTools.getAttributeValue(node, CONFIG_PATH));
    }

    /**
     * @param node   the node to read the playing fields from
     * @param config the game configuration
     */
    protected static void readPlayingFields(Node node, final GameConfig config) {
        final PlayingFieldManager manager = PlayingFieldManager.getInstance();
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_PLAYING_FIELDS, null) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                if (CONFIG_PLAYING_FIELD.equals(node.getNodeName()) && isAvailable(node, config)) {
                    String fileName = HGBaseXMLTools.getAttributeValue(node, CONFIG_FILE);
                    if (HGBaseTools.hasContent(fileName)) {
                        InputStream in = HGBaseFileTools.openAssetsFileStream(fileName);
                        PlayingField field = PlayingFieldFileOperator.fromStream(in);
                        if (field != null) {
                            String name = HGBaseFileTools.getFileName(fileName, false);
                            manager.addField(name, field);
                        }
                        HGBaseFileTools.closeStream(in);
                    }
                }
            }
        });
    }

    /**
     * Reads one arrangement.
     *
     * @param node        The arrangement node.
     * @param arrangement The arrangement object.
     */
    private static void readArrangement(Node node, Arrangement arrangement) {
        String type = HGBaseXMLTools.getAttributeValue(node, CONFIG_TYPE);
        String value = HGBaseXMLTools.getAttributeValue(node, CONFIG_VALUE);
        if (HGBaseTools.hasContent(type) && HGBaseTools.hasContent(value)) {
            if (node.getNodeName().equals(CONFIG_PART)) {
                arrangement.setPart(type, value);
            }
            if (node.getNodeName().equals(CONFIG_PARTSET)) {
                arrangement.setPartSet(type, value);
            }
            if (node.getNodeName().equals(CONFIG_CARDSET)) {
                arrangement.setCardSet(type, value);
            }
            if (node.getNodeName().equals(CONFIG_COLOR)) {
                int rgb = HGBaseTools.toInt(value);
                if (rgb != HGBaseTools.INVALID_INT) {
                    arrangement.setColor(type, new Color(rgb));
                }
            }
        }
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readArrangements(Node node, final GameConfig config) {
        config.completeArrangement = HGBaseXMLTools.getAttributeValue(node, CONFIG_COMPLETE).equals(
                CONFIG_TRUE);
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_ARRANGEMENTS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                if (node.getNodeName().equals(CONFIG_ARRANGEMENT) && isAvailable(node, config)) {
                    String name = HGBaseXMLTools.getAttributeValue(node, CONFIG_NAME);
                    int rgb = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_BACKCOLOR));
                    Color backColor = (HGBaseTools.isValid(rgb)) ? new Color(rgb) : null;
                    String back = HGBaseXMLTools.getAttributeValue(node, CONFIG_BACKGROUND);
                    String board = HGBaseXMLTools.getAttributeValue(node, CONFIG_BOARD);
                    String pieceset = HGBaseXMLTools.getAttributeValue(node, CONFIG_PIECESET);
                    String cover = HGBaseXMLTools.getAttributeValue(node, CONFIG_COVER);
                    String cardset = HGBaseXMLTools.getAttributeValue(node, CONFIG_CARDSET);
                    Arrangement newArrangement = new Arrangement(name, backColor, back, board, pieceset, cover, cardset);
                    arrangementList.add(newArrangement);
                    // look for user defined parts and part sets
                    ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_ARRANGEMENT, newArrangement) {

                        @Override
                        public void performNode(Node node, int index, Object obj) {
                            readArrangement(node, (Arrangement) obj);
                        }
                    });
                }
            }
        });
    }

    /**
     * @param node The settings node.
     * @return True, if hidden-attribute is set to true.
     */
    protected static boolean isHiddenEntry(Node node) {
        return HGBaseXMLTools.getAttributeValue(node, CONFIG_HIDDEN).equals(CONFIG_TRUE);
    }

    /**
     * @param node The settings node.
     * @return True, if hidden-attribute is explicitly set to false.
     */
    protected static boolean isVisibleEntry(Node node) {
        return HGBaseXMLTools.getAttributeValue(node, CONFIG_HIDDEN).equals(CONFIG_FALSE);
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     * @return True, if the part is hidden.
     */
    protected static boolean isHiddenPart(Node node, GameConfig config) {
        return ((config.helpHidden && !isVisibleEntry(node)) || isHiddenEntry(node));
    }

    /**
     * Checks whether this is currently the pro or free version of the app and whether the current node is only available for the pro version.
     *
     * @param node   the node with the current part
     * @param config the game configuration
     * @return true if this part is available, false if not because it's a pro part and the free version is currently running
     */
    protected static boolean isAvailable(Node node, GameConfig config) {
        return config.proVersion || !HGBaseXMLTools.getAttributeBooleanValue(node, CONFIG_PRO);
    }

    /**
     * Returns the list for the given type. If it does not exist, it will be created.
     *
     * @param map  The part or part set map.
     * @param type The part or part set type.
     * @return A list.
     */
    static <T extends Part> List<T> getListFromMap(Map<String, List<T>> map, String type) {
        List<T> list = map.get(type);
        if (list != null) {
            return list;
        } else {
            List<T> newList = new ArrayList<>();
            map.put(type, newList);
            return newList;
        }
    }

    /**
     * Reads one extended part.
     *
     * @param node   The part node.
     * @param config The game configuration object.
     */
    private static void readExtendedPart(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_EXTEND) && isAvailable(node, config)) {
            String type = HGBaseXMLTools.getAttributeValue(node, CONFIG_TYPE);
            String partClass = HGBaseXMLTools.getAttributeValue(node, CONFIG_PARTCLASS);
            String setClass = HGBaseXMLTools.getAttributeValue(node, CONFIG_SETCLASS);
            String cvpClass = HGBaseXMLTools.getAttributeValue(node, CONFIG_CVPCLASS);
            if (partClass.length() > 0) {
                config.extendPartMap.put(type, partClass);
            }
            if (setClass.length() > 0) {
                config.extendPartSetMap.put(type, setClass);
            }
            if (cvpClass.length() > 0) {
                config.extendCvpMap.put(type, cvpClass);
            }
        }
    }

    /**
     * Reads one part.
     *
     * @param node            The part node.
     * @param config          The game configuration object.
     * @param topLevelEffects The top level effects map.
     */
    private static void readPart(Node node, GameConfig config, Map<String, ImageEffect> topLevelEffects) {
        if (!Objects.equals(node.getNodeName(), CONFIG_PART) || !isAvailable(node, config)) {
            return;
        }
        String name = HGBaseXMLTools.getAttributeValue(node, CONFIG_NAME);
        String type = HGBaseXMLTools.getAttributeValue(node, CONFIG_TYPE);
        if (!HGBaseTools.hasContent(type)) {
            HGBaseLog.logWarn("No type defined for part '" + name + "'!");
            return;
        }
        String image = HGBaseXMLTools.getAttributeValue(node, CONFIG_IMAGE);
        if (!HGBaseTools.hasContent(image)) {
            image = calculateImage(CONFIG_PART, name, config);
        }
        Bitmap imgPart = HGBaseGuiTools.loadImage(image);
        boolean hidden = isHiddenPart(node, config);
        if (imgPart == null) {
            logImageNotFound("Part", image);
            return;
        }
        List<Part> listPart = getListFromMap(config.partMap, type);
        String partClass = config.extendPartMap.get(type);
        Part newPart = null;
        if (partClass != null) {
            Class<?>[] classes = {String.class, String.class, Bitmap.class, boolean.class};
            Object[] params = {type, name, imgPart, hidden};
            newPart = ClassFactory.createClass(partClass, Part.class, CONFIG_CVPCLASS, classes, params);
        }
        if (newPart == null) {
            newPart = new Part(type, name, imgPart, hidden);
        }
        setEffectsForPart(newPart, node, topLevelEffects);
        listPart.add(newPart);
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readParts(Node node, final GameConfig config) {
        fillImageInformation(CONFIG_PART, node, config);
        fillImageInformation(CONFIG_PARTSET, node, config);
        final Map<String, ImageEffect> topLevelEffects = readEffectsFromNode(node);
        config.helpHidden = isHiddenEntry(node);
        // look for classes that shall extend Part, PartSet or ColorValuePart
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_PARTS, config) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                readExtendedPart(node, (GameConfig) obj);
            }
        });
        // look for parts
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_PARTS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                readPart(node, (GameConfig) obj, topLevelEffects);
            }
        });
        // look for part sets
        new PartSetContructor<PartSet>(CONFIG_PARTS, CONFIG_PARTSET, CONFIG_PART, config.partSetMap) {

            @Override
            protected PartSet createPartSet(String type, String name, Node node, boolean hidden) {
                String setClass = config.extendPartSetMap.get(type);
                PartSet newPartSet = null;
                if (setClass != null) {
                    Class<?>[] classes = {String.class, String.class, boolean.class};
                    Object[] params = {type, name, hidden};
                    newPartSet = ClassFactory.createClass(setClass, PartSet.class, CONFIG_SETCLASS, classes, params);
                }
                if (newPartSet == null) {
                    newPartSet = new PartSet(type, name, hidden);
                }
                setEffectsForPart(newPartSet, node, topLevelEffects);
                return newPartSet;
            }

            @Override
            protected ColorValuePart createColorValuePart(PartSet set, String color, int sequence, int value, Bitmap image, Node node) {
                final String partType = set.getType() + CONFIG_EXT_PART;
                String cvpClass = config.extendCvpMap.get(set.getType());
                ColorValuePart newCVP = null;
                if (cvpClass != null) {
                    Class<?>[] classes = {PartSet.class, String.class, String.class, int.class, int.class, Bitmap.class};
                    Object[] params = {set, partType, color, sequence, value, image};
                    newCVP = ClassFactory.createClass(cvpClass, ColorValuePart.class, CONFIG_CVPCLASS, classes, params);
                }
                if (newCVP == null) {
                    newCVP = new ColorValuePart(set, partType, color, sequence, value, image);
                }
                setEffectsForPart(newCVP, node, set);
                return newCVP;
            }
        }.run(node, config);
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readColors(Node node, GameConfig config) {
        // look for colors
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_COLORS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                GameConfig config = (GameConfig) obj;
                if (node.getNodeName().equals(CONFIG_COLOR) && isAvailable(node, config)) {
                    String colorType = HGBaseXMLTools.getAttributeValue(node, CONFIG_TYPE);
                    if (HGBaseTools.hasContent(colorType)) {
                        String defaultText = HGBaseXMLTools.getAttributeValue(node, CONFIG_DEFAULT);
                        int rgb = HGBaseTools.toInt(defaultText);
                        Color defaultColor = (rgb == HGBaseTools.INVALID_INT) ? null : new Color(rgb);
                        config.colors.put(colorType, defaultColor);
                    }
                }
            }
        });
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readPieceSets(Node node, GameConfig config) {
        setOrderBy(config, ConstantValue.CONFIG_PIECESET, node);
        fillImageInformation(CONFIG_PIECE, node, config);
        final Map<String, ImageEffect> topLevelEffects = readEffectsFromNode(node);
        config.helpHidden = isHiddenEntry(node);
        new PartSetContructor<PieceSet>(CONFIG_PIECES, CONFIG_PIECESET, CONFIG_PIECE, pieceSetList) {
            @Override
            protected PieceSet createPartSet(String type, String name, Node node, boolean hidden) {
                PieceSet pieceSetPart = new PieceSet(name, hidden);
                setEffectsForPart(pieceSetPart, node, topLevelEffects);
                return pieceSetPart;
            }

            @Override
            protected ColorValuePart createColorValuePart(PartSet set, String color, int sequence, int value, Bitmap image, Node node) {
                Piece piecePart = new Piece((PieceSet) set, color, sequence, value, image);
                setEffectsForPart(piecePart, node, set);
                return piecePart;
            }

        }.run(node, config);
    }

    /**
     * @param setType The part set type.
     * @param node    The node to read out the order by mode.
     */
    static void setOrderBy(GameConfig config, String setType, Node node) {
        int currentOrder = config.getOrderby(setType);
        if (currentOrder < 0 || currentOrder == GameConfig.ORDERBY_NONE) {
            String modeText = HGBaseXMLTools.getAttributeValue(node, CONFIG_ORDERBY);
            int modeInt = GameConfig.ORDERBY_NONE;
            if (modeText.equals(CONFIG_VALUE)) {
                modeInt = GameConfig.ORDERBY_VALUE;
            } else if (modeText.equals(CONFIG_COLOR)) {
                modeInt = GameConfig.ORDERBY_COLOR;
            }
            config.setOrderBy.set(setType, modeInt);
        }
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readCardSets(Node node, GameConfig config) {
        setOrderBy(config, ConstantValue.CONFIG_CARDSET, node);
        fillImageInformation(CONFIG_CARD, node, config);
        final Map<String, ImageEffect> topLevelEffects = readEffectsFromNode(node);
        config.helpHidden = isHiddenEntry(node);
        new PartSetContructor<CardSet>(CONFIG_CARDS, CONFIG_CARDSET, CONFIG_CARD, config.cardSetsMap) {
            @Override
            protected CardSet createPartSet(String type, String name, Node node, boolean hidden) {
                String validType = (type == null || type.isEmpty()) ? ConstantValue.CONFIG_CARDSET : type;
                CardSet cardSetPart = new CardSet(validType, name, hidden);
                setEffectsForPart(cardSetPart, node, topLevelEffects);
                return cardSetPart;
            }

            @Override
            protected ColorValuePart createColorValuePart(PartSet set, String color, int sequence, int value, Bitmap image, Node node) {
                Card cardPart = new Card((CardSet) set, color, sequence, value, image);
                setEffectsForPart(cardPart, node, set);
                return cardPart;
            }

        }.run(node, config);

        // check for all type of card sets if an order by was defined,
        // if not take the order defined at cards
        int defaultOrder = config.getOrderby(ConstantValue.CONFIG_CARDSET);
        if (isDefinedOrder(defaultOrder)) {
            String[] cardTypes = config.getCardSetTypes();
            for (String cardType : cardTypes) {
                int testOrder = config.getOrderby(cardType);
                if (!isDefinedOrder(testOrder)) {
                    config.setOrderBy.set(cardType, defaultOrder);
                }
            }
        }
    }

    /**
     * Returns {code true} if the specified value is a valid order.
     *
     * @param defaultOrder The value to check.
     * @return {code true} if the specified value is a valid order.
     */
    private static boolean isDefinedOrder(int defaultOrder) {
        return defaultOrder >= 0 && defaultOrder != GameConfig.ORDERBY_NONE;
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readCovers(Node node, GameConfig config) {
        fillImageInformation(CONFIG_COVER, node, config);
        final Map<String, ImageEffect> topLevelEffects = readEffectsFromNode(node);
        config.helpHidden = isHiddenEntry(node);
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_COVERS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                GameConfig config = (GameConfig) obj;
                if (node.getNodeName().equals(CONFIG_COVER) && isAvailable(node, config)) {
                    String name = HGBaseXMLTools.getAttributeValue(node, CONFIG_NAME);
                    String image = HGBaseXMLTools.getAttributeValue(node, CONFIG_IMAGE);
                    if (!HGBaseTools.hasContent(image)) {
                        image = calculateImage(CONFIG_COVER, name, config);
                    }
                    Bitmap imgCover = HGBaseGuiTools.loadImage(image);
                    boolean hidden = isHiddenPart(node, config);
                    if (imgCover != null) {
                        Cover coverPart = new Cover(name, imgCover, hidden);
                        setEffectsForPart(coverPart, node, topLevelEffects);
                        coverList.add(coverPart);
                    } else {
                        logImageNotFound("Cover", image);
                    }
                }
            }
        });
    }

    /**
     * Reads one board.
     *
     * @param node            The board node.
     * @param config          The game configuration object.
     * @param topLevelEffects The top level effects map.
     */
    private static void readBoard(Node node, GameConfig config, Map<String, ImageEffect> topLevelEffects) {
        if (node.getNodeName().equals(CONFIG_BOARD) && isAvailable(node, config)) {
            String name = HGBaseXMLTools.getAttributeValue(node, CONFIG_NAME);
            String image = HGBaseXMLTools.getAttributeValue(node, CONFIG_IMAGE);
            if (!HGBaseTools.hasContent(image)) {
                image = calculateImage(CONFIG_BOARD, name, config);
            }
            Bitmap imgBoard = HGBaseGuiTools.loadImage(image);
            int xPos = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_XPOS));
            if (xPos == HGBaseTools.INVALID_INT) {
                xPos = 0;
            }
            int yPos = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_YPOS));
            if (yPos == HGBaseTools.INVALID_INT) {
                yPos = 0;
            }
            int zoom = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_ZOOM));
            if (zoom == HGBaseTools.INVALID_INT) {
                zoom = 100;
            }
            boolean hidden = isHiddenPart(node, config);
            if (imgBoard != null) {
                Board boardPart = new Board(name, imgBoard, xPos, yPos, hidden, zoom);
                setEffectsForPart(boardPart, node, topLevelEffects);
                boardList.add(boardPart);
            } else {
                logImageNotFound("Board", image);
            }
        }
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readBoards(Node node, GameConfig config) {
        fillImageInformation(CONFIG_BOARD, node, config);
        final Map<String, ImageEffect> topLevelEffects = readEffectsFromNode(node);
        config.helpHidden = isHiddenEntry(node);
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_BOARDS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                readBoard(node, (GameConfig) obj, topLevelEffects);
            }
        });
    }

    /**
     * Read one background.
     *
     * @param node            The background node.
     * @param config          The game configuration object.
     * @param topLevelEffects The top level effects map.
     */
    private static void readBackground(Node node, GameConfig config, Map<String, ImageEffect> topLevelEffects) {
        if (node.getNodeName().equals(CONFIG_BACKGROUND) && isAvailable(node, config)) {
            String name = HGBaseXMLTools.getAttributeValue(node, CONFIG_NAME);
            String image = HGBaseXMLTools.getAttributeValue(node, CONFIG_IMAGE);
            boolean hidden = isHiddenPart(node, config);
            if (!HGBaseTools.hasContent(image)) {
                image = calculateImage(CONFIG_BACKGROUND, name, config);
            }
            Bitmap imgBack = HGBaseGuiTools.loadImage(image);
            if (imgBack != null) {
                boolean repeat = (HGBaseXMLTools.getAttributeValue(node, CONFIG_REPEAT).equals(CONFIG_TRUE));
                boolean ignoreZoom = (HGBaseXMLTools.getAttributeValue(node, CONFIG_IGNOREZOOM).equals(CONFIG_TRUE));
                int zoom = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_ZOOM));
                if (zoom == HGBaseTools.INVALID_INT) {
                    zoom = 100;
                }
                Background backPart = new Background(name, imgBack, repeat, ignoreZoom, hidden, zoom);
                if (!repeat) {
                    setEffectsForPart(backPart, node, topLevelEffects);
                }
                backgroundList.add(backPart);
            } else {
                logImageNotFound("Background", image);
            }
        }
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readBackgrounds(Node node, GameConfig config) {
        fillImageInformation(CONFIG_BACKGROUND, node, config);
        final Map<String, ImageEffect> topLevelEffects = readEffectsFromNode(node);
        config.helpHidden = isHiddenEntry(node);
        // read the background color from the root (either color or backcolor attribute)
        int rgb = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_COLOR));
        if (HGBaseTools.isValid(rgb)) {
            backgroundColor = new BackgroundColor(new Color(rgb), config.helpHidden);
        } else {
            rgb = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_BACKCOLOR));
            if (HGBaseTools.isValid(rgb)) {
                backgroundColor = new BackgroundColor(new Color(rgb), config.helpHidden);
            }
        }
        // read all the background images
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_BACKGROUNDS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                readBackground(node, (GameConfig) obj, topLevelEffects);
            }
        });
    }

    /**
     * @param pieceType The name of the piece type.
     * @param name      Name of the piece.
     * @param config    The GameConfig object.
     * @return The standard file name.
     */
    protected static String calculateImage(String pieceType, String name, GameConfig config) {
        return config.getImagePath(pieceType) + "/" + name + "." + config.getImageExtension(pieceType);
    }

    /**
     * Reads all effects from the given node and stores them in a map, where the key is the attribute
     * name that holds the data and the value is the effect (only if effect is available).
     *
     * @param node the node to read the effect data from
     * @return a map with attribute name as keys and effect as values, can be empty
     */
    private static Map<String, ImageEffect> readEffectsFromNode(Node node) {
        Map<String, ImageEffect> availableEffects = new HashMap<>();
        for (Entry<String, AbstractImageEffectReader<? extends ImageEffect>> reader : effectReaders.entrySet()) {
            ImageEffect effect = reader.getValue().getEffectDefinition(node);
            if (effect != null) {
                availableEffects.put(reader.getKey(), effect);
            }
        }
        return availableEffects;
    }

    /**
     * Set all effects for the given part according to the node and the top level effects.
     *
     * @param part               the part to set the effect for
     * @param node               the node to read the possible effect
     * @param higherLevelEffects a map with all higher level effects
     */
    private static void setEffectsForPart(Part part, Node node, Map<String, ImageEffect> higherLevelEffects) {
        for (Entry<String, AbstractImageEffectReader<? extends ImageEffect>> reader : effectReaders.entrySet()) {
            ImageEffect higherLevelEffect = higherLevelEffects.get(reader.getKey());
            reader.getValue().setEffectForPart(part, node, higherLevelEffect);
        }
    }

    /**
     * Set all effects for the given part according to the node and the effect of the top level part.
     *
     * @param part            the part to set the effect for
     * @param node            the node to read the possible effect
     * @param higherLevelPart the higher level part possibly holding an effect
     */
    private static void setEffectsForPart(Part part, Node node, Part higherLevelPart) {
        for (Entry<String, AbstractImageEffectReader<? extends ImageEffect>> reader : effectReaders.entrySet()) {
            ImageEffect higherLevelEffect = reader.getValue().getEffectFromPart(higherLevelPart);
            reader.getValue().setEffectForPart(part, node, higherLevelEffect);
        }
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readPlayers(Node node, GameConfig config) {
        fillImageInformation(CONFIG_PLAYER, node, config);
        config.playerPieceColor = HGBaseXMLTools.getAttributeValue(node, CONFIG_PIECECOLOR);
        PlayerFactory.getInstance().setDefaultComputerType(HGBaseXMLTools.getAttributeValue(node, CONFIG_DEFAULTCOMPUTER));
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_PLAYERS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                GameConfig config = (GameConfig) obj;
                if (node.getNodeName().equals(CONFIG_PLAYER) && isAvailable(node, config)) {
                    String playerType = HGBaseXMLTools.getAttributeValue(node, CONFIG_TYPE);
                    String classPath = HGBaseXMLTools.getAttributeValue(node, CONFIG_CLASS);
                    String image = HGBaseXMLTools.getAttributeValue(node, CONFIG_IMAGE);
                    if (!HGBaseTools.hasContent(image)) {
                        image = calculateImage(CONFIG_PLAYER, playerType, config);
                    }
                    Bitmap typeImage = HGBaseGuiTools.loadImage(image);
                    if ((PlayerFactory.getInstance().addPlayerType(playerType, classPath, typeImage)) && (typeImage == null)) {
                        logImageNotFound("Player type", image);
                    }
                }
            }
        });
    }

    /**
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    protected static void readSettings(Node node, GameConfig config) {
        ChildNodeIterator.run(new ChildNodeIterator(node, CONFIG_SETTINGS, config) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                GameConfig config = (GameConfig) obj;
                readAppSettings(node, config);
                readAdvertisementSettings(node, config);
                readGameRulesSettings(node, config);
                readGameStateSettings(node, config);
                readNewGameDialogSettings(node);
                readGameDialogsSettings(node);
                readStateChangedSettings(node, config);
                readPlayerSettings(node, config);
                readStatisticsSettings(node, config);
                readZoomSettings(node, config);
                readNetworkSettings(node, config);
                readGamefieldSettings(node, config);
                readInfoDialogSettings(node, config);
                readInterruptionSettings(node, config);
                readHintSettings(node, config);
                readDelaySettings(node, config);
                readMainMenuSettings(node, config);
            }
        });
    }

    /**
     * Reads the main menu settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readMainMenuSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_MAIN_MENU)) {
            config.mainMenuImageScaleType = HGBaseXMLTools.getAttributeValue(node, CONFIG_SCALE_TYPE);
        }
    }

    /**
     * Reads the delay settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readDelaySettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_DELAY)) {
            int round = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_ROUND));
            if (round > 0) {
                config.delayRound = round;
            }
            int turn = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_TURN));
            if (turn > 0) {
                config.delayTurn = turn;
            }
            int move = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_MOVE));
            if (move > 0) {
                config.delayMove = move;
            }
            int player = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_PLAYER));
            if (player > 0) {
                config.delayPlayer = player;
            }
        }
    }

    /**
     * Reads the hints settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readHintSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_HINTS)) {
            putIntoMap(node, ConstantValue.HINTS_PATH, config.hintsMap);
            putIntoMap(node, ConstantValue.HINTS_EXTENSION, config.hintsMap);
            for (String hintType : ConstantValue.getHintTypes()) {
                putIntoMap(node, hintType, config.hintsMap);
            }
        }
    }

    /**
     * Reads the interruption settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readInterruptionSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_INTERRUPT)) {
            config.interruptAfterRound = HGBaseXMLTools.getAttributeValue(node, CONFIG_ROUND).equals(CONFIG_TRUE);
        }
    }

    /**
     * Reads the info dialog settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readInfoDialogSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_INFODIALOGS)) {
            config.dialogAfterTurn = HGBaseXMLTools.getAttributeValue(node, CONFIG_TURN).equals(CONFIG_TRUE);
            config.dialogAfterRound = HGBaseXMLTools.getAttributeValue(node, CONFIG_ROUND).equals(CONFIG_TRUE);
            config.dialogAfterGame = HGBaseXMLTools.getAttributeValue(node, CONFIG_GAME).equals(CONFIG_TRUE);
        }
    }

    /**
     * Reads the game field settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readGamefieldSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_GAMEFIELD)) {
            int w = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_WIDTH));
            int h = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_HEIGHT));
            if (w != HGBaseTools.INVALID_INT && h != HGBaseTools.INVALID_INT) {
                config.fieldWidth = w;
                config.fieldHeight = h;
            }
        }
    }

    /**
     * Reads the network settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readNetworkSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_NETWORK)) {
            int port = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_PORT));
            if (port != HGBaseTools.INVALID_INT) {
                config.networkPort = port;
            }
            config.localGameStateTurn = HGBaseXMLTools.getAttributeValue(node, CONFIG_TURN).equals(CONFIG_LOCAL);
            config.localGameStateRound = HGBaseXMLTools.getAttributeValue(node, CONFIG_ROUND).equals(CONFIG_LOCAL);
            config.localGameStateGame = HGBaseXMLTools.getAttributeValue(node, CONFIG_GAME).equals(CONFIG_LOCAL);

        }
    }

    /**
     * Reads the zoom settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readZoomSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_ZOOM)) {
            int min = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_MIN));
            int max = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_MAX));
            if (min != HGBaseTools.INVALID_INT) {
                config.minZoom = min;
            }
            if (max != HGBaseTools.INVALID_INT) {
                config.maxZoom = max;
            }
            if (config.minZoom > config.maxZoom) {
                config.minZoom = GameConfig.STANDARD_ZOOM;
                config.maxZoom = GameConfig.STANDARD_ZOOM;
            }
        }
    }

    /**
     * Reads the statistic settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readStatisticsSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_STATISTICS)) {
            config.rememberGames = HGBaseXMLTools.getAttributeValue(node, CONFIG_GAMES).equals(CONFIG_TRUE);
            config.rememberScores = HGBaseXMLTools.getAttributeValue(node, CONFIG_SCORES).equals(CONFIG_TRUE);
            int hiScore = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_HIGHSCORE));
            if (hiScore > 0) {
                config.highScoreLength = hiScore;
                config.onlyFirstHighScore = HGBaseXMLTools.getAttributeValue(node, CONFIG_ONLYFIRST).equals(CONFIG_TRUE);
            }
            int scrollWhen = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_SCROLLWHEN));
            if (scrollWhen > 0) {
                config.statisticsScrollWhen = scrollWhen;
            }
            config.isLowerScoreBetter = HGBaseXMLTools.getAttributeValue(node, CONFIG_LOWERSCOREISBETTER).equals(CONFIG_TRUE);
        }
    }

    /**
     * Reads the player settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readPlayerSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_PLAYERS)) {
            config.oneHumanPlayer = HGBaseXMLTools.getAttributeValue(node, CONFIG_ONEHUMAN).equals(CONFIG_TRUE);
            config.withoutHumanPlayer = HGBaseXMLTools.getAttributeValue(node, CONFIG_WITHOUTHUMAN).equals(CONFIG_TRUE);
            int min = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_MIN));
            int max = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_MAX));
            if (min != HGBaseTools.INVALID_INT && max != HGBaseTools.INVALID_INT && min <= max) {
                config.minPlayers = min;
                config.maxPlayers = max;
                int defaultPlayers = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, CONFIG_DEFAULT));
                if (defaultPlayers >= min && defaultPlayers <= max) {
                    config.defaultPlayers = defaultPlayers;
                } else {
                    config.defaultPlayers = config.minPlayers;
                }
            }
            String order = HGBaseXMLTools.getAttributeValue(node, CONFIG_ORDER);
            if (CONFIG_CLOCKWISE.equals(order)) {
                config.playersOrder = GameConfig.PLAYERS_CLOCKWISE;
            } else if (CONFIG_COUNTERCLOCKWISE.equals(order) || CONFIG_ANTICLOCKWISE.equals(order)) {
                config.playersOrder = GameConfig.PLAYERS_COUNTERCLOCKWISE;
            } else {
                config.playersOrder = GameConfig.PLAYERS_LEFTTORIGHT;
            }
        }
    }

    /**
     * Reads the game state changed settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readStateChangedSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_CHANGED)) {
            config.changedOnNewGame = HGBaseXMLTools.getAttributeValue(node, CONFIG_NEWGAME).equals(CONFIG_TRUE);
            config.changedOnNewRound = HGBaseXMLTools.getAttributeValue(node, CONFIG_NEWROUND).equals(CONFIG_TRUE);
            config.changedOnNewTurn = HGBaseXMLTools.getAttributeValue(node, CONFIG_NEWTURN).equals(CONFIG_TRUE);
            config.changedAfterMove = HGBaseXMLTools.getAttributeValue(node, CONFIG_AFTERMOVE).equals(CONFIG_TRUE);
        }
    }

    /**
     * Reads the game dialog settings.
     *
     * @param node The settings node.
     */
    private static void readGameDialogsSettings(Node node) {
        if (node.getNodeName().equals(CONFIG_GAMEDIALOGS)) {
            String classPath = HGBaseXMLTools.getAttributeValue(node, CONFIG_CLASS);
            GameDialogFactory.getInstance().setGameDialogsClass(classPath);
        }
    }

    /**
     * Reads the new game dialog settings.
     *
     * @param node The settings node.
     */
    private static void readNewGameDialogSettings(Node node) {
        if (node.getNodeName().equals(CONFIG_NEWGAMEDIALOG)) {
            String classPath = HGBaseXMLTools.getAttributeValue(node, CONFIG_CLASS);
            GameDialogFactory.getInstance().setNewGameDialogClass(classPath);
        }
    }

    /**
     * Reads the game state settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readGameStateSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_GAMESTATE)) {
            String classPath = HGBaseXMLTools.getAttributeValue(node, CONFIG_CLASS);
            if (!StateFactory.getInstance().setGameStateClass(classPath)) {
                config.hasErrors = true;
            } else {
                // look for a user defined xml root element name
                config.gameStateXmlRoot = HGBaseXMLTools.getAttributeValue(node, CONFIG_XML_ROOT);
            }
        }
    }

    /**
     * Reads the game rule settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readGameRulesSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_RULES)) {
            String classPath = HGBaseXMLTools.getAttributeValue(node, CONFIG_CLASS);
            if (!RulesFactory.getInstance().setRulesClass(classPath)) {
                config.hasErrors = true;
            }
        }
    }

    /**
     * Reads the advertisement settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readAdvertisementSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_ADVERTISEMENTS)) {
            config.advertisements = HGBaseXMLTools.getAttributeBooleanValue(node, CONFIG_ADVERTISEMENTACTIVE);
            config.advertisementURL = HGBaseXMLTools.getAttributeValue(node, CONFIG_ADVERTISEMENTURL);
            config.advertisementErrorPageURL = HGBaseXMLTools.getAttributeValue(node, CONFIG_ADVERTISEMENTERRORPAGEURL);
            config.advertisementWidthPercent = HGBaseXMLTools.getAttributeIntValue(node, CONFIG_ADVERTISEMENTWIDTHPERCENT, HGBaseTools.INVALID_INT);
            config.advertisementHeightPercent = HGBaseXMLTools.getAttributeIntValue(node, CONFIG_ADVERTISEMENTHEIGHTPERCENT, HGBaseTools.INVALID_INT);
        }
    }

    /**
     * Reads the app information settings.
     *
     * @param node   The settings node.
     * @param config The game configuration object.
     */
    private static void readAppSettings(Node node, GameConfig config) {
        if (node.getNodeName().equals(CONFIG_APP)) {
            config.proVersion = HGBaseXMLTools.getAttributeBooleanValue(node, CONFIG_PRO);
            config.fullscreenMode = HGBaseXMLTools.getAttributeValue(node, CONFIG_FULLSCREENMODE);
        }
    }

    /**
     * Puts the given attribute from the node into the map, if it exists.
     *
     * @param node      The node that's attributes are investigated.
     * @param attribute The attribute to look for.
     * @param map       The map so save the value.
     */
    private static void putIntoMap(Node node, String attribute, Map<String, String> map) {
        String value = HGBaseXMLTools.getAttributeValue(node, attribute);
        if (HGBaseTools.hasContent(value)) {
            map.put(attribute, value);
        }
    }

    /**
     * Set the hasError attribute to true if needed values are missing.
     *
     * @param config The GameConfig object.
     */
    private static void testForErrors(GameConfig config) {
        if (config.getMinPlayers() == 0 || config.getMaxPlayers() == 0
                || config.getMaxPlayers() < config.getMinPlayers()) {
            config.hasErrors = true;
            HGBaseLog.logError("Number of players are defined ambiguous!");
        } else {
            PlayerFactory f = PlayerFactory.getInstance();
            if (f.getSupportedPlayerTypes().length - f.getNetworkPlayerTypes().length <= 0) {
                config.hasErrors = true;
                HGBaseLog.logError("There aren't enough valid player types defined!");
            }
            if (config.networkPort > 0 && f.getNetworkPlayerTypes().length > 0) {
                config.networkPossible = true;
            }
        }
    }

    /**
     * @param config The GameConfig object.
     */
    private static void initConfigValues(final GameConfig config) {
        config.completeArrangement = false;
        config.minZoom = GameConfig.STANDARD_ZOOM;
        config.maxZoom = GameConfig.STANDARD_ZOOM;
        config.extensionMap = new LinkedHashMap<>();
        config.pathMap = new LinkedHashMap<>();
        config.colors = new LinkedHashMap<>();
        config.playerPieceColor = GameConfig.EMPTY_STRING;
        config.networkPossible = false;
        config.networkPort = 0;
        config.minPlayers = 0;
        config.maxPlayers = 0;
        config.defaultPlayers = 0;
        config.fieldWidth = 0;
        config.fieldHeight = 0;
        config.oneHumanPlayer = false;
        config.withoutHumanPlayer = false;
        config.rememberScores = false;
        config.rememberGames = false;
        config.highScoreLength = 0;
        config.onlyFirstHighScore = false;
        config.isLowerScoreBetter = false;
        config.statisticsScrollWhen = 0;
        config.changedOnNewGame = false;
        config.changedOnNewRound = false;
        config.changedOnNewTurn = false;
        config.changedAfterMove = false;
        config.dialogAfterTurn = false;
        config.dialogAfterRound = false;
        config.dialogAfterGame = false;
        config.interruptAfterRound = false;
        config.hasErrors = false;
        config.cardSetsMap = new LinkedHashMap<>();
        config.partMap = new LinkedHashMap<>();
        config.partSetMap = new LinkedHashMap<>();
        config.extendPartMap = new HashMap<>();
        config.extendPartSetMap = new HashMap<>();
        config.extendCvpMap = new HashMap<>();
        config.setOrderBy = new IntBooleanStringMap();
        config.localGameStateTurn = false;
        config.localGameStateRound = false;
        config.localGameStateGame = false;
        config.hintsMap = new LinkedHashMap<>();
        config.delayRound = 0;
        config.delayTurn = 0;
        config.delayMove = 0;
        config.delayPlayer = 0;
        config.gameSpeed = 1.0;
        config.playersOrder = GameConfig.PLAYERS_LEFTTORIGHT;
        config.recordOnNewGame = false;
        config.recordOnNewRound = false;
        config.recordOnNewTurn = false;
    }

    /**
     * Logs an "image not found" warning.
     *
     * @param imageTypeName The type of the image as string.
     * @param imageName     The name of the image.
     */
    private static void logImageNotFound(String imageTypeName, String imageName) {
        HGBaseLog.logWarn(imageTypeName + " image " + imageName + " not found!");
    }
}