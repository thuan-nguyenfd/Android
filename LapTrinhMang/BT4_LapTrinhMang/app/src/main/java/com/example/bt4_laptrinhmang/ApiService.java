package com.example.bt4_laptrinhmang;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface ApiService {
    // Lấy danh sách tất cả bài post
    @GET("posts")
    Call<List<Post>> getPosts();
}
