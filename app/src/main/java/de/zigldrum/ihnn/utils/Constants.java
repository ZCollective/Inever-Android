package de.zigldrum.ihnn.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {

    @IntDef({AgeRestrictions.NSFW_BORDER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AgeRestrictions {
        int NSFW_BORDER = 18;
    }

    @IntDef({ContentPacksResults.DEFAULT, ContentPacksResults.UPDATED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentPacksResults {
        int DEFAULT = 0;
        int UPDATED = 1;
    }

    @IntDef({RequestCodes.SETTINGS_REQUEST_CODE, RequestCodes.CONTENTPACKS_REQUEST_CODE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestCodes {
        int SETTINGS_REQUEST_CODE = 0;
        int CONTENTPACKS_REQUEST_CODE = 1;
    }

    @IntDef({SettingsResults.DEFAULT,
            SettingsResults.STATE_CHANGED,
            SettingsResults.UPDATENOW,
            SettingsResults.STATE_AND_UPDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SettingsResults {
        int DEFAULT = 0;
        int STATE_CHANGED = 1;
        int UPDATENOW = 2;
        int STATE_AND_UPDATE = 3;
    }
}
