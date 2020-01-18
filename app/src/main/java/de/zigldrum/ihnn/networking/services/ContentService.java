package de.zigldrum.ihnn.networking.services;

import de.zigldrum.ihnn.networking.objects.ContentPackResponse;
import de.zigldrum.ihnn.networking.objects.ProposalRequestBody;
import de.zigldrum.ihnn.networking.objects.ProposalResponse;
import de.zigldrum.ihnn.networking.objects.QuestionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ContentService {

    @Headers("Origin: ihnn-app")
    @GET("v1/public/content/packs")
    Call<ContentPackResponse> getPacks();

    @Headers("Origin: ihnn-app")
    @GET("v1/public/content/packs/{packid}/questions")
    Call<QuestionResponse> getQuestions(@Path("packid") int packID);

    @Headers({"Origin: ihnn-app", "Content-Type: application/json"})
    @POST("v1/public/content/questionProposal")
    Call<ProposalResponse> proposeQuestion(@Body ProposalRequestBody body);
}
