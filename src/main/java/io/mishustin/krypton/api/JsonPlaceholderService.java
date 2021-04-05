package io.mishustin.krypton.api;

import feign.Param;
import feign.RequestLine;
import io.mishustin.krypton.model.Post;
import io.mishustin.krypton.model.User;

import java.util.List;

public interface JsonPlaceholderService {

    @RequestLine("GET /posts")
    ApiResponse<List<Post>> getPosts();

    @RequestLine("GET /posts/{postId}")
    ApiResponse<Post> getPost(@Param("postId") int postId);

    @RequestLine("DELETE /posts/{postId}")
    ApiResponse<Object> deletePost(@Param("postId") int postId);

    @RequestLine("GET /users")
    ApiResponse<User> getUser();
}
