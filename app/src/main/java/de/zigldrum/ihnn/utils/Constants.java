package de.zigldrum.ihnn.utils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {

    @StringDef({
            ContentPackKeys.CP_DESCRIPTION,
            ContentPackKeys.CP_KEY_WORDS,
            ContentPackKeys.CP_VERSION,
            ContentPackKeys.CP_MIN_AGE,
            ContentPackKeys.CP_NAME,
            ContentPackKeys.CP_ID
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentPackKeys {
        String CP_DESCRIPTION = "content_pack_description";
        String CP_KEY_WORDS = "content_pack_keywords";
        String CP_VERSION = "content_pack_version";
        String CP_MIN_AGE = "content_pack_min_age";
        String CP_NAME = "content_pack_name";
        String CP_ID = "content_pack_id";
    }

    @StringDef({
            QuestionKeys.QUESTION_STRING,
            QuestionKeys.QUESTION_ID,
            QuestionKeys.PACK_ID_FK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface QuestionKeys {
        String QUESTION_STRING = "question_string";
        String QUESTION_ID = "question_id";
        String PACK_ID_FK = "content_pack_id_fk";
    }

    @IntDef({AgeRestrictions.NSFW_BORDER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AgeRestrictions {
        int NSFW_BORDER = 18;
    }

    @IntDef({
            RequestCodes.RC_SETTINGS,
            RequestCodes.RC_GAME
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestCodes {
        int RC_SETTINGS = 0;
        int RC_GAME = 1;
    }

    @IntDef({
            SettingsResults.SETTINGS_DEFAULT,
            SettingsResults.SETTINGS_UPDATE_NOW
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SettingsResults {
        int SETTINGS_DEFAULT = 0;
        int SETTINGS_UPDATE_NOW = 1;
    }

    @IntDef({
            GameResults.GAME_DEFAULT,
            GameResults.GAME_QUESTIONS_EMPTY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface GameResults {
        int GAME_DEFAULT = 0;
        int GAME_QUESTIONS_EMPTY = 1;
    }

    @IntDef({
            UpdateProgress.START,
            UpdateProgress.GOT_VALID_RESPONSE,
            UpdateProgress.EVALUATED_CONTENT_PACKS,
            UpdateProgress.DOWNLOADED_NEW_CONTENT_PACKS,
            UpdateProgress.DONE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface UpdateProgress {
        int START = 5;
        int GOT_VALID_RESPONSE = 15;
        int EVALUATED_CONTENT_PACKS = 40;
        int DOWNLOADED_NEW_CONTENT_PACKS = 90;
        int DONE = 100;
    }
}
