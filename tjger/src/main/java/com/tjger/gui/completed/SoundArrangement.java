package com.tjger.gui.completed;

import com.tjger.game.completed.GameConfig;
import com.tjger.lib.ConstantValue;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SoundArrangement extends Sound {
    /**
     * The map of the used sounds.
     */
    private final Map<String, String> mapSounds = new LinkedHashMap<>();
    /**
     * The map of the used sound sets.
     */
    private final Map<String, String> mapSoundSets = new LinkedHashMap<>();

    /**
     * Constructs a new instance.
     *
     * @param name      The name of the sound arrangement.
     * @param proTeaser The flag if the sound arrangement is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param productId The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     */
    public SoundArrangement(String name, boolean proTeaser, String productId) {
        super(ConstantValue.CONFIG_SOUND_ARRANGEMENT, name, null, false, proTeaser, productId);
    }

    /**
     * Sets the used sound for the specified type in this sound arrangement.
     *
     * @param type The type of the sound to set.
     * @param name The name of the used sound.
     */
    public void setSound(String type, String name) {
        mapSounds.put(type, name);
    }

    /**
     * Sets the used sound set for the specified type in this sound arrangement.
     *
     * @param type The type of the sound set to set.
     * @param name The name of the used sound set.
     */
    public void setSoundSet(String type, String name) {
        mapSoundSets.put(type, name);
    }

    /**
     * Returns the used sound for the specified type in this sound arrangement.
     *
     * @param type The type of the sound.
     * @return The used sound for the specified type in this sound arrangement.
     */
    public Sound getSound(String type) {
        return GameConfig.getInstance().getSound(type, mapSounds.get(type));
    }

    /**
     * Returns the used sound set for the specified type in this sound arrangement.
     *
     * @param type The type of the sound set.
     * @return The used sound set for the specified type in this sound arrangement.
     */
    public SoundSet getSoundSet(String type) {
        return GameConfig.getInstance().getSoundSet(type, mapSoundSets.get(type));
    }

    /**
     * Returns all type that are included in this arrangement.
     *
     * @return All type that are included in this arrangement.
     */
    public Set<String> getTypes() {
        Set<String> types = new HashSet<>();
        types.addAll(mapSounds.keySet());
        types.addAll(mapSoundSets.keySet());
        return types;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        SoundArrangement that = (SoundArrangement) other;
        return Objects.equals(mapSounds, that.mapSounds) && Objects.equals(mapSoundSets, that.mapSoundSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapSounds, mapSoundSets);
    }
}
