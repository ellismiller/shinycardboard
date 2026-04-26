package com.swucollector.app.data.api.model

import com.google.gson.annotations.SerializedName

data class ApiCardsResponse(
    @SerializedName("total_cards") val totalCards: Int,
    @SerializedName("data") val data: List<ApiCard>
)
