package de.zigldrum.ihnn.activities.content;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.ContentPacks;
import de.zigldrum.ihnn.networking.objects.ContentPack;

public class ContentPacksAdapter extends RecyclerView.Adapter<ContentPacksViewHolder> {

    private final List<ContentPack> packs;
    private final ContentPacks app;

    public ContentPacksAdapter(@NonNull Collection<ContentPack> packs, @NonNull ContentPacks app) {
        this.packs = new ArrayList<>(packs);
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
        viewHolder.name.setText(cp.getName());
        viewHolder.description.setText(cp.getDescription());
        viewHolder.id.setText(String.valueOf(cp.getId()));
        viewHolder.toggleSwitch.setChecked(!app.state.getDisabledPacks().contains(cp.getId()));
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }
}
