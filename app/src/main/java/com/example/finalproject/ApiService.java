package com.example.finalproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;

public interface ApiService {
    // Get all posts
    @GET("posts")
    Call<List<Post>> getAllPosts();

    // Get a specific post
    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") int id);

    // Create a new post
    @POST("posts")
    Call<Post> createPost(@Body Post post);

    // Update a post
    @PUT("posts/{id}")
    Call<Post> updatePost(@Path("id") int id, @Body Post post);

    // Delete a post
    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);

    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
        @Query("country") String country,
        @Query("apiKey") String apiKey
    );
}
