package com.tjger.gui.internal;

import android.preference.ListPreference;

import androidx.annotation.NonNull;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.SoundUtil;
import com.tjger.gui.completed.GameElement;
import com.tjger.gui.completed.Sound;
import com.tjger.gui.completed.SoundArrangement;
import com.tjger.gui.completed.SoundSet;
import com.tjger.gui.config.SoundSetListPreference;
import com.tjger.lib.ConstantValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import at.hagru.hgbase.gui.config.HGBaseConfigTools;
import at.hagru.hgbase.gui.config.HGBaseSoundListPreference;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseText;

/**
 * The dialog for choosing the sounds.
 */
public class SoundsDlg extends GameElementsDlg<SoundArrangement> {
    /**
     * The message key for the sound settings dialog title.
     */
    private static final String MSG_SETTINGS_SOUNDS_DLG_TITLE = "settings_sounds";
    /**
     * The list of user defined sounds.
     */
    private final String[] userSounds;
    /**
     * The list of user defined sound sets.
     */
    private final String[] userSoundSets;

    /**
     * Constructs a new instance.
     */
    public SoundsDlg() {
        super(HGBaseText.getText(MSG_SETTINGS_SOUNDS_DLG_TITLE).replace('.', ' '), ConstantValue.CONFIG_SOUND_ARRANGEMENT);
        GameConfig config = GameConfig.getInstance();
        userSounds = config.getSoundTypes();
        userSoundSets = config.getSoundSetTypes();
    }

    @Override
    protected boolean isCompleteArrangement() {
        GameConfig config = GameConfig.getInstance();
        return ((config.isCompleteSoundArrangement()) && (config.getSoundArrangements().length > 0));
    }

    @Override
    protected List<ConfigItem> loadConfigItems() {
        GameConfig config = GameConfig.getInstance();
        List<ConfigItem> configItems = new ArrayList<>();
        addArrangements(configItems, config);
        addUserSoundSets(configItems, config);
        addUserSounds(configItems, config);
        return configItems;
    }

    @Override
    protected Set<String> getArrangementTypes() {
        return Arrays.stream(GameConfig.getInstance().getSoundArrangements()).flatMap(arrangement -> arrangement.getTypes().stream()).collect(Collectors.toSet());
    }

    @Override
    protected Object getArrangementValue(SoundArrangement arrangement, String type) {
        if (Arrays.asList(userSounds).contains(type)) {
            return arrangement.getSound(type);
        } else if (Arrays.asList(userSoundSets).contains(type)) {
            return arrangement.getSoundSet(type);
        }
        return null;
    }

    @Override
    protected SoundArrangement[] getAvailableArrangements() {
        return GameConfig.getInstance().getSoundArrangements();
    }

    @Override
    protected boolean isArrangementElement(SoundArrangement arrangement, String type) {
        if (isUserSoundSet(type)) {
            return Objects.equals(arrangement.getSoundSet(type), getSelectedSoundSet(type));
        } else if (isUserSound(type)) {
            return Objects.equals(arrangement.getSound(type), getSelectedSound(type));
        }
        return false; // Unknown type is not an element of the arrangement.
    }

    @Override
    protected void setConfigItemValue(String type, Object value) {
        ConfigItem configItem = getConfigItem(type);
        if (configItem instanceof GameElementsDlg.GameElementComboBox) {
            ((GameElementsDlg<?>.GameElementComboBox) configItem).setSelectedItem((GameElement) value);
        }
    }

    /**
     * Add a combobox for the arrangements.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    protected void addArrangements(List<ConfigItem> configItems, GameConfig config) {
        configItems.add(createGameElementComboBox(ConstantValue.CONFIG_SOUND_ARRANGEMENT, config.getSoundArrangements(), config.getActiveSoundArrangement()));
    }

    /**
     * Adds a combobox for each user defined sound set.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    protected void addUserSoundSets(List<ConfigItem> configItems, GameConfig config) {
        Set.of(userSoundSets).forEach(userSoundSetType -> configItems.add(createGameElementComboBox(userSoundSetType, config.getSoundSets(userSoundSetType), config.getActiveSoundSet(userSoundSetType))));
    }

    /**
     * Adds a combobox for each user defined sound.
     *
     * @param configItems The list of the configuration items.
     * @param config      The game configuration object.
     */
    protected void addUserSounds(List<ConfigItem> configItems, GameConfig config) {
        Set.of(userSounds).forEach(userSoundType -> configItems.add(createGameElementComboBox(userSoundType, config.getSounds(userSoundType), config.getActiveSound(userSoundType))));
    }

    @NonNull
    @Override
    protected GameElementsDlg<SoundArrangement>.GameElementComboBox constructGameElementComboBox(String id, GameElement[] elements, GameElement activeElement) {
        if (isUserSoundSet(id)) {
            return new SoundSetComboBox(id, Arrays.stream(elements).map(e -> (SoundSet) e).toArray(SoundSet[]::new), (SoundSet) activeElement);
        } else if (isUserSound(id)) {
            return new SoundComboBox(id, Arrays.stream(elements).map(e -> (Sound) e).toArray(Sound[]::new), (Sound) activeElement);
        }
        return super.constructGameElementComboBox(id, elements, activeElement);
    }

    /**
     * Returns {@code true} if the specified type is a user defined soundset.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is a user defined soundset.
     */
    private boolean isUserSoundSet(String type) {
        return Arrays.asList(userSoundSets).contains(type);
    }

    /**
     * Returns {@code true} if the specified type is a user defined sound.
     *
     * @param type The type to check.
     * @return {@code true} if the specified type is a user defined sound.
     */
    private boolean isUserSound(String type) {
        return Arrays.asList(userSounds).contains(type);
    }

    /**
     * Returns the currently selected soundset of the specified type.
     *
     * @param type The type of the soundset.
     * @return The currently selected soundset of the specified type.
     */
    private SoundSet getSelectedSoundSet(String type) {
        return (SoundSet) getSelectedComboboxValue(type);
    }

    /**
     * Returns the currently selected sound of the specified type.
     *
     * @param type The type of the sound.
     * @return The currently selected sound of the specified type.
     */
    private Sound getSelectedSound(String type) {
        return (Sound) getSelectedComboboxValue(type);
    }

    @Override
    protected Object getConfigItemValue(String type) {
        return Optional.ofNullable(getConfigItem(type)).filter(GameElementComboBox.class::isInstance).map(GameElementComboBox.class::cast).map(GameElementComboBox::getSelectedItem).orElse(null);
    }

    @Override
    protected boolean isProTeaserElementSelected(GameConfig config) {
        return config.isProTeaserSoundSelected();
    }

    /**
     * A combobox for sounds.
     */
    protected class SoundComboBox extends GameElementComboBox {
        /**
         * The id of the combobox
         */
        private final String id;

        /**
         * Constructs a new instance.
         *
         * @param id            The id of the combobox.
         * @param sounds        The available sounds in the combobox.
         * @param selectedSound The selected sound.
         */
        public SoundComboBox(String id, Sound[] sounds, Sound selectedSound) {
            super(id, sounds, selectedSound);
            this.id = id;
        }

        @Override
        protected ListPreference createPreference(String id, String[] values, String defaultValue) {
            HGBaseSoundListPreference listPreference = HGBaseConfigTools.createSoundListPreference(SoundsDlg.this, id, values, defaultValue, true, this::playSound);
            addUnpurchasedInformation(listPreference);
            return listPreference;
        }

        /**
         * Plays the specified sound.
         *
         * @param soundName The name of the sound to play.
         */
        protected void playSound(String soundName) {
            SoundUtil.playSound(id, soundName);
        }
    }

    /**
     * A combobox for sound sets.
     */
    protected class SoundSetComboBox extends GameElementComboBox {
        /**
         * Constructs a new instance.
         *
         * @param id               The id of the combobox.
         * @param soundSets        The available sound sets in the combobox.
         * @param selectedSoundSet The selected sound set.
         */
        public SoundSetComboBox(String id, SoundSet[] soundSets, SoundSet selectedSoundSet) {
            super(id, soundSets, selectedSoundSet);
        }

        @Override
        protected ListPreference createPreference(String id, String[] values, String defaultValue) {
            SoundSetListPreference list = new SoundSetListPreference(SoundsDlg.this, this::playSound);
            list.setKey(id);
            list.setTitle(HGBaseText.getText(id));
            list.setDefaultValue(defaultValue);
            list.setEntryValues(values);
            list.setEntries(generateListEntries(values));
            list.setValue(HGBaseConfig.get(id, defaultValue));
            list.setSummary("%s");
            addUnpurchasedInformation(list);
            return list;
        }

        /**
         * Generates the entries for the list preference bases on the specified values.
         *
         * @param values The values of list preference.
         * @return The entries for the list preference bases on the specified values.
         */
        protected String[] generateListEntries(String[] values) {
            return Arrays.stream(values).map(value -> (HGBaseText.existsText(value) ? HGBaseText.getText(value) : value)).toArray(String[]::new);
        }

        /**
         * Plays the specified sound.
         *
         * @param sound The sound to play.
         */
        protected void playSound(Sound sound) {
            SoundUtil.playSound(sound);
        }
    }
}
