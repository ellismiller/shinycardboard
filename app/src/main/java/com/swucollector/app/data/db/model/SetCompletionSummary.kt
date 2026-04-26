package com.swucollector.app.data.db.model

data class SetCompletionSummary(
    val setCode: String,
    val totalPrintings: Int,
    val ownedPrintings: Int
)
