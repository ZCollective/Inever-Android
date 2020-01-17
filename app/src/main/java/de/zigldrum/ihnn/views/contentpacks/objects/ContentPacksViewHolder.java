package de.zigldrum.ihnn.views.contentpacks.objects;

import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.views.contentpacks.view.ContentPacks;

public class ContentPacksViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView description;
    public EditText id;
    public Switch toggleSwitch;

    private ContentPacks app;

    public ContentPacksViewHolder(View v, ContentPacks app) {
        super(v);
        this.app = app;
        name = v.findViewById(R.id.rec_packs_name);
        description = v.findViewById(R.id.rec_packs_desc);
        id = v.findViewById(R.id.rec_packs_id);
        toggleSwitch = v.findViewById(R.id.rec_packs_enable);
        toggleSwitch.setOnClickListener(v1 -> {
            if(toggleSwitch.isChecked()) app.enablePack(Integer.valueOf(id.getText().toString()));
            else app.disablePack(Integer.valueOf(id.getText().toString()));
        });
    }
}

