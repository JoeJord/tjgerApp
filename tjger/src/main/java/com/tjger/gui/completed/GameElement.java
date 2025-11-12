package com.tjger.gui.completed;

import androidx.annotation.NonNull;

import at.hagru.hgbase.lib.HGBaseItem;
import at.hagru.hgbase.lib.HGBaseText;

public class GameElement implements HGBaseItem, Comparable<GameElement> {
    /**
     * The type of the element.
     */
    private final String type;
    /**
     * The name of the element.
     */
    private final String name;
    /**
     * The flag of the element is hidden
     */
    private final boolean hidden;
    /**
     * The flag if the element is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    private final boolean proTeaser;
    /**
     * The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     */
    private final String productId;

    /**
     * Constructs a new instance.
     *
     * @param elementType The type of the element.
     * @param name        The name of the element.
     * @param hidden      The flag if the element is hidden.
     * @param proTeaser   The flag if the element is only available in the pro version but should be shown in the free version as teaser for the pro version.
     * @param productId   The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     */
    protected GameElement(String elementType, String name, boolean hidden, boolean proTeaser, String productId) {
        this.type = elementType;
        this.name = name;
        this.hidden = hidden;
        this.proTeaser = proTeaser;
        this.productId = productId;
    }

    /**
     * Returns the type of the element.
     *
     * @return The type of the element.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the name of this game element.
     *
     * @return The name of this game element.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns {@code true} if this game element is hidden.<br>
     * If the element is hidden, it will not be displayed in the settings dialog and can not be an active element.
     *
     * @return {@code true} if this game element is hidden.
     */
    public boolean isHidden() {
        return hidden;
    }

    @NonNull
    @Override
    public String toString() {
        return HGBaseText.getText(getName());
    }

    @Override
    public int compareTo(GameElement other) {
        if (other == null) {
            return 1;
        }
        return getName().compareToIgnoreCase(other.getName());
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof GameElement && this.toString().equals(other.toString()));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String getId() {
        return getName();
    }

    /**
     * Returns {@code true} if this game element is only available in the pro version but should be shown in the free version as teaser for the pro version.
     *
     * @return {@code true} if this game element is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    public boolean isProTeaser() {
        return proTeaser;
    }

    /**
     * Returns the id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     *
     * @return The id of the In-App-Purchase-Product. If not {@code null} then the element is only available if the product is purchased.
     */
    public String getProductId() {
        return productId;
    }
}
