package com.tjger.game.completed;

import com.tjger.gui.completed.Sound;
import com.tjger.gui.completed.SoundSet;

import java.util.List;
import java.util.Random;

import at.hagru.hgbase.lib.HGBaseSound;

/**
 * Utilities to play a configured sound.
 */
public class SoundUtil {
    /**
     * Random number generator used for producing random values within this class.
     */
    private static final Random random = new Random();

    /**
     * Private constructor to prevent instantiation.
     */
    private SoundUtil() {
        // Nothing to do.
    }

    /**
     * Plays the specified sound.
     *
     * @param sound The sound to play.
     */
    public static void playSound(Sound sound) {
        if (sound == null) {
            return;
        }
        HGBaseSound.playAudio(sound.getFilename());
    }

    /**
     * Plays the sound specified by type and name.
     *
     * @param type The type of the sound to play.
     * @param name The name of the sound to play.
     */
    public static void playSound(String type, String name) {
        playSound(GameConfig.getInstance().getSound(type, name));
    }

    /**
     * Plays the active sound of the specified type.
     *
     * @param type The type of the sound to play.
     */
    public static void playActiveSound(String type) {
        playSound(GameConfig.getInstance().getActiveSound(type));
    }

    /**
     * Plays from the specified soundset the sound with the specified sequence.
     *
     * @param soundSet The soundset from which the sound should be played.
     * @param sequence The sequence of the sound in the soundset.
     */
    public static void playSoundSetSound(SoundSet soundSet, int sequence) {
        if (soundSet == null) {
            return;
        }
        playSound(soundSet.getSound(sequence));
    }

    /**
     * Plays a random sound from the specified soundset.
     *
     * @param soundSet The soundset from which a sound should be played.
     */
    public static void playSoundSetRandomSound(SoundSet soundSet) {
        if (soundSet == null) {
            return;
        }
        List<Sound> sounds = soundSet.getSounds();
        if ((sounds == null) || (sounds.isEmpty())) {
            return;
        }
        playSound(sounds.get(random.nextInt(sounds.size())));
    }

    /**
     * Plays from the active soundset of the specified type the sound with the specified sequence.
     *
     * @param type     The type of the sound set.
     * @param sequence The sequence of the sound in the soundset.
     */
    public static void playActiveSoundSetSound(String type, int sequence) {
        playSoundSetSound(GameConfig.getInstance().getActiveSoundSet(type), sequence);
    }

    /**
     * Plays from the active soundset of the specified type a random sound.
     *
     * @param type The type of the soundset.
     */
    public static void playActiveSoundSetRandomSound(String type) {
        playSoundSetRandomSound(GameConfig.getInstance().getActiveSoundSet(type));
    }
}
