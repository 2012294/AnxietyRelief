package com.hareem.anxietyrelief;

import com.google.gson.JsonObject;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientAPI {
    @POST("/saveTransactions")
    Call<Void> saveTransaction(@Body RequestBody requestBody);
    @GET("/getAppointmentsOfChat")
    Call<List<Appointment>> getAppointmentsOfChat();

    @GET("/getAppointmentsOfVideo")
    Call<List<Appointment>> getAppointmentsOfVideo();
    @POST("/saveAppointment")
    Call<Void> saveAppointments(@Body RequestBody requestBody);

    @GET("/gettherapistAppointments/{therapistId}")
    Call<List<Appointment>> getTherapistAppointments(@Path("therapistId") String therapistId);
    @GET("/checkUsernamePatientAvailability/{username}")
    Call<UsernameAvailabilityResponse> checkUsernameAvailability(@Path("username") String username);

    @POST("/registerPatient")
    Call<RegistrationResponse> registerPatient(@Body Patient patient);


    @FormUrlEncoded
    @POST("/loginPatients")
    Call<LoginResponse> loginPatient(@Field("email") String email, @Field("password") String password);

    @GET("/getPatient/{userId}")
    Call<Patient> getPatient(@Path("userId") ObjectId userId);


    @POST("/saveFavoriteStatus")
    Call<Void> saveFavoriteStatus(@Body Affirmations requestModel);

    @POST("/deleteFavoriteStatus")
    Call<Void> deleteFavoriteStatus(@Body Affirmations affirmations);

    @GET("/getFavoriteAffirmations")
    Call<List<Affirmations>> getFavoriteAffirmations(@Query("patientId") String patientId);
    @GET("/getFavoriteStatus")
    Call<JsonObject> getFavoriteStatus(@Query("patientId") String patientId, @Query("affirmationId") String affirmationId);


    @POST("/saveAffirmation")
    Call<JsonObject> saveAffirmation(@Body RequestBody requestBody);

    @POST("/deleteAffirmation")
    Call<JsonObject> deleteAffirmation(@Body RequestBody requestBody);
    @GET("/getOwnAffirmations/{patientId}")
    Call<List<Affirmations>> getOwnAffirmations(@Path("patientId") String patientId);



    @POST("/updateAffirmation")
    Call<JsonObject> updateAffirmation(@Body RequestBody requestBody);



    @POST("/saveTheme")
    Call<Void> saveTheme(@Body JsonObject themeObject);


    @GET("/getAffirmationDetailsByPatient/{patientId}")
    Call<List<Affirmations>> getAffirmationDetailsByPatient(@Path("patientId") String patientId);
    @GET("/getTheme/{patientId}")
    Call<ThemeResponse> getTheme(@Path("patientId") String patientId);
    @FormUrlEncoded
    @POST("/saveAnxietyLevel")
    Call<Void> saveAnxietyLevel(
            @Field("patientId") String patientId,
            @Field("anxietyLevel") String anxietyLevel,
            @Field("DateTime") String DateTime

    );

    @GET("/getAnxietyLevels/{patientId}")
    Call<List<AnxietyLevel>> getAnxietyLevels(@Path("patientId") String patientId);


    @POST("/deleteAnxietyLevel")
    Call<JsonObject> deleteAnxietyLevel(@Body RequestBody requestBody);

    @PUT("/updatePatientPassword")
    Call<RegistrationResponse> updatePasswordPatient(@Body Patient patient);


    @GET("/therapists1")
    Call<List<Therapist>> getTherapists();

    @GET("/getTherapist/{userId}")
    Call<Therapist> getTherapist(@Path("userId") ObjectId userId);



    @POST("/saveCard/{patientId}")
    Call<Void> saveCard(@Path("patientId") String patientId, @Body List<CardData> cardDataList);

    @GET("/getCards/{patientId}")
    Call<List<CardData>> getCards(@Path("patientId") String patientId);

}
