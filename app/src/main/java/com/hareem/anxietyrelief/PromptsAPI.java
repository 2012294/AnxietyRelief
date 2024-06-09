
package com.hareem.anxietyrelief;

        import java.util.List;

        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.http.Path;

public interface PromptsAPI {
    @GET("/getAvailablePrompts/{patientId}")
    Call<List<JournalingPrompt>> getAvailablePrompts(@Path("patientId") String patientId);
}
