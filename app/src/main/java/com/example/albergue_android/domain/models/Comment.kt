package com.example.albergue_android.domain.models

import java.util.Date

data class Comment(
    val id: String,
    val comment: String,
    val createdAt: Date,
    val createdBy: String
)