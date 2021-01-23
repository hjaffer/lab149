package com.example.lab149application.framework.network

import com.example.lab149application.framework.network.model.snap.SnapMatch
import com.example.lab149application.framework.network.model.snap.SnapObject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SnapRetrofit {
    // @Headers("Authorization: Bearer <here>")
    @GET("/iositems/items")
    suspend fun getAllSnaps(): List<SnapObject>

    @FormUrlEncoded
    @POST("/iositems/items")
    suspend fun postSnap(
        @Field("ImageLabel") ImageLabel: String,
        @Field("Image") Image: String
    ): SnapMatch
}
