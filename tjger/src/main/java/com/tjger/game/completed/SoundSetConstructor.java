package com.tjger.game.completed;

import com.tjger.gui.completed.Sound;
import com.tjger.gui.completed.SoundSet;

import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;

import at.hagru.hgbase.lib.HGBaseLog;

abstract class SoundSetConstructor extends GameElementSetConstructor<SoundSet, Sound> {
    /**
     * The map of all available sets per type.
     */
    protected final Map<String, List<SoundSet>> setMap;

    /**
     * Constructs a new instance.
     *
     * @param typeConfigKey    The configuration key for the type of the sound.
     * @param setConfigKey     The configuration key for the set.
     * @param elementConfigKey The configuration key for the set elements.
     * @param fileConfigKey    The configuration key for the filename from a sound.
     * @param setMap           The map of all available sets per type.
     */
    protected SoundSetConstructor(String typeConfigKey, String setConfigKey, String elementConfigKey, String fileConfigKey, Map<String, List<SoundSet>> setMap) {
        super(typeConfigKey, setConfigKey, elementConfigKey, fileConfigKey);
        this.setMap = setMap;
    }

    /**
     * Constructs a new sound set.
     *
     * @param node   The sound set node.
     * @param config The game configuration object.
     */
    public static void construct(Node node, GameConfig config) {
        new SoundSetConstructor(GameConfigFileReader.CONFIG_SOUNDS, GameConfigFileReader.CONFIG_SOUNDSET, GameConfigFileReader.CONFIG_SOUND, GameConfigFileReader.CONFIG_FILE, config.soundSetMap) {
        }.run(node, config);
    }

    @Override
    protected String getAlternativeSetType(Node node) {
        // not needed
        return null;
    }

    @Override
    protected void logTypeMissing(String setName) {
        HGBaseLog.logWarn("No type defined for sound set '" + setName + "'!");

    }

    @Override
    protected SoundSet createSet(String type, String name, boolean hidden, Node node, GameConfig config) {
        return new SoundSet(type, name, hidden);
    }

    @Override
    protected Sound createElement(SoundSet set, Node node, int sequence, boolean proTeaser, GameConfig config) {
        return new Sound(set.getType(), set.getName(), sequence, getElementFilePath(node, sequence, set, config), false, proTeaser);
    }

    @Override
    protected String getElementFileName(Node node, SoundSet set) {
        return set.getType();
    }

    @Override
    protected boolean isTypeNeeded() {
        return (setMap != null);
    }

    @Override
    protected void addSetToCollection(SoundSet set, String type) {
        if (setMap != null) {
            GameConfigFileReader.getListFromMap(setMap, type).add(set);
        }
    }
}
