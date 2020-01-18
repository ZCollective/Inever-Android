package de.zigldrum.ihnn.services;

import de.zigldrum.ihnn.utils.AppState;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequesterService {

    private static ContentService contentService;

    public static ContentService buildContentService() {
        if (contentService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppState.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(builder.build())
                    .build();
            contentService = retrofit.create(ContentService.class);
        }
        return contentService;
    }
}
