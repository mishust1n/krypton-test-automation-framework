package io.mishustin.krypton.api;

import io.mishustin.krypton.model.Post;
import io.mishustin.krypton.model.User;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface JsonPlaceholderService {

    @GET("/posts")
    Call<List<Post>> getPosts();

    @GET("/posts/{postId}")
    Call<Post> getPost(@Path("postId") int postId);

    @DELETE("/posts/{postId}")
    Call<Object> deletePost(@Path("postId") int postId);

    @GET("/users")
    Call<User> getUser();
}
