package de.zigldrum.ihnn.activities.content;

import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.ContentPacks;

class ContentPacksViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public EditText id;

    TextView description;
    Switch toggleSwitch;

    ContentPacksViewHolder(View v, ContentPacks app) {
        super(v);

        id = v.findViewById(R.id.rec_packs_id);
        name = v.findViewById(R.id.rec_packs_name);
        description = v.findViewById(R.id.rec_packs_desc);
        toggleSwitch = v.findViewById(R.id.rec_packs_enable);

        toggleSwitch.setOnClickListener(v1 -> {
            if (toggleSwitch.isChecked()) {
                app.enablePack(Integer.valueOf(id.getText().toString()));
            } else {
                app.disablePack(Integer.valueOf(id.getText().toString()));
            }
        });
    }
}
