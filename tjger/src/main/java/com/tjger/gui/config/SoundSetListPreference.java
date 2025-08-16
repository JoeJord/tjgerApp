package com.tjger.gui.config;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tjger.R;
import com.tjger.game.completed.GameConfig;
import com.tjger.gui.completed.Sound;
import com.tjger.lib.SetSoundPlayer;

import at.hagru.hgbase.gui.config.HGBaseSoundListPreference;
import at.hagru.hgbase.lib.HGBaseText;

/**
 * A preference to select a sound set with has a button to play the sounds of the selected set.
 */
public class SoundSetListPreference extends HGBaseSoundListPreference {
    /**
     * The player which is used to play the sound.
     */
    private final SetSoundPlayer soundPlayer;

    /**
     * Constructs a new instance.
     *
     * @param context     The context for the preference.
     * @param soundPlayer The player which is used to play the sound.
     */
    public SoundSetListPreference(Context context, SetSoundPlayer soundPlayer) {
        super(context, null);
        this.soundPlayer = soundPlayer;
    }

    @Override
    protected void onPlayButtonClicked() {
        showSoundSetDialog(getValue());
    }

    /**
     * Handles a click on the play button of a sound in the sound set.
     *
     * @param sound The sound from which the play button was clicked.
     */
    protected void onSetSoundPlayButtonClicked(Sound sound) {
        if (soundPlayer == null || sound == null) {
            return;
        }
        soundPlayer.play(sound);
    }

    /**
     * Shows the dialog with the sounds of the sound set.
     *
     * @param soundSetName The name of the sound set.
     */
    protected void showSoundSetDialog(String soundSetName) {
        Context context = getContext();
        buildDialog(context, createSoundsView(context, loadSounds(getKey(), soundSetName))).show();
    }

    /**
     * Returns the sounds of the sound set.
     *
     * @param soundSetType The type of the sound set.
     * @param soundSetName The name of the sound set.
     * @return The sounds of the sound set.
     */
    protected Sound[] loadSounds(String soundSetType, String soundSetName) {
        return GameConfig.getInstance().getSoundSet(soundSetType, soundSetName).getSounds().toArray(Sound[]::new);
    }

    /**
     * Returns the view which lists the specified sounds.
     *
     * @param context The context the view is running in.
     * @param sounds  The sounds to be listed.
     * @return The view which lists the specified sounds.
     */
    protected View createSoundsView(Context context, Sound[] sounds) {
        ListView view = new ListView(context);
        view.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_2, android.R.id.text1, sounds) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.sound_set_list_item, parent, false);
                    holder = new ViewHolder();
                    holder.text = convertView.findViewById(R.id.soundset_sound_name);
                    holder.button = convertView.findViewById(R.id.play_soundset_sound_button);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Sound sound = sounds[position];
                holder.text.setText(getSoundListEntryText(context, sound));
                holder.button.setOnClickListener(v -> onSetSoundPlayButtonClicked(sound));

                return convertView;
            }
        });
        return view;
    }

    /**
     * Returns the text which is displayed for the specified sound in the sounds list.
     *
     * @param context The context the list view is running in.
     * @param sound   The sound of the list entry.
     * @return The text which is displayed for the specified sound in the sounds list.
     */
    protected String getSoundListEntryText(Context context, Sound sound) {
        return context.getString(R.string.sound_name_with_sequence, HGBaseText.getText(sound.getName()), String.valueOf(sound.getSequence()));
    }

    /**
     * Builds the dialog which displays the sounds of the set.
     *
     * @param context The parent context.
     * @param view    The view to use as the contents of the dialog.
     * @return The created dialog.
     */
    protected AlertDialog.Builder buildDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.settings_sounds);
        builder.setView(view);
        builder.setNegativeButton(R.string.button_close, null);
        return builder;
    }

    /**
     * ViewHolder class for a single item in the sound set list.
     * <p>
     * This class holds references to the views within a list item layout (a {@code TextView} for the sound name and an {@code ImageButton} to play the sound).<br>
     * It is used to avoid repeated calls to {@code findViewById} during list scrolling, improving the performance and efficiency of the list view.
     * </p>
     * Used within an adapter to display a list of individual sounds in a sound set dialog.
     */
    private static class ViewHolder {
        /**
         * Text view displaying the name of the sound file.
         */
        TextView text;
        /**
         * Button to play the associated sound.
         */
        ImageButton button;
    }
}
