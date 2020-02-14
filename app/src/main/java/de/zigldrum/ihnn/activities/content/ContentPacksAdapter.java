package de.zigldrum.ihnn.activities.content;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.utils.AppState;

public class ContentPacksAdapter extends RecyclerView.Adapter<ContentPacksAdapter.ContentPacksViewHolder> {

    private static final String LOG_TAG = "ContentPacksAdapter";

    private final List<ContentPack> packs;
    private final PackSwitchCallback app;
    private final AppState state = AppState.getInstance(null);

    public ContentPacksAdapter(@NonNull Collection<ContentPack> packs, @NonNull PackSwitchCallback app) {
        this.packs = new ArrayList<>(packs);
        this.app = app;
    }

    @NonNull
    @Override
    public ContentPacksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.recycler_contentpack_row, viewGroup, false);

        return new ContentPacksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentPacksViewHolder viewHolder, int position) {
        ContentPack cp = packs.get(position);
        final int id = cp.getId();

        viewHolder.name.setText(cp.getName());
        viewHolder.description.setText(cp.getDescription());
        viewHolder.toggleSwitch.setChecked(!state.getDisabledPacks().contains(id));
        viewHolder.toggleSwitch.setOnCheckedChangeListener((view, checked) -> {
            if (checked) {
                Log.d(LOG_TAG, "Enabling Pack: id=" + id);
                state.getDisabledPacks().remove(id);
            } else {
                Log.d(LOG_TAG, "Disabling Pack: id=" + id);
                state.getDisabledPacks().add(id);
            }

            app.setStateUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }

    public interface PackSwitchCallback {
        void setStateUpdated();

    }

    static class ContentPacksViewHolder extends RecyclerView.ViewHolder {

        private final TextView description;
        private final TextView name;
        private final Switch toggleSwitch;

        ContentPacksViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.rec_packs_name);
            description = view.findViewById(R.id.rec_packs_desc);
            toggleSwitch = view.findViewById(R.id.rec_packs_enable);
        }
    }
}
