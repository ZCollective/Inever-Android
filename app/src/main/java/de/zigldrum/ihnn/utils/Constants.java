package de.zigldrum.ihnn.utils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {

    @StringDef({
            ContentPackKeys.DESCRIPTION,
            ContentPackKeys.KEY_WORDS,
            ContentPackKeys.VERSION,
            ContentPackKeys.MIN_AGE,
            ContentPackKeys.NAME,
            ContentPackKeys.ID
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentPackKeys {
        String DESCRIPTION = "content_pack_description";
        String KEY_WORDS = "content_pack_keywords";
        String VERSION = "content_pack_version";
        String MIN_AGE = "content_pack_min_age";
        String NAME = "content_pack_name";
        String ID = "content_pack_id";
    }

    @StringDef({
            QuestionKeys.QUESTION_STRING,
            QuestionKeys.PACK_ID_FK,
            QuestionKeys.ID
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface QuestionKeys {
        String QUESTION_STRING = "question_string";
        String PACK_ID_FK = "content_pack_id_fk";
        String ID = "question_id";
    }

    @IntDef({AgeRestrictions.NSFW_BORDER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AgeRestrictions {
        int NSFW_BORDER = 18;
    }

    @IntDef({RequestCodes.SETTINGS_REQUEST_CODE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestCodes {
        int SETTINGS_REQUEST_CODE = 0;
        int GAME_REQUEST_CODE = 1;
    }

    @IntDef({SettingsResults.DEFAULT, SettingsResults.UPDATE_NOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SettingsResults {
        int DEFAULT = 0;
        int UPDATE_NOW = 1;
    }

    @IntDef({GameResults.GAME_DEFAULT, GameResults.GAME_QUESTIONS_EMPTY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GameResults {
        int GAME_DEFAULT = 0;
        int GAME_QUESTIONS_EMPTY = 1;
    }
}
