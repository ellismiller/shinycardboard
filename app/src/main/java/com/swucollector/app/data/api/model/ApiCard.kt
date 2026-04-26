package com.swucollector.app.data.api.model

import com.google.gson.annotations.SerializedName

data class ApiCard(
    @SerializedName("Set") val set: String,
    @SerializedName("Number") val number: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Subtitle") val subtitle: String?,
    @SerializedName("Type") val type: String,
    @SerializedName("Aspects") val aspects: List<String>?,
    @SerializedName("Traits") val traits: List<String>?,
    @SerializedName("Arenas") val arenas: List<String>?,
    @SerializedName("Cost") val cost: Int?,
    @SerializedName("Power") val power: Int?,
    @SerializedName("HP") val hp: Int?,
    @SerializedName("FrontText") val frontText: String?,
    @SerializedName("BackText") val backText: String?,
    @SerializedName("EpicAction") val epicAction: String?,
    @SerializedName("Rarity") val rarity: String,
    @SerializedName("Unique") val unique: Boolean?,
    @SerializedName("Artist") val artist: String?,
    @SerializedName("VariantType") val variantType: String?,
    @SerializedName("DoubleSided") val doubleSided: Boolean?
)
