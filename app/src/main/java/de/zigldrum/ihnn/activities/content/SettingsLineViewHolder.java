package de.zigldrum.ihnn.activities.content;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.R;

class SettingsLineViewHolder extends RecyclerView.ViewHolder {

    final TextView text;
    final Switch toggleSwitch;

    SettingsLineViewHolder(View view) {
        super(view);
        text = view.findViewById(R.id.rec_settings_text);
        toggleSwitch = view.findViewById(R.id.rec_settings_switch);
    }
}

