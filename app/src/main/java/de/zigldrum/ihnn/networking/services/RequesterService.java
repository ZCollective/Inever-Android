package de.zigldrum.ihnn.networking.services;

import de.zigldrum.ihnn.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequesterService {

    private static ContentService contentService;

    public static ContentService getContentService() {
        if (contentService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(builder.build())
                    .build();
            contentService = retrofit.create(ContentService.class);
        }

        return contentService;
    }
}
