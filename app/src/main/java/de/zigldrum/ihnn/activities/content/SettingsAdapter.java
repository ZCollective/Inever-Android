package de.zigldrum.ihnn.activities.content;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.utils.AppState;
import de.zigldrum.ihnn.R;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsLineViewHolder> {

    private static final String LOG_TAG = "SettingsAdapter";

    private final SettingsSwitchCallback app;
    private final AppState state = AppState.getInstance(null);

    public SettingsAdapter(@NonNull SettingsSwitchCallback app) {
        this.app = app;
    }

    @NonNull
    @Override
    public SettingsLineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.recycler_settings_row, viewGroup, false);

        return new SettingsLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsLineViewHolder viewHolder, @IntRange(from = 0, to = 2) int position) {
        switch (position) {
            case 0:
                // NSFW-Choice
                viewHolder.text.setText(R.string.settings_enable_18_plus);
                viewHolder.toggleSwitch.setChecked(state.getEnableNSFW());
                viewHolder.toggleSwitch.setOnCheckedChangeListener((view, checked) -> {
                    state.setEnableNSFW(checked);
                    Log.i(LOG_TAG, "NSFW is " + (checked ? "enabled" : "disabled"));
                });
                break;
            case 1:
                // Auto-Update-Choice
                viewHolder.text.setText(R.string.settings_enable_content_update);
                viewHolder.toggleSwitch.setChecked(state.getEnableAutoUpdates());
                viewHolder.toggleSwitch.setOnCheckedChangeListener((view, checked) -> {
                    state.setEnableAutoUpdates(checked);
                    Log.i(LOG_TAG, "Auto Updates are " + (checked ? "enabled" : "disabled"));
                });
                break;
            case 2:
                // NSFW-only-Choice
                viewHolder.text.setText(R.string.settings_nsfw_only);
                viewHolder.toggleSwitch.setChecked(state.isOnlyNSFW());
                viewHolder.toggleSwitch.setOnCheckedChangeListener((view, checked) -> {
                    state.setOnlyNSFW(checked);
                    Log.i(LOG_TAG, "NSFW Only mode is " + (checked ? "enabled" : "disabled"));
                });
                break;
            default:
                Log.w(LOG_TAG, "Unknown settings-id: " + position);
                return;
        }

        app.setStateUpdated();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public interface SettingsSwitchCallback {
        void setStateUpdated();
    }

    static class SettingsLineViewHolder extends RecyclerView.ViewHolder {

        private final TextView text;
        private final Switch toggleSwitch;

        SettingsLineViewHolder(@NonNull View view) {
            super(view);
            text = view.findViewById(R.id.rec_settings_text);
            toggleSwitch = view.findViewById(R.id.rec_settings_switch);
        }
    }
}
