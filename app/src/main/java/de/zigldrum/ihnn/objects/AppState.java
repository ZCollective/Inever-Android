package de.zigldrum.ihnn.objects;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.zigldrum.ihnn.BuildConfig;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.Question;

public class AppState implements Serializable {

    public final static String BASE_URL = BuildConfig.DEBUG ? "http://spackenserver.de:11337/" : "http://spackenserver.de:11337/";

    private static final long serialVersionUID = 2242379700997586835L;
    private static final String APP_STATE_FILE = "AppState.db";
    private static final String LOG_TAG = "AppState";

    private final List<ContentPack> packs;
    private final List<Question> questions;
    private final Set<Integer> disabledPacks;

    private boolean initialized;
    private boolean enableNSFW;
    private boolean enableAutoUpdates;
    private boolean onlyNSFW;

    public AppState() {
        this.packs = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.disabledPacks = new HashSet<>();

        this.initialized = false;
        this.enableNSFW = false;
        this.enableAutoUpdates = true;
        this.onlyNSFW = false;
    }

    public static AppState loadState(@NonNull File baseDir) {
        AppState state = null;

        try (ObjectInputStream stateIn = new ObjectInputStream(new FileInputStream(new File(baseDir, APP_STATE_FILE)))) {
            Log.d(LOG_TAG, "Attempting to parse AppState.");
            state = (AppState) stateIn.readObject();
            Log.d(LOG_TAG, "Successfully read AppState.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return state;
    }

    public static boolean isFirstStart(@NonNull File baseDir) {
        Log.d(LOG_TAG, "Checking if AppState File exists");
        Log.d(LOG_TAG, "Files: " + Arrays.stream(baseDir.listFiles()).map(File::getAbsolutePath).collect(Collectors.toList()).toString());

        Optional<File> appFile = Arrays.stream(baseDir.listFiles()).filter(f -> f.getAbsolutePath().endsWith(APP_STATE_FILE)).findFirst();

        if (appFile.isPresent()) {
            File actualFile = appFile.get();

            Log.d(LOG_TAG, "Found file: " + actualFile);

            String permissions = "Permissions: ";
            permissions += actualFile.canRead() ? "r/" : "-/";
            permissions += actualFile.canWrite() ? "w/" : "-/";
            permissions += actualFile.canExecute() ? "x" : "-";

            Log.d(LOG_TAG, permissions);
            return false;
        } else {
            return true;
        }
    }

    public boolean getEnableNSFW() {
        return enableNSFW;
    }

    public void setEnableNSFW(boolean enable) {
        this.enableNSFW = enable;
    }

    public boolean getEnableAutoUpdates() {
        return enableAutoUpdates;
    }

    public void setEnableAutoUpdates(boolean enable) {
        this.enableAutoUpdates = enable;
    }

    public boolean isOnlyNSFW() {
        return onlyNSFW;
    }

    public void setOnlyNSFW(boolean onlyNSFW) {
        this.onlyNSFW = onlyNSFW;
    }

    public Set<Integer> getDisabledPacks() {
        return disabledPacks;
    }

    public void setDisabledPacks(Set<Integer> disabledPacks) {
        this.disabledPacks.clear();
        if (disabledPacks != null) {
            this.disabledPacks.addAll(disabledPacks);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public List<ContentPack> getPacks() {
        return packs;
    }

    public void setPacks(List<ContentPack> packs) {
        this.packs.clear();
        if (packs != null) {
            this.packs.addAll(packs);
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions.clear();
        if (questions != null) {
            this.questions.addAll(questions);
        }
    }

    public boolean saveState(File baseDir) {
        try (ObjectOutputStream stateOut = new ObjectOutputStream(new FileOutputStream(new File(baseDir, APP_STATE_FILE)))) {
            stateOut.writeObject(this);
            Log.d(LOG_TAG, "Successfully stored AppState to: " + baseDir.getAbsolutePath());
            Log.d(LOG_TAG, "Other files: " + Arrays.stream(baseDir.listFiles()).map(File::getAbsolutePath).collect(Collectors.toList()).toString());
            return true;
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error when writing AppState!", e);
            return false;
        }
    }
}
