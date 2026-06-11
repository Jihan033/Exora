package com.example.exora.auth;

import com.example.exora.model.ClubModel;
import com.example.exora.model.EventModel;
import com.example.exora.model.RecruitmentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @GET("events")
    Call<List<EventModel>> getEvents();

    @GET("my-clubs")
    Call<List<ClubModel>> getMyClubs(@Header("Authorization") String token);

    @GET("recruitments")
    Call<List<RecruitmentModel>> getRecruitments();

    @POST("apply-club")
    Call<Void> applyToClub(@Header("Authorization") String token, @Body ClubApplyRequest request);

    @POST("join-event")
    Call<Void> joinEvent(@Header("Authorization") String token, @Body EventJoinRequest request);
}