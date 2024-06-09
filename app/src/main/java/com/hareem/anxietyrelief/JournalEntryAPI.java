package com.hareem.anxietyrelief;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JournalEntryAPI {

    @POST("/saveJournalEntry")
    Call<Void> saveJournalEntry(@Body JournalEntryModel journalEntry);

    @GET("/getJournalEntries/{patientId}")
    Call<List<JournalEntryModel>> getJournalEntries(@Path("patientId") String patientId);

    @DELETE("/deleteJournalEntry/{entryId}")
    Call<Void> deleteJournalEntry(@Path("entryId") String entryId);

    @PUT("/updateJournalEntry/{entryId}")
    Call<Void> updateJournalEntry(@Path("entryId") String entryId, @Body JournalEntryModel updatedEntry);

}
