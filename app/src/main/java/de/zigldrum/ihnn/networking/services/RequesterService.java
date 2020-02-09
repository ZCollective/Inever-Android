package de.zigldrum.ihnn.networking.services;

import androidx.annotation.NonNull;

import de.zigldrum.ihnn.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequesterService {

    private static ContentService contentService;

    public static void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        contentService = retrofit.create(ContentService.class);
    }

    @NonNull
    public static ContentService getInstance() {
        if (contentService == null) {
            init();
        }

        return contentService;
    }
}
