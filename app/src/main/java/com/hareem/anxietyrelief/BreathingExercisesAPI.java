package com.hareem.anxietyrelief;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.GET;
import retrofit2.http.POST;

import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface BreathingExercisesAPI {
    @POST("/insertBreathingExercises")
    Call<Void> insertBreathingExercises(@Body BreathingExercisesModel newExercise);

    @GET("/getBreathingExercises/{patientId}")
    Call<List<BreathingExercisesModel>> getBreathingExercises(@Path("patientId") String patientId);

    @PUT("/updateMusic/{patientId}")
    Call<Void> updateMusicValue(@Path("patientId") String patientId, @Body Map<String, Integer> music);


    @PUT("/updateEqualBreathing/{patientId}")
    Call<Void> updateEqualBreathingValue(@Path("patientId") String patientId, @Body Map<String, Integer> equalBreathingValue);

    @PUT("/updateBoxBreathing/{patientId}")
    Call<Void> updateBoxBreathingValue(@Path("patientId") String patientId, @Body Map<String, Integer> boxBreathingValue);

    @PUT("/update478Breathing/{patientId}")
    Call<Void> update478BreathingValue(@Path("patientId") String patientId, @Body Map<String, Integer> FourseveneightBreathingValue);

    @PUT("/updateTriangleBreathing/{patientId}")
    Call<Void> updatetriangleBreathingValue(@Path("patientId") String patientId, @Body Map<String, Integer> TriangleBreathingValue);
}




