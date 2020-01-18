package de.zigldrum.ihnn.views.contentpacks.objects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.views.contentpacks.view.ContentPacks;

public class ContentPacksAdapter extends RecyclerView.Adapter<ContentPacksViewHolder> {

    private final List<ContentPack> packs;
    private final ContentPacks app;

    public ContentPacksAdapter(List<ContentPack> packs, ContentPacks app) {
        this.packs = packs;
        this.app = app;
    }

    @NonNull
    @Override
    public ContentPacksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_contentpack_row, viewGroup, false);

        return new ContentPacksViewHolder(view, app);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentPacksViewHolder viewHolder, int i) {
        ContentPack cp = packs.get(i);
        viewHolder.id.setText(String.valueOf(cp.getId()));
        viewHolder.name.setText(cp.getName());
        viewHolder.description.setText(cp.getDescription());
        viewHolder.toggleSwitch.setChecked(!app.state.getDisabledPacks().contains(cp.getId()));
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }
}
