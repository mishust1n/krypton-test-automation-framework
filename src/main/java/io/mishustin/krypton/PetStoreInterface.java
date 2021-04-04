package io.mishustin.krypton;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PetStoreInterface {


    @GET("/pet/{petId}")
    Call<Object> getPet(@Path("petId") int id);


}
