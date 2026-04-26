package com.swucollector.app.data.api

import com.swucollector.app.data.api.model.ApiCard
import retrofit2.http.GET
import retrofit2.http.Path

interface SwuApiService {
    @GET("cards/{set}")
    suspend fun getCardsBySet(@Path("set") setCode: String): List<ApiCard>
}
