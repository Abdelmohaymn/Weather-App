package com.example.weather;

import com.example.weather.model.Response;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface WeatherApi {

    @GET("forecast.json")
    Call<Response> getData(@QueryMap Map<String,String> parameters);
}
