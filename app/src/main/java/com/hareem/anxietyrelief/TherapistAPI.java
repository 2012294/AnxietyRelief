package com.hareem.anxietyrelief;

import com.google.gson.JsonObject;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TherapistAPI {
    @GET("/checkUsernameTherapistAvailability/{username}")
    Call<UsernameAvailabilityResponse> checkUsernameAvailability(@Path("username") String username);
    @DELETE("/appointments/{id}")
    Call<Void> deleteAppointment(@Path("id") String appointmentId);
    @POST("/registerTherapist")
    Call<RegistrationResponse> registerTherapist(@Body Therapist therapist);
//
    @FormUrlEncoded
    @POST("/loginTherapists")
    Call<LoginResponse> loginTherapists(@Field("email") String email, @Field("password") String password);
//
    @GET("/getTherapist/{userId}")
    Call<Therapist> getTherapist(@Path("userId") ObjectId userId);

    @PUT("/updateTherapistPassword")
    Call<RegistrationResponse> updatePasswordTherapist(@Body Therapist therapist);

    @Multipart
    @POST("upload/{therapistId}")
    Call<ResponseBody> uploadMultiplePdfs(
            @Path("therapistId") String therapistId,
            @Part List<MultipartBody.Part> pdfParts
    );
    @POST("/saveAccount/{therapistId}")
    Call<Void> saveCard(@Path("therapistId") String therapistId, @Body List<AccountData> accountList);
    @POST("/profiledata/{therapistId}")
    Call<ResponseBody> updateTherapistData(@Path("therapistId") String therapistId, @Body Map<String, Object> requestBody);


    @GET("/gettherapistsdata/{therapistId}")
    Call<Therapistprofiledata> getTherapistData(@Path("therapistId") String therapistId);

    @POST("/saveProfilePic")
    Call<ResponseBody> saveProfilePic(@Body Map<String, Object> requestBody);

    @POST("/deleteProfilePic")
    Call<ResponseBody> deleteProfilePic(@Body Map<String, Object> requestBody);

    @DELETE("deletePdfFiles/{therapistId}")
    Call<ResponseBody> deletePdfFiles(@Path("therapistId") String therapistId);


    @PUT("/completeTransaction")
    Call<ResponseBody> completeTransaction(@Body Map<String, String> requestBody);




}
