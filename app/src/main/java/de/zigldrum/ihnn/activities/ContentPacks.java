package de.zigldrum.ihnn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.content.ContentPacksAdapter;
import de.zigldrum.ihnn.utils.AppState;

import static de.zigldrum.ihnn.utils.Constants.ContentPacksResults.DEFAULT;
import static de.zigldrum.ihnn.utils.Constants.ContentPacksResults.UPDATED;

public class ContentPacks extends AppCompatActivity {

    private static final String LOG_TAG = "ContentPacks";
    public AppState state;
    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_packs);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        state = AppState.loadState(getFilesDir());  // This operation is very expensive

        RecyclerView rView = findViewById(R.id.packs_list_view);

        // Performance improvement
        rView.setHasFixedSize(true);

        // Using linear layout
        RecyclerView.LayoutManager rLayout = new LinearLayoutManager(this);
        rView.setLayoutManager(rLayout);

        // Setting adapter class
        RecyclerView.Adapter rAdapter = new ContentPacksAdapter(state.getPacks(), this);
        rView.setAdapter(rAdapter);
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
        if (updated) state.saveState(getFilesDir());
        Intent data = new Intent();
        int resultCode = updated ? UPDATED : DEFAULT;
        setResult(resultCode, data);
        super.finish();
    }
}
