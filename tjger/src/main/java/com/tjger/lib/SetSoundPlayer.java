package com.tjger.lib;

import com.tjger.gui.completed.Sound;

/**
 * An interface to play a sound from a sound set.
 */
@FunctionalInterface
public interface SetSoundPlayer {
    /**
     * Plays the specified sound.
     *
     * @param sound The sound to play.
     */
    void play(Sound sound);
}
