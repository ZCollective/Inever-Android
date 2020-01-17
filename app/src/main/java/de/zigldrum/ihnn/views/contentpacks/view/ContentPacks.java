package de.zigldrum.ihnn.views.contentpacks.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.finals.ContentPacksResults;
import de.zigldrum.ihnn.objects.AppState;
import de.zigldrum.ihnn.views.contentpacks.objects.ContentPacksAdapter;

import static de.zigldrum.ihnn.finals.ContentPacksResults.DEFAULT;
import static de.zigldrum.ihnn.finals.ContentPacksResults.UPDATED;

public class ContentPacks extends AppCompatActivity {

    private static final String LOG_TAG = "ContentPacks";

    private RecyclerView rView;
    private RecyclerView.LayoutManager rLayout;
    private RecyclerView.Adapter rAdapter;

    public AppState state;

    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_packs);
        state = AppState.loadState(getFilesDir());
        rView = findViewById(R.id.packs_list_view);

        // Performance improvement
        rView.setHasFixedSize(true);

        // Using linear layout
        rLayout = new LinearLayoutManager(this);
        rView.setLayoutManager(rLayout);

        // Setting adapter class
        rAdapter = new ContentPacksAdapter(state.getPacks(), this);
        rView.setAdapter(rAdapter);
    }

    public void goBack(View v) {
        finish();
    }

    public void enablePack(Integer id){
        this.updated = true;
        state.getDisabledPacks().remove(id);
        Log.d(LOG_TAG, "Enabling Pack: " + id);
    }

    public void disablePack(Integer id) {
        this.updated = true;
        state.getDisabledPacks().add(id);
        Log.d(LOG_TAG, "Disabling Pack: " + id);
    }

    @Override
    public void finish() {
        if (updated) state.saveState(getFilesDir());
        Intent data = new Intent();
        int resultCode = updated ? UPDATED : DEFAULT;
        setResult(resultCode, data);
        super.finish();
    }
}
