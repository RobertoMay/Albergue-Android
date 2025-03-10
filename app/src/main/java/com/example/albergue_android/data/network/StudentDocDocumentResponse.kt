package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.StudentDocDocument

data class StudentDocDocumentResponse(
    val error: Boolean,
    val msg: String,
    val data: List<StudentDocDocument>?
)