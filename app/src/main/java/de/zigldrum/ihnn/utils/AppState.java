package de.zigldrum.ihnn.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.networking.objects.ContentPack;
import de.zigldrum.ihnn.networking.objects.Question;
import de.zigldrum.ihnn.utils.Constants.ContentPackKeys;
import de.zigldrum.ihnn.utils.Constants.QuestionKeys;
import io.paperdb.Paper;

public class AppState {

    private static final String LOG_TAG = "AppState";

    private static AppState instance = null;

    private final Set<ContentPack> packs;
    private final Set<Question> questions;
    private final Set<Integer> disabledPacks;

    private boolean onlyNSFW;
    private boolean enableNSFW;
    private boolean initialized;
    private boolean enableAutoUpdates;
    private boolean initializationError;

    private AppState(@NonNull Context ctx) throws NullPointerException {
        packs = Paper.book().read("packs", new HashSet<>());
        onlyNSFW = Paper.book().read("onlyNSFW", false);
        questions = Paper.book().read("questions", new HashSet<>());
        enableNSFW = Paper.book().read("enableNSFW", false);
        initialized = Paper.book().read("initialized", false);
        disabledPacks = Paper.book().read("disabledPacks", new HashSet<>());
        enableAutoUpdates = Paper.book().read("enableAutoUpdates", true);
        initializationError = Paper.book().read("initializationError", false);

        if (!initialized) {
            Objects.requireNonNull(ctx);
            Optional<Throwable> error = initializeAtFirstStart(ctx);

            if (error.isPresent()) {
                Paper.book().destroy();
                initializationError = true;
                Log.w(LOG_TAG, "First Start Failed!", error.get());
            } else {
                Log.i(LOG_TAG, "First Start was successful!");
            }
        }
    }

    public static boolean init(@NonNull Context ctx) {
        instance = new AppState(ctx);
        return !instance.initializationError && instance.initialized;
    }

    public static AppState getInstance() {
        return instance;
    }

    @NonNull
    private Optional<Throwable> initializeAtFirstStart(@NonNull Context ctx) {
        try (CSVReaderHeaderAware packIn = new CSVReaderHeaderAware(
                new InputStreamReader(ctx.getResources().openRawResource(R.raw.contentpacks)))) {
            Map<String, String> packMap;

            while ((packMap = packIn.readMap()) != null) {
                String packVersion = packMap.get(ContentPackKeys.CP_VERSION);
                String packMinAge = packMap.get(ContentPackKeys.CP_MIN_AGE);
                String packId = packMap.get(ContentPackKeys.CP_ID);

                if (packId == null || packVersion == null || packMinAge == null) {
                    Log.w(LOG_TAG, "Malformed pack, skipping this one!");
                    continue;
                }

                int version = Integer.parseInt(packVersion);
                int minAge = Integer.parseInt(packMinAge);
                int id = Integer.parseInt(packId);

                String name = packMap.get(ContentPackKeys.CP_NAME);
                String keywords = packMap.get(ContentPackKeys.CP_KEY_WORDS);
                String description = packMap.get(ContentPackKeys.CP_DESCRIPTION);

                ContentPack pack = new ContentPack(id, name, description, keywords, minAge, version);
                packs.add(pack);
            }

            Log.i(LOG_TAG, "Found " + packs.size() + " Packs!");
        } catch (IOException | CsvValidationException ioe) {
            return Optional.of(ioe);
        }

        try (CSVReaderHeaderAware questionIn = new CSVReaderHeaderAware(
                new InputStreamReader(ctx.getResources().openRawResource(R.raw.questions)))) {
            Map<String, String> questionMap;

            while ((questionMap = questionIn.readMap()) != null) {
                String qId = questionMap.get(QuestionKeys.QUESTION_ID);
                String packIdFk = questionMap.get(QuestionKeys.PACK_ID_FK);

                if (qId == null | packIdFk == null) {
                    Log.w(LOG_TAG, "Malformed question, skipping this one");
                    continue;
                }

                int id = Integer.parseInt(qId);
                int packID = Integer.parseInt(packIdFk);
                String string = questionMap.get(QuestionKeys.QUESTION_STRING);

                questions.add(new Question(id, string, packID));
            }

            Log.i(LOG_TAG, "Found " + questions.size() + " Questions!");
        } catch (IOException | CsvValidationException ioe) {
            return Optional.of(ioe);
        }

        initialized = true;
        saveState();
        return Optional.empty();
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

    @NonNull
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

    @NonNull
    public Set<ContentPack> getPacks() {
        return packs;
    }

    public void setPacks(Set<ContentPack> packs) {
        this.packs.clear();

        if (packs != null) {
            this.packs.addAll(packs);
        }
    }

    @NonNull
    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(@Nullable Set<Question> questions) {
        this.questions.clear();

        if (questions != null) {
            this.questions.addAll(questions);
        }
    }

    public boolean saveState() {
        try {
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                Paper.book()
                     .write("packs", packs)
                     .write("questions", questions)
                     .write("disabledPacks", disabledPacks)
                     .write("onlyNSFW", onlyNSFW)
                     .write("enableNSFW", enableNSFW)
                     .write("initialized", initialized)
                     .write("enableAutoUpdates", enableAutoUpdates)
                     .write("initializationError", initializationError);
                Log.d(LOG_TAG, "Done saving!");
            });
        } catch (RejectedExecutionException | NullPointerException e) {
            Log.w(LOG_TAG, "Cannot execute save-state task! See attached stack-trace!", e);
            return false;
        }

        return true;
    }
}
