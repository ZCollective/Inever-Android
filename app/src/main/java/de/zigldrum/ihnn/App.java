package de.zigldrum.ihnn;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import de.zigldrum.ihnn.networking.services.RequesterService;
import de.zigldrum.ihnn.utils.AppState;
import io.paperdb.Paper;

/**
 * Expensive initialization-tasks will happen here, so that everything afterwards runs fast
 */
public class App extends Application {

    private static final String LOG_TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "Running App::onCreate()");

        // Init No-SQL-Database
        Paper.init(this);

        // Init App-State
        if (!AppState.init(this)) {
            // If an error occurs, AppState will delete its database automatically, so we try again
            if (!AppState.init(this)) {
                Toast.makeText(this, R.string.info_contact_developer, Toast.LENGTH_LONG).show();
            }
        }

        // Init REST-Service
        RequesterService.init();
    }
}
