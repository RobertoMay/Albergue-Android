package com.example.albergue_android.data.network


import com.example.albergue_android.domain.models.Comment
import com.example.albergue_android.domain.models.StudentDocDocument
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface StudentDocService {

    @GET("studentdoc/student/{id}")
    fun getById(@Path("id") id: String): Call<List<StudentDocDocument>>

    @Multipart
    @POST("studentdoc/add-document")
    fun uploadFile(
        @Part("aspiranteId") aspiranteId: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("documentType") documentType: RequestBody,
        @Part("documentName") documentName: RequestBody
    ): Call<Void>

    @Multipart
    @POST("studentdoc/edit-document")
    fun editFile(
        @Part("aspiranteId") aspiranteId: RequestBody,
        @Part document: MultipartBody.Part,
        @Part("documentType") documentType: RequestBody,
        @Part("documentName") documentName: RequestBody
    ): Call<Void>

    @FormUrlEncoded
    @POST("studentdoc/delete-document")
    fun deleteFile(
        @Field("aspiranteId") aspiranteId: String,
        @Field("documentType") documentType: String
    ): Call<Void>

    @GET("studentdoc/comments/{aspiranteId}")
    fun getComments(@Path("aspiranteId") aspiranteId: String): Call<List<Comment>>

    @POST("studentdoc/")
    fun createStudentDoc(@Body studentDoc: StudentDocDocument): Call<StudentDocDocument>

    @POST("studentdoc/add-comment")
    @FormUrlEncoded
    fun addComment(
        @Field("aspiranteId") aspiranteId: String,
        @Field("text") text: String,
        @Field("createdBy") createdBy: String
    ): Call<response>

    @HTTP(method = "DELETE", path = "studentdoc/delete-comment/{commentId}", hasBody = true)
    @FormUrlEncoded
    fun deleteComment(
        @Path("commentId") commentId: String,
        @Field("aspiranteId") aspiranteId: String
    ): Call<response>

    @FormUrlEncoded
    @PUT("studentdoc/update-status/{aspiranteId}")
    fun updateDocumentStatus(
        @Path("aspiranteId") aspiranteId: String,
        @Field("link") link: String,
        @Field("status") status: String
    ): Call<response>

    @PUT("studentdoc/update-all-status/{aspiranteId}")
    fun updateAllDocumentsStatus(
        @Path("aspiranteId") aspiranteId: String,
        @Body body: Map<String, String>
    ): Call<response>

    data class response(val message: String);
}