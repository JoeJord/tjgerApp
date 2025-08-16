package com.tjger.gui.completed;

import java.util.List;
import java.util.stream.Collectors;

public class SoundSet extends GameElementSet {
    /**
     * Constructs a new instance.
     *
     * @param soundType The type of the sound set.
     * @param name      The name of the sound set.
     * @param hidden    The flag if the sound set is hidden.
     */
    public SoundSet(String soundType, String name, boolean hidden) {
        super(soundType, name, hidden, false);
    }

    /**
     * Returns all sounds from this set.
     *
     * @return All sounds from this set.
     */
    public List<Sound> getSounds() {
        return getElements().stream().filter(Sound.class::isInstance).map(Sound.class::cast).collect(Collectors.toList());
    }

    /**
     * Returns the sound with the specified sequence or {@code null}.
     *
     * @param sequence The sequence of the sound in this set.
     * @return The sound with the specified sequence or {@code null}.
     */
    public Sound getSound(int sequence) {
        return getSounds().stream().filter(sound -> sound.getSequence() == sequence).findFirst().orElse(null);
    }
}
