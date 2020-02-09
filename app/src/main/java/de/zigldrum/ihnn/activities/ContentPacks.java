package de.zigldrum.ihnn.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.content.ContentPacksAdapter;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.utils.AppState;

public class ContentPacks extends AppCompatActivity {

    private static final String LOG_TAG = "ContentPacks";

    public final AppState state = AppState.getInstance();

    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_packs);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.d(LOG_TAG, "Message: " + getIntent().getStringExtra("msg"));

        RecyclerView rView = findViewById(R.id.packs_list_view);

        // Performance improvement
        rView.setHasFixedSize(true);

        // Using linear layout
        rView.setLayoutManager(new LinearLayoutManager(this));

        // Sorting, so the packs are always at the same spot
        List<ContentPack> packs = new ArrayList<>(state.getPacks());
        Collections.sort(packs, (x, y) -> x.getId().compareTo(y.getId()));

        // Setting adapter class
        rView.setAdapter(new ContentPacksAdapter(packs, this));
    }

    public void goBack(View v) {
        finish();
    }

    public void enablePack(Integer id) {
        this.updated = true;
        Log.d(LOG_TAG, "Enabling Pack: " + id);
        state.getDisabledPacks().remove(id);
    }

    public void disablePack(Integer id) {
        this.updated = true;
        Log.d(LOG_TAG, "Disabling Pack: " + id);
        state.getDisabledPacks().add(id);
    }

    @Override
    public void finish() {
        if (updated) {
            if (state.saveState()) {
                Log.i(LOG_TAG, "Saving AppState after Updates!");
            } else {
                Log.w(LOG_TAG, "Could not save AppState!");
            }
        }

        super.finish();
    }
}
