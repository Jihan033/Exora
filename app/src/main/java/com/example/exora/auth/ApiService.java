package com.example.exora.auth;

import com.example.exora.model.AboutUsModel;
import com.example.exora.model.ClubModel;
import com.example.exora.model.EventModel;
import com.example.exora.model.RecruitmentModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    // --- CLUBS ---
    @GET("api/clubs")
    Call<List<ClubModel>> getClubs();

    @POST("api/clubs")
    Call<ClubModel> createClub(@Header("Authorization") String token, @Body ClubModel club);

    @PUT("api/clubs/{id}")
    Call<ClubModel> updateClub(@Header("Authorization") String token, @Path("id") int id, @Body ClubModel club);

    @DELETE("api/clubs/{id}")
    Call<Void> deleteClub(@Header("Authorization") String token, @Path("id") int id);

    // --- APPLICATIONS ---
    @POST("api/apply-club")
    Call<Void> applyToClub(@Header("Authorization") String token, @Body Map<String, Object> request);

    @GET("api/applications")
    Call<List<Map<String, Object>>> getApplications(@Header("Authorization") String token);

    @PUT("api/applications/{id}/status")
    Call<Void> updateApplicationStatus(@Header("Authorization") String token, @Path("id") int id, @Body Map<String, String> statusBody);

    // --- EVENTS ---
    @GET("api/events")
    Call<List<EventModel>> getEvents();

    @POST("api/events")
    Call<EventModel> createEvent(@Header("Authorization") String token, @Body EventModel event);

    @PUT("api/events/{id}")
    Call<EventModel> updateEvent(@Header("Authorization") String token, @Path("id") int id, @Body EventModel event);

    @DELETE("api/events/{id}")
    Call<Void> deleteEvent(@Header("Authorization") String token, @Path("id") int id);

    @POST("api/events/join")
    Call<Void> joinEvent(@Header("Authorization") String token, @Body EventJoinRequest request);

    @GET("api/events/user/{email}")
    Call<List<EventModel>> getJoinedEvents(@Path("email") String email);

    @GET("api/events/{id}/participants")
    Call<List<Map<String, String>>> getEventParticipants(@Header("Authorization") String token, @Path("id") int eventId);

    // --- OTHERS ---
    @GET("api/recruitments")
    Call<List<RecruitmentModel>> getRecruitments();

    @GET("api/about-us")
    Call<AboutUsModel> getAboutUs();

    @PUT("api/about-us")
    Call<AboutUsModel> updateAboutUs(@Header("Authorization") String token, @Body AboutUsModel aboutUs);
}
