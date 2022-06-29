package com.tjger.game.completed;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.MainFrame;
import com.tjger.game.GameRules;
import com.tjger.game.GameState;
import com.tjger.game.internal.GameStatistics;
import com.tjger.game.internal.PlayerProfiles;
import com.tjger.game.internal.RulesFactory;
import com.tjger.gui.PartSorter;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.IntBooleanStringMap;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Manages general objects of the game. Do not inherit from this class.
 *
 * @author hagru
 */
final public class GameManager {

    private static GameManager manager;
    private static GameConfig gameConfig;
    private static GameEngine gameEngine;
    private static PlayerManager playerManager;
    private static GameRules gameRules;
    private IntBooleanStringMap gameInfoMap;
    private Map<String,PartSorter> sorterMap;
    private int loadError;

    private GameManager() {
        super();
        this.sorterMap = new HashMap<>();
        readGameConfiguration();
        resetNewGameInformation();
    }

    /**
     * Reads the game configuration from the xml file.
     */
    private void readGameConfiguration() {
        // create the game configuration instance to read out the xml file
        gameConfig = GameConfig.getInstance();
        if (gameConfig.hasErrors) {
            return;
        }
        // create the game engine
        gameEngine = GameEngine.getInstance();
        // create the rules class
        gameRules = RulesFactory.getInstance().createGameRules();
        // create the player manager to initialize the player
        playerManager = PlayerManager.getInstance();
    }

    /**
     * Creates and returns the one and only instance of the game manager.
     * This implementation is not thread-safe but called only on creation of the main frame.
     *
     * @return the one and only instance of the game manager.
     */
    public static GameManager createInstance(MainFrame main) {
    	if (manager == null) {
            manager = new GameManager();
        }
        return manager;
    }

    /**
     * @return The one and only instance of the game manager or null if it was not created.
     */
    public static GameManager getInstance() {
        return manager;
    }
    
    /**
     * @return The application's main frame.
     */
    public MainFrame getMainFrame() {
    	return MainFrame.getInstance();
    }

    /**
     * @return The game configuration.
     */
    public GameConfig getGameConfig() {
        return gameConfig;
    }

    /**
     * @return The player manager.
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * @return The game rules.
     */
    public GameRules getGameRules() {
        return gameRules;
    }

    /**
     * @return The current game state.
     */
    public GameState getGameState() {
        return getGameEngine().getGameState();
    }

    /**
     * @return The current game engine.
     */
    public GameEngine getGameEngine() {
        return gameEngine;
    }

    /**
     * @return The current game statistics.
     */
    public GameStatistics getGameStatistics() {
        return GameStatistics.getInstance();
    }

    /**
     * @return The player profiles.
     */
    public PlayerProfiles getPlayerProfiles() {
        return PlayerProfiles.getInstance();
    }

    /**
     * Resets all information for a new game.
     */
    public void resetNewGameInformation() {
        gameInfoMap = new IntBooleanStringMap();
    }

    /**
     * @see ConstantValue.GAMEINFO_xxx
     * @param infoKey Information to set.
     * @param information A piece of information.
     */
    public void setNewGameInformation(String infoKey, String information) {
        gameInfoMap.set(infoKey, information);
    }

    /**
     * @see ConstantValue.GAMEINFO_xxx
     * @param infoKey Information to set.
     * @param information A piece of information.
     */
    public void setNewGameInformation(String infoKey, int information) {
        gameInfoMap.set(infoKey, information);
    }

    /**
     * @see ConstantValue.GAMEINFO_xxx
     * @param infoKey Information to set.
     * @param information A piece of information.
     */
    public void setNewGameInformation(String infoKey, boolean information) {
        gameInfoMap.set(infoKey, information);
    }

    /**
     * @param infoKey Information that is required.
     * @return The information or null if there exists no such information
     */
    private String getNewGameInformation(String infoKey) {
        return gameInfoMap.get(infoKey);
    }

    /**
     * @param infoKey Information that is required.
     * @return The information or "" if there exists no such information
     */
    public String getNewGameInformationText(String infoKey) {
        return getNewGameInformationText(infoKey, "");
    }

    /**
     * @param infoKey Information that is required.
     * @param defaultValue The default value if there exists no such information
     * @return The information or {@code defaultValue} if there exists no such information
     */
    public String getNewGameInformationText(String infoKey, String defaultValue) {
        return gameInfoMap.get(infoKey, defaultValue);
    }
    
    /**
     * @param infoKey Information that is required.
     * @return The information or INVALID_INT if there exists no such information
     */
    public int getNewGameInformationInt(String infoKey) {
        return getNewGameInformationInt(infoKey, HGBaseTools.INVALID_INT);
    }

    /**
     * @param infoKey Information that is required.
     * @param defaultValue The default value if there exists no such information
     * @return The information or {@code defaultValue} if there exists no such information
     */
    public int getNewGameInformationInt(String infoKey, int defaultValue) {
        return gameInfoMap.getInt(infoKey, defaultValue);
    }

    /**
     * @param infoKey Information that is required.
     * @return The information or false if there exists no such information
     */
    public boolean getNewGameInformationBoolean(String infoKey) {
        return getNewGameInformationBoolean(infoKey, false);
    }

    /**
     * @param infoKey Information that is required.
     * @param defaultValue The default value if there exists no such information
     * @return The information or  {@code defaultValue} if there exists no such information
     */
    public boolean getNewGameInformationBoolean(String infoKey, boolean defaultValue) {
        return gameInfoMap.getBoolean(infoKey, defaultValue);
    }

    /**
     * @return All keys for new game information.
     */
    public String[] getNewGameInformationKeys() {
        return gameInfoMap.getKeys();
    }

    /**
     * Sets a sorting behavior for a given type of part (e.g. cards).
     * For one type there can only exist one sorter. If a sorter is set,
     * the standard sorting algorithm is ignored.
     *
     * @param partType The type of the part, see Part.getType().
     * @param partSorter The part sorter.
     */
    public void setPartSorter(String partType, PartSorter partSorter) {
        sorterMap.put(partType, partSorter);
    }

    /**
     * @param partType A part type.
     * @return The part sorter or null (default sorting behavior).
     */
    public PartSorter getPartSorter(String partType) {
        return sorterMap.get(partType);
    }

    /**
     * Save the current game.
     *
     * @param doc The document object.
     * @return 0 if saving was successful.
     */
    public int saveGame(Document doc) {
        if (doc==null) {
            return -10705;
        }
        String gameStateXmlRoot = GameConfig.getInstance().getGameStateXmlRoot();
        if (HGBaseTools.hasContent(gameStateXmlRoot)) {
            // only the game state is saved in a user defined xml root
            Element root = doc.createElement(gameStateXmlRoot);
            doc.appendChild(root);
            return getGameState().save(doc, root);            
        } else {
            // save all game information
            Element root = doc.createElement("tjgergame");
            doc.appendChild(root);
            int ret = 0;
            // save the game information values
            saveNewGameInformation(doc, root);
            // save the game engine values
            Element gameEngine = doc.createElement("gameengine");
            ret = getGameEngine().saveEngine(doc, gameEngine);
            if (ret!=0) {
                return ret;
            }
            root.appendChild(gameEngine);
            // save the game statistics
            Element gameStats = doc.createElement("gamestatistics");
            ret = getGameStatistics().saveStatistics(doc, gameStats);
            if (ret!=0) {
                return ret;
            }
            root.appendChild(gameStats);
            // save the game state
            Element gameState = doc.createElement("gamestate");
            ret = getGameState().save(doc, gameState);
            if (ret!=0) {
                return ret;
            }
            root.appendChild(gameState);
        }
        // game is saved
        return 0;
    }

    /**
     * Loads a game.
     *
     * @param root The root of the game information.
     * @return 0 if loading was successful.
     */
    public int loadGame(Node root) {
        final GameEngine engine = getGameEngine();
        loadError = 0;
        resetNewGameInformation();
        String gameStateXmlRoot = GameConfig.getInstance().getGameStateXmlRoot();
        if (HGBaseTools.hasContent(gameStateXmlRoot)) {
            // load the user defined game state
            loadError = getGameState().load(root);
        } else {
        // load game engine, statistics and game state
            ChildNodeIterator.run(new ChildNodeIterator(root, "tjgergame", this) {
                @Override
                public void performNode(Node node, int index, Object obj) {
                    GameManager manager = (GameManager)obj;
                    if (node.getNodeName().equals("newgameinfo")) {
                        loadNewGameInformation(node);
                    }
                    if (node.getNodeName().equals("gameengine") && manager.loadError==0) {
                        manager.loadError = manager.getGameEngine().loadEngine(node);
                    }
                    if (node.getNodeName().equals("gamestatistics") && manager.loadError==0) {
                        manager.loadError = manager.getGameStatistics().loadStatistics(node);
                    }
                    if (node.getNodeName().equals("gamestate") && manager.loadError==0) {
                        manager.loadError = manager.getGameState().load(node);
                    }
                }
            });
        }
        // init the game if loading was successful
        if (loadError==0) {
            // signal a normal game start
            engine.contributeGameState(GameEngine.ACTION_NEWGAME);
            engine.contributeGameState(GameEngine.ACTION_NEWROUND);
            engine.contributeGameState(GameEngine.ACTION_NEWTURN);
            // save the scores to the config file
            getGameStatistics().saveScoreAndGames();
            // look what to do
            if (engine.isActiveRound()) {
                // test if turn is finished
                if (getGameRules().isTurnFinished(getGameState()) || engine.getCurrentMove()==0) {
                    // start a new turn
                    engine.newTurn();
                } else {
		            // just let the player do it's move
		            engine.doPlayerMove();
                }
            } else if (engine.isActiveGame()){
                engine.contributeGameState(GameEngine.ACTION_AFTERMOVE);
                // the round is not active, activate it automatically if the user can't do that
                if (!getGameConfig().isInterruptAfterRound() || engine.getCurrentRound()==0) {
                    engine.newRound();
                }
            } else {
                // contribute a game finished
                engine.contributeGameState(GameEngine.ACTION_GAMEFINISHED);
            }
        } else {
            engine.stopGame();
        }
        return loadError;
    }

    /**
     * Saves the data of the gameInfoMap.
     *
     * @param doc The document object.
     * @param root The root node.
     */
    private void saveNewGameInformation(Document doc, Element root) {
        Element ngi = doc.createElement("newgameinfo");
        String[] keys = getNewGameInformationKeys();
        for (int i=0; i<keys.length; i++) {
            Element info = doc.createElement("info");
            info.setAttribute("key", keys[i]);
            Object value = getNewGameInformation(keys[i]);
            info.setAttribute("value", value.toString());
            ngi.appendChild(info);
        }
        root.appendChild(ngi);
    }

    /**
     * Loads the data for the gameInfoMap.
     *
     * @param node The node containg the game information.
     */
    private void loadNewGameInformation(Node node) {
        ChildNodeIterator.run(new ChildNodeIterator(node, "newgameinfo", this) {

            @Override
            public void performNode(Node node, int index, Object obj) {
                if (node.getNodeName().equals("info")) {
                    String key = HGBaseXMLTools.getAttributeValue(node, "key");
                    String value = HGBaseXMLTools.getAttributeValue(node, "value");
                    setNewGameInformation(key, value);
                }
            }
        });
    }

}
