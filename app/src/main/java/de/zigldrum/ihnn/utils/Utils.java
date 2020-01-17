package de.zigldrum.ihnn.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import de.zigldrum.ihnn.R;

public class Utils {

    private static final String LOG_TAG = "Utils";

    public static void showLongToast (Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
    public static void showShortToast (Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void setMainProgressVisible(Activity activity, boolean isVisible){
        ProgressBar progressBar = activity.findViewById(R.id.main_progress);
        if (progressBar == null) {
            Log.w(LOG_TAG, "Cannot get Progressbar!");
        } else {
            progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }
    public static void setMainProgressProgress(Activity activity, boolean indeterminate, int progress){
        ProgressBar progressBar = activity.findViewById(R.id.main_progress);
        if (progressBar == null) {
            Log.w(LOG_TAG, "Cannot get Progressbar!");
        } else {
            if (indeterminate) progressBar.setIndeterminate(true);
            else {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(progress);
            }
        }
    }
}
