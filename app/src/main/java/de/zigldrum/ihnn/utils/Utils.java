package de.zigldrum.ihnn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.ContentPacks;
import de.zigldrum.ihnn.activities.Game;
import de.zigldrum.ihnn.activities.ProposeQuestion;
import de.zigldrum.ihnn.activities.Settings;

public class Utils {

    private static final String LOG_TAG = "Utils";

    public static void showLongToast(@NonNull Context context, @NonNull String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void setMainProgressVisible(@NonNull Activity activity, boolean isVisible) {
        ProgressBar progressBar = activity.findViewById(R.id.main_progress);
        if (progressBar == null) {
            Log.w(LOG_TAG, "Cannot get Progressbar!");
        } else {
            progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public static Intent startGame(@NonNull Context ctx) {
        Intent intent = new Intent(ctx, Game.class);
        intent.putExtra("msg", "Have fun!");
        return intent;
    }

    public static Intent openSettings(@NonNull Context ctx) {
        Intent intent = new Intent(ctx, Settings.class);
        intent.putExtra("msg", "Go set some things!");
        return intent;
    }

    public static Intent openProposals(@NonNull Context ctx) {
        Intent intent = new Intent(ctx, ProposeQuestion.class);
        intent.putExtra("msg", "Go propose!");
        return intent;
    }

    public static Intent openContentManagement(@NonNull Context ctx) {
        Intent intent = new Intent(ctx, ContentPacks.class);
        intent.putExtra("msg", "Go manage!");
        return intent;
    }
}
