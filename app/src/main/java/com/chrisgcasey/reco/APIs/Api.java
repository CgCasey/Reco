package com.chrisgcasey.reco.APIs;

import com.chrisgcasey.reco.model.ActiveListings;



import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.Callback;

/**
 * Created by chris on 1/2/2016.
 */
public interface Api {
    @GET("/listings/active")
    void activeListings(@Query("includes") String includes, Callback<ActiveListings> callback);
}
