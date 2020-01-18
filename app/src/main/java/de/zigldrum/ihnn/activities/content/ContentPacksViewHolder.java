package de.zigldrum.ihnn.activities.content;

import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.ContentPacks;

class ContentPacksViewHolder extends RecyclerView.ViewHolder {

    TextView description, name;
    EditText id;
    Switch toggleSwitch;

    ContentPacksViewHolder(View view, ContentPacks app) {
        super(view);

        id = view.findViewById(R.id.rec_packs_id);
        name = view.findViewById(R.id.rec_packs_name);
        description = view.findViewById(R.id.rec_packs_desc);
        toggleSwitch = view.findViewById(R.id.rec_packs_enable);

        toggleSwitch.setOnClickListener(v -> {
            if (toggleSwitch.isChecked()) {
                app.enablePack(Integer.valueOf(id.getText().toString()));
            } else {
                app.disablePack(Integer.valueOf(id.getText().toString()));
            }
        });
    }
}

