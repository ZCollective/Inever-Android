package de.zigldrum.ihnn.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import de.zigldrum.ihnn.R;
import de.zigldrum.ihnn.objects.AppState;
import de.zigldrum.ihnn.objects.ContentPack;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequesterService {

    private static ContentService contentService;

    public static ContentService buildContentService(){
        if(contentService == null) {
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
