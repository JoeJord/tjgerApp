package com.tjger.gui.completed;

import java.util.Objects;

public class Sound extends GameElement {
    /**
     * The name of the sound file.
     */
    private final String filename;
    /**
     * The sequence number of the sound.
     */
    private final int sequence;

    /**
     * Constructs a new instance.
     *
     * @param soundType The type of the sound.
     * @param name      The name of the sound.
     * @param sequence  The sequence number of the sound.
     * @param filename  The name of the sound file.
     * @param hidden    The flag if the sound is hidden.
     * @param proTeaser The flag if the sound is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param productId The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     */
    public Sound(String soundType, String name, int sequence, String filename, boolean hidden, boolean proTeaser, String productId) {
        super(soundType, name, hidden, proTeaser, productId);
        this.sequence = sequence;
        this.filename = filename;
    }

    /**
     * Constructs a new instance.
     *
     * @param soundType The type of the sound.
     * @param name      The name of the sound.
     * @param filename  The name of the sound file.
     * @param hidden    The flag if the sound is hidden.
     * @param proTeaser The flag if the sound is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param productId The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     */
    public Sound(String soundType, String name, String filename, boolean hidden, boolean proTeaser, String productId) {
        this(soundType, name, 1, filename, hidden, proTeaser, productId);
    }

    /**
     * Returns the name of the sound file.
     *
     * @return The name of the sound file.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns the sequence number of the sound.
     *
     * @return The sequence number of the sound.
     */
    public int getSequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Sound) && (super.equals(other)) && (Objects.equals(filename, ((Sound) other).filename));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), filename);
    }
}
