package com.tjger.gui.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.ProductDetails;
import com.tjger.MainFrame;
import com.tjger.R;
import com.tjger.game.completed.GameConfig;
import com.tjger.gui.completed.GameElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import at.hagru.hgbase.gui.config.HGBaseConfigStateDialog;
import at.hagru.hgbase.gui.config.HGBaseConfigTools;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.billing.HGBaseBillingHelper;
import at.hagru.hgbase.lib.internal.billing.HGBaseBillingListener;

/**
 * The dialog template for choosing game elements.
 *
 * @param <A> The type of the arrangement.
 */
public abstract class GameElementsDlg<A extends GameElement> extends HGBaseConfigStateDialog implements OnSharedPreferenceChangeListener, HGBaseBillingListener {
    /**
     * The arrangement when the user combines elements that are no predefined arrangement.
     */
    private static final UserDefinedArrangement ARRANGE_USERDEFINED = new UserDefinedArrangement();
    /**
     * The configuration key for the arrangements.
     */
    private final String configKeyArrangement;
    /**
     * The list all configuration items for all configurable game elements.
     */
    protected List<ConfigItem> configItemList;
    /**
     * The flag if it is allowed to modify an arrangement.
     */
    private boolean completeArrangement;
    /**
     * The flag if the arrangement is currently changing.
     */
    private boolean onChangeArrangement;

    /**
     * Constructs a new instance.
     *
     * @param dlgTitle             The title of the dialog.
     * @param configKeyArrangement The configuration key for the arrangement.
     */
    protected GameElementsDlg(String dlgTitle, String configKeyArrangement) {
        super(dlgTitle);
        this.configKeyArrangement = configKeyArrangement;
        onChangeArrangement = false;
        HGBaseConfig.getPreferences().registerOnSharedPreferenceChangeListener(this);
        HGBaseBillingHelper.getInstance().addListener(this);
    }

    @Override
    protected void createComponents() {
        completeArrangement = isCompleteArrangement();
        loadAndAddConfigItems();
    }

    /**
     * Loads the configuration items and adds a preference to the dialog for each item.
     */
    protected void loadAndAddConfigItems() {
        onChangeArrangement = true;
        configItemList = loadConfigItems();
        configItemList.removeIf(Objects::isNull);
        addPreferences(configItemList);
        onChangeArrangement = false;
    }

    /**
     * Reloads the dialog.
     */
    protected void reloadDialog() {
        PreferenceScreen screen = getPreferenceScreen();
        if (screen != null) {
            screen.removeAll();
        }
        loadAndAddConfigItems();
    }

    /**
     * Returns {@code true} if the arrangement are complete and may not be changed.
     *
     * @return {@code true} if the arrangement are complete and may not be changed.
     */
    protected abstract boolean isCompleteArrangement();

    /**
     * Loads all configuration items that should be displayed.
     *
     * @return A list of configuration items that should be displayed.
     */
    protected abstract List<ConfigItem> loadConfigItems();

    /**
     * Adds a preference for each specified configuration item.
     *
     * @param configItems The list of configuration items.
     */
    protected void addPreferences(List<ConfigItem> configItems) {
        if ((configItems == null) || (configItems.isEmpty())) {
            return;
        }
        configItems.forEach(item -> addPreference(item.getPreference()));
    }

    /**
     * Creates a combobox with the specified game elements.
     *
     * @param id            The configuration id of the combobox.
     * @param elements      The game elements to choose from.
     * @param activeElement The current active game element of the game element type.
     * @return The created combobox.
     * @noinspection deprecation
     */
    @SuppressWarnings("unchecked")
    protected final GameElementComboBox createGameElementComboBox(String id, GameElement[] elements, GameElement activeElement) {
        List<GameElement> elementsList = removeHiddenElements(elements);
        if (elementsList.isEmpty()) {
            return null;
        }
        GameElementComboBox comboBox = constructGameElementComboBox(id, elementsList.toArray(new GameElement[0]), activeElement);
        if (isArrangementCombobox(comboBox) && (!completeArrangement)) {
            comboBox.addItem(ARRANGE_USERDEFINED);
        }
        comboBox.getPreference().setEnabled(isComboboxChangeable(comboBox));

        return comboBox;
    }

    /**
     * Constructs a game element combobox.
     *
     * @param id            The configuration id of the combobox.
     * @param elements      The game elements to choose from.
     * @param activeElement The current active game element of the game element type.
     * @return The created combobox.
     */
    @NonNull
    protected GameElementComboBox constructGameElementComboBox(String id, GameElement[] elements, GameElement activeElement) {
        return new GameElementComboBox(id, elements, activeElement);
    }

    /**
     * Removes the hidden elements from the specified elements and returns the remaining as list.
     *
     * @param elements The elements to check.
     * @return The remaining elements as list.
     */
    protected List<GameElement> removeHiddenElements(GameElement[] elements) {
        List<GameElement> elementsList = new ArrayList<>(Arrays.asList(elements));
        elementsList.removeIf(GameElement::isHidden);
        return elementsList;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key == null) {
            return;
        }
        if (key.equals(configKeyArrangement)) {
            onArrangementChanged();
            return;
        }
        GameElementComboBox combobox = getCombobox(key);
        if (combobox != null) {
            onGameElementComboBoxChanged(combobox);
        }
    }

    /**
     * Handles the change of the selected arrangement.
     */
    protected void onArrangementChanged() {
        changeArrangement(getSelectedArrangement());
    }

    /**
     * Handles the change of the selected item of the specified combobox.
     *
     * @param combobox The combobox that has changed.
     */
    protected void onGameElementComboBoxChanged(GameElementComboBox combobox) {
        if (isArrangementElement(combobox.getOption())) {
            testArrangementModified();
        }
    }

    /**
     * Sets all elements of the specified arrangement to the value in the arrangement.
     *
     * @param arrangement The arrangement which provides the elements.
     */
    protected void changeArrangement(A arrangement) {
        if (arrangement == null) {
            testArrangementModified();
            return;
        }
        onChangeArrangement = true;
        Set<String> types = getArrangementTypes();
        types.forEach(type -> setConfigItemValue(type, getArrangementValue(arrangement, type)));
        onChangeArrangement = false;
    }

    /**
     * Tests if the arrangement has been modified.
     */
    @SuppressWarnings("unchecked")
    protected void testArrangementModified() {
        if ((!isArrangementAvailable()) || (isArrangementChanging())) {
            return;
        }
        GameElementComboBox arrangementCombobox = getArrangementCombobox();
        if (arrangementCombobox == null) {
            return;
        }
        A arrangement = findMatchingArrangement(getArrangementTypes());
        if (arrangement == null) {
            arrangement = (A) ARRANGE_USERDEFINED;
        }
        arrangementCombobox.setSelectedItem(arrangement);
    }

    /**
     * Returns the arrangement which includes the currently selected elements or {@code null} if none could be found.
     *
     * @param types The types of the game elements to check.
     * @return The arrangement which includes the currently selected elements or {@code null} if none could be found.
     */
    protected A findMatchingArrangement(Set<String> types) {
        return Arrays.stream(getAvailableArrangements()).filter(arrangement -> types.stream().allMatch(type -> isArrangementElement(arrangement, type))).findFirst().orElse(null);
    }

    /**
     * Returns all available arrangements.
     *
     * @return All available arrangements.
     */
    protected abstract A[] getAvailableArrangements();

    /**
     * Returns {@code true} if the specified game element type is part of the arrangements.
     *
     * @param type The type of the game element.
     * @return {@code true} if the specified game element type is part of the arrangements.
     */
    protected boolean isArrangementElement(String type) {
        return getArrangementTypes().contains(type);
    }

    /**
     * Returns {@code true} if the currently selected item of the configuration item identified by the specified type is an element of the specified arrangement.
     *
     * @param arrangement The arrangement.
     * @param type        The type of the configuration item.
     * @return {@code true} if the currently selected item of the configuration item identified by the specified type is an element of the specified arrangement.
     */
    protected abstract boolean isArrangementElement(A arrangement, String type);

    /**
     * Returns {@code true} if the specified selected element is the same as the specified arrangement element.<br>
     * Hidden arrangement elements will be ignored.
     *
     * @param arrangementElement The game element of the arrangement.
     * @param selectedElement    The selected game element.
     * @return {@code true} if the specified selected element is the same as the specified arrangement element.
     */
    protected boolean isArrangementElement(GameElement arrangementElement, GameElement selectedElement) {
        if ((arrangementElement != null) && (arrangementElement.isHidden())) {
            // Don't check hidden arrangement elements because they are not available in the dialog.
            return true;
        }
        return Objects.equals(arrangementElement, selectedElement);
    }

    /**
     * Returns {@code true} if it is allowed to change the selection of the specified combobox.
     *
     * @param combobox The combobox to check.
     * @return {@code true} if it is allowed to change the selection of the specified combobox.
     */
    protected boolean isComboboxChangeable(GameElementComboBox combobox) {
        return (combobox.getItemCount() != 1) && ((!completeArrangement) || (isArrangementCombobox(combobox)));
    }

    /**
     * Returns {@code true} if the arrangement configuration is available.
     *
     * @return {@code true} if the arrangement configuration is available.
     */
    protected boolean isArrangementAvailable() {
        return HGBaseTools.hasContent(configKeyArrangement);
    }

    /**
     * Returns {@code true} if the arrangement is currently changing the elements of the arrangement.
     *
     * @return {@code true} if the arrangement is currently changing the elements of the arrangement.
     */
    protected boolean isArrangementChanging() {
        return onChangeArrangement;
    }

    /**
     * Returns {@code true} if the specified combobox configuration key is the combobox for the arrangements.
     *
     * @param configKey The configuration key of the combobox to check.
     * @return {@code true} if the specified combobox configuration key is the combobox for the arrangements.
     */
    protected boolean isArrangementCombobox(String configKey) {
        return Objects.equals(configKeyArrangement, configKey);
    }

    /**
     * Returns {@code true} if the specified combobox is the combobox for the arrangements.
     *
     * @param combobox The combobox to check.
     * @return {@code true} if the specified combobox is the combobox for the arrangements.
     */
    protected boolean isArrangementCombobox(GameElementComboBox combobox) {
        return (combobox != null) && (isArrangementCombobox(combobox.getOption()));
    }

    /**
     * Returns the arrangement combobox.
     *
     * @return The arrangement combobox.
     */
    @SuppressWarnings("unchecked")
    protected GameElementComboBox getArrangementCombobox() {
        if ((configItemList == null) || (configItemList.isEmpty())) {
            return null;
        }
        return configItemList.stream().filter(GameElementComboBox.class::isInstance).map(GameElementComboBox.class::cast).filter(Objects::nonNull).filter(this::isArrangementCombobox).findFirst().orElse(null);
    }

    /**
     * Returns the selected arrangement.
     *
     * @return The selected arrangement.
     */
    @SuppressWarnings("unchecked")
    protected A getSelectedArrangement() {
        GameElementComboBox combobox = getArrangementCombobox();
        if (combobox == null) {
            return null;
        }
        return (A) combobox.getSelectedItem();
    }

    /**
     * Returns all configuration item types, which are affected by the arrangement.
     *
     * @return All configuration item types, which are affected by the arrangement.
     */
    protected abstract Set<String> getArrangementTypes();

    /**
     * Returns the value of the specified type in the arrangement.
     *
     * @param arrangement The arrangement.
     * @param type        The type of the game element.
     * @return The value of the specified type in the arrangement.
     */
    protected abstract Object getArrangementValue(A arrangement, String type);

    /**
     * Sets the value of the configuration item identified by the specified type.
     *
     * @param type  The type of the configuration item.
     * @param value The value to set.
     */
    protected abstract void setConfigItemValue(String type, Object value);

    /**
     * Returns the value of the configuration item identified by the specified type.
     *
     * @param type The type of the configuration item.
     * @return The value of the configuration item identified by the specified type.
     */
    protected abstract Object getConfigItemValue(String type);

    /**
     * Returns the configuration item for the specified type or {@code null}.
     *
     * @param type The type of the configuration item.
     * @return The configuration item for the specified type or {@code null}.
     */
    protected ConfigItem getConfigItem(String type) {
        if ((configItemList == null) || (configItemList.isEmpty()) || (!HGBaseTools.hasContent(type))) {
            return null;
        }
        return configItemList.stream().filter(item -> type.equals(item.getOption())).findFirst().orElse(null);
    }

    /**
     * Returns the combobox for the specified type or {@code null}.
     *
     * @param type The type of the configuration item.
     * @return The combobox for the specified type or {@code null}.
     */
    @SuppressWarnings("unchecked")
    protected GameElementComboBox getCombobox(String type) {
        ConfigItem configItem = getConfigItem(type);

        if (configItem instanceof GameElementsDlg.GameElementComboBox) {
            return (GameElementComboBox) configItem;
        }
        return null;
    }

    /**
     * Returns the selected value of the combobox for the specified type or {@code null}.
     *
     * @param type The type of the configuration item.
     * @return The selected value of the combobox for the specified type or {@code null}.
     */
    public Object getSelectedComboboxValue(String type) {
        GameElementComboBox comboBox = getCombobox(type);
        return (comboBox != null) ? comboBox.getSelectedItem() : null;
    }

    /**
     * Returns {@code true} if the specified game element is not already purchased.
     *
     * @param element The game element to check.
     * @return {@code true} if the specified game element is not already purchased.
     */
    protected boolean isUnpurchasedGameElement(GameElement element) {
        return GameConfig.getInstance().isUnpurchasedGameElement(element);
    }

    /**
     * Checks if the specified game element is purchased and launches the purchase flow if it is not purchased.
     *
     * @param gameElement The game element to check.
     */
    protected void checkGameElementPurchased(GameElement gameElement) {
        if (isUnpurchasedGameElement(gameElement)) {
            HGBaseBillingHelper.getInstance().launchPurchase(this, gameElement.getProductId());
        }
    }

    @Override
    protected void onPreferenceChange(Preference preference, Object newValue) {
        super.onPreferenceChange(preference, newValue);
        checkGameElementPurchased(getCombobox(preference.getKey()).getItem((String) newValue));
    }

    /**
     * @noinspection deprecation
     */
    @Override
    protected boolean canLeave(Preference preference, String newValue) {
        (new Handler()).post(this::setStateMessage); // The status message must be displayed with a slight delay, otherwise it cannot be determined correctly.
        return true;
    }

    /**
     * Sets the state message.
     */
    private void setStateMessage() {
        Integer warningMsg = getWarningMessage();
        if (warningMsg != null) {
            setWarnMessage(HGBaseText.getText(warningMsg));
        } else {
            setWarnMessage("");
        }
    }

    /**
     * Returns the message that should be displayed as a warning.
     *
     * @return The message that should be displayed as a warning.
     */
    private Integer getWarningMessage() {
        Integer unpurchasedWarning = getUnpurchasedWarning();
        if (unpurchasedWarning != null) {
            return unpurchasedWarning;
        }
        return getTeaserPartWarning();
    }

    /**
     * Returns {@code true} if at least one selected game element is only available in the pro version but should be shown in the free version as teaser for the the pro version.
     *
     * @param config The game configuration.
     * @return {@code true} if at least one selected game element is only available in the pro version but should be shown in the free version as teaser for the the pro version.
     */
    protected abstract boolean isProTeaserElementSelected(GameConfig config);

    /**
     * Returns the warning message if a teaser part is selected or {@code null} if no warning is needed.
     *
     * @return The warning message if a teaser part is selected or {@code null} if no warning is needed.
     */
    private Integer getTeaserPartWarning() {
        GameConfig gameConfig = GameConfig.getInstance();
        return (!gameConfig.isProVersion() && isProTeaserElementSelected(gameConfig)) ? R.string.teaser_part_warning : null;
    }

    /**
     * Returns {@code true} if at least one selected game element is not purchased.
     *
     * @param config The game configuration.
     * @return {@code true} if at least one selected game element is not purchased.
     */
    protected abstract boolean isUnpurchasedElementSelected(GameConfig config);

    /**
     * Returns the warning message if an unpurchased element is selected or {@code null} if no warning is needed.
     *
     * @return The warning message if an unpurchased element is selected or {@code null} if no warning is needed.
     */
    protected Integer getUnpurchasedWarning() {
        return isUnpurchasedElementSelected(GameConfig.getInstance()) ? R.string.unpurchased_warning : null;
    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onBackPressed() {
        MainFrame.getInstance().checkNewGame(); // Check if it is allowed to start a new game with the current selection.
        MainFrame.getInstance().checkResumeGame(); // Check if it is allowed to resume a game with the current selection.
        super.onBackPressed();
    }

    @Override
    public void onBillingError(@NonNull String message) {
        // Nothing to do.
    }

    @Override
    public void onProductsLoaded(@NonNull List<ProductDetails> products) {
        // Nothing to do.
    }

    @Override
    public void onPurchaseSuccess(@NonNull String productId) {
        if (GameConfig.getInstance().getGameElementsProductIds().contains(productId)) {
            // Reload the dialog when a game element was successfully purchased.
            runOnUiThread(this::reloadDialog);
        }
    }

    /**
     * Interface for configuration items.
     */
    protected interface ConfigItem {
        /**
         * Returns the preference for this configuration item.
         *
         * @return The preference for this configuration item.
         * @noinspection deprecation
         */
        Preference getPreference();

        /**
         * Returns the option of this item.
         *
         * @return The option of this item.
         */
        String getOption();
    }

    /**
     * Wrapper for displaying dots for user defined arrangements.
     */
    protected static class UserDefinedArrangement extends GameElement {
        /**
         * The configuration value of an user defined arrangement.
         */
        private static final String ARRANGE_USERDEFINED_ID = "arrangement_userdefined";

        /**
         * Constructs a new instance.
         */
        public UserDefinedArrangement() {
            super(ARRANGE_USERDEFINED_ID, ARRANGE_USERDEFINED_ID, false, false, null);
        }
    }

    /**
     * A combobox for game elements.
     */
    protected class GameElementComboBox implements ConfigItem {
        /**
         * The preference object.
         *
         * @noinspection deprecation
         */
        private final ListPreference preference;
        /**
         * The available game elements.
         */
        private final GameElement[] elements;

        /**
         * Constructs a new instance.
         *
         * @param id              The id of the combobox.
         * @param elements        The available elements in the combobox.
         * @param selectedElement The selected element.
         */
        public GameElementComboBox(String id, GameElement[] elements, GameElement selectedElement) {
            this.elements = elements;
            String[] values = HGBaseTools.toStringIdArray(elements);
            String defaultValue = (selectedElement == null) ? "" : selectedElement.getId();
            preference = createPreference(id, values, defaultValue);
        }

        /**
         * Creates the list preference for the combobox.
         *
         * @param id           The id of the combobox.
         * @param values       A list of values that can be selected.
         * @param defaultValue The default value.
         * @return The created preference.
         */
        protected ListPreference createPreference(String id, String[] values, String defaultValue) {
            ListPreference listPreference = HGBaseConfigTools.createListPreference(GameElementsDlg.this, id, values, defaultValue, true);
            addUnpurchasedInformation(listPreference);
            return listPreference;
        }

        /**
         * Adds to each unpurchased entry of the specified preference an information about that.
         *
         * @param preference The preference to check.
         */
        protected void addUnpurchasedInformation(ListPreference preference) {
            CharSequence[] entries = preference.getEntries();
            for (int i = 0; i < entries.length; i++) {
                if (isUnpurchasedGameElement(elements[i])) {
                    entries[i] = entries[i] + " " + HGBaseText.getText("unpurchased_marker");
                }
            }
            preference.setEntries(entries);
        }

        /**
         * Adds the specified element to the combobox.
         *
         * @param element The element to add.
         * @noinspection deprecation
         */
        public void addItem(GameElement element) {
            if (element == null) {
                return;
            }
            CharSequence[] entries = preference.getEntries();
            CharSequence[] entryValues = preference.getEntryValues();

            List<CharSequence> newEntries = new ArrayList<>(Arrays.asList(entries));
            List<CharSequence> newEntryValues = new ArrayList<>(Arrays.asList(entryValues));

            newEntries.add(HGBaseText.getText(element.getId()));
            newEntryValues.add(element.getId());

            preference.setEntries(newEntries.toArray(new CharSequence[0]));
            preference.setEntryValues(newEntryValues.toArray(new CharSequence[0]));
        }

        /**
         * Returns the number of items in the combobox.
         *
         * @return The number of items in the combobox.
         */
        public int getItemCount() {
            return elements.length;
        }

        /**
         * @noinspection deprecation
         */
        @Override
        public Preference getPreference() {
            return preference;
        }

        /**
         * @noinspection deprecation
         */
        @Override
        public String getOption() {
            return preference.getKey();
        }

        /**
         * Returns the selected item.
         *
         * @return The selected item.
         * @noinspection deprecation
         */
        public GameElement getSelectedItem() {
            return getItem(preference.getValue());
        }

        /**
         * Sets the selected item.
         *
         * @param element The element to set.
         * @noinspection deprecation
         */
        public void setSelectedItem(GameElement element) {
            if (element == null) {
                return;
            }
            preference.setValue(element.getId());
        }

        /**
         * Returns the item with the specified id.
         *
         * @param id The id of the item to retrieve.
         * @return The item with the specified id.
         */
        public GameElement getItem(String id) {
            return (HGBaseTools.hasContent(id)) ? HGBaseTools.findItemById(elements, id) : null;
        }
    }
}
