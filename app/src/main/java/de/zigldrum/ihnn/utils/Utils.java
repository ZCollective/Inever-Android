package de.zigldrum.ihnn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.activities.ContentPacks;
import de.zigldrum.ihnn.activities.Game;
import de.zigldrum.ihnn.activities.ProposeQuestion;
import de.zigldrum.ihnn.activities.Settings;

public class Utils {

    private static final String LOG_TAG = "Utils";

    @Nullable
    public static <T> Snackbar showLongSnackbar(@NonNull Activity activity, @NonNull T text) {
        Snackbar result = null;

        if (text instanceof CharSequence) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            result = Snackbar.make(rootView, (CharSequence) text, Snackbar.LENGTH_LONG);
            result.show();
        } else if (text instanceof Integer) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            result = Snackbar.make(rootView, (Integer) text, Snackbar.LENGTH_LONG);
            result.show();
        }

        return result;
    }

    public static void setMainProgressVisible(@NonNull Activity activity, boolean isVisible) {
        ProgressBar progressBar = activity.findViewById(R.id.main_progress);
        if (progressBar == null) {
            Log.w(LOG_TAG, "Cannot get Progressbar!");
        } else {
            progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
