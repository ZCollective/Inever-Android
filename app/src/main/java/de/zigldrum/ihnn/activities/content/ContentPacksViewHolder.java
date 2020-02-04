package de.zigldrum.ihnn.activities.content;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.R;

class ContentPacksViewHolder extends RecyclerView.ViewHolder {

    final TextView description;
    final TextView name;
    final Switch toggleSwitch;

    ContentPacksViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.rec_packs_name);
        description = view.findViewById(R.id.rec_packs_desc);
        toggleSwitch = view.findViewById(R.id.rec_packs_enable);
    }
}

