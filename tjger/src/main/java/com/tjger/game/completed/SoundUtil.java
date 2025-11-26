package com.tjger.game.completed;

import com.tjger.gui.completed.Sound;
import com.tjger.gui.completed.SoundSet;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

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
     * Plays the specified sound, ignoring the current sound configuration (on/off).
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
     * Plays the sound specified by type and name.<br>
     * Use {@link SoundUtil#playSound(Sound)} as {@code soundPLayer} to play the sound ignoring the current sound configuration (on/off).<br>
     * Use {@link com.tjger.MainFrame#playAudio(Sound)} to play the sound only if the sound configuration allows it (on/off).
     *
     * @param type        The type of the sound to play.
     * @param name        The name of the sound to play.
     * @param soundPlayer The method that plays the sound.
     */
    public static void playSound(String type, String name, Consumer<Sound> soundPlayer) {
        soundPlayer.accept(GameConfig.getInstance().getSound(type, name));
    }

    /**
     * Plays the active sound of the specified type.<br>
     * Use {@link SoundUtil#playSound(Sound)} as {@code soundPLayer} to play the sound ignoring the current sound configuration (on/off).<br>
     * Use {@link com.tjger.MainFrame#playAudio(Sound)} to play the sound only if the sound configuration allows it (on/off).
     *
     * @param type        The type of the sound to play.
     * @param soundPlayer The method that plays the sound.
     */
    public static void playActiveSound(String type, Consumer<Sound> soundPlayer) {
        soundPlayer.accept(GameConfig.getInstance().getActiveSound(type));
    }

    /**
     * Plays from the specified soundset the sound with the specified sequence.<br>
     * Use {@link SoundUtil#playSound(Sound)} as {@code soundPLayer} to play the sound ignoring the current sound configuration (on/off).<br>
     * Use {@link com.tjger.MainFrame#playAudio(Sound)} to play the sound only if the sound configuration allows it (on/off).
     *
     * @param soundSet    The soundset from which the sound should be played.
     * @param sequence    The sequence of the sound in the soundset.
     * @param soundPlayer The method that plays the sound.
     */
    public static void playSoundSetSound(SoundSet soundSet, int sequence, Consumer<Sound> soundPlayer) {
        if (soundSet == null) {
            return;
        }
        soundPlayer.accept(soundSet.getSound(sequence));
    }

    /**
     * Plays a random sound from the specified soundset.<br>
     * Use {@link SoundUtil#playSound(Sound)} as {@code soundPLayer} to play the sound ignoring the current sound configuration (on/off).<br>
     * Use {@link com.tjger.MainFrame#playAudio(Sound)} to play the sound only if the sound configuration allows it (on/off).
     *
     * @param soundSet    The soundset from which a sound should be played.
     * @param soundPlayer The method that plays the sound.
     */
    public static void playSoundSetRandomSound(SoundSet soundSet, Consumer<Sound> soundPlayer) {
        if (soundSet == null) {
            return;
        }
        List<Sound> sounds = soundSet.getSounds();
        if ((sounds == null) || (sounds.isEmpty())) {
            return;
        }
        soundPlayer.accept(sounds.get(random.nextInt(sounds.size())));
    }

    /**
     * Plays from the active soundset of the specified type the sound with the specified sequence.<br>
     * Use {@link SoundUtil#playSound(Sound)} as {@code soundPLayer} to play the sound ignoring the current sound configuration (on/off).<br>
     * Use {@link com.tjger.MainFrame#playAudio(Sound)} to play the sound only if the sound configuration allows it (on/off).
     *
     * @param type        The type of the sound set.
     * @param sequence    The sequence of the sound in the soundset.
     * @param soundPlayer The method that plays the sound.
     */
    public static void playActiveSoundSetSound(String type, int sequence, Consumer<Sound> soundPlayer) {
        playSoundSetSound(GameConfig.getInstance().getActiveSoundSet(type), sequence, soundPlayer);
    }

    /**
     * Plays from the active soundset of the specified type a random sound.<br>
     * Use {@link SoundUtil#playSound(Sound)} as {@code soundPLayer} to play the sound ignoring the current sound configuration (on/off).<br>
     * Use {@link com.tjger.MainFrame#playAudio(Sound)} to play the sound only if the sound configuration allows it (on/off).
     *
     * @param type        The type of the soundset.
     * @param soundPlayer The method that plays the sound.
     */
    public static void playActiveSoundSetRandomSound(String type, Consumer<Sound> soundPlayer) {
        playSoundSetRandomSound(GameConfig.getInstance().getActiveSoundSet(type), soundPlayer);
    }
}
