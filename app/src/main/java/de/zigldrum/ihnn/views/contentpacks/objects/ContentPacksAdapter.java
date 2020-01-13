package de.zigldrum.ihnn.views.contentpacks.objects;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.objects.ContentPack;
import de.zigldrum.ihnn.views.contentpacks.view.ContentPacks;

public class ContentPacksAdapter extends RecyclerView.Adapter<ContentPacksViewHolder> {

    private List<ContentPack> packs;
    private ContentPacks app;

    public ContentPacksAdapter (List<ContentPack> packs, ContentPacks app) {
        this.packs = packs;
        this.app = app;
    }

    @NonNull
    @Override
    public ContentPacksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_contentpack_row, viewGroup, false);

        ContentPacksViewHolder vh = new ContentPacksViewHolder(view, app);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentPacksViewHolder contentPacksViewHolder, int i) {
        ContentPack cp = packs.get(i);
        contentPacksViewHolder.name.setText(cp.getName());
        contentPacksViewHolder.description.setText(cp.getDescription());
        contentPacksViewHolder.id.setText("" + cp.getId());
        contentPacksViewHolder.toggleSwitch.setChecked(!app.state.getDisabledPacks().contains(cp.getId()));
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }
}
