package com.example.albergue_android.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.albergue_android.data.network.*

object ApiClient {
    private const val BASE_URL = "https://albergue-57e14.uc.r.appspot.com/api/"

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

    // Propiedad para acceder a
    val dataStudentService: DataStudentService by lazy {
        retrofit.create(DataStudentService::class.java)
    }

    val studentEnrollmentService: StudentEnrollmentService by lazy {
        retrofit.create(StudentEnrollmentService::class.java)
    }

    val studentDocService: StudentDocService by lazy {
        retrofit.create(StudentDocService::class.java)
    }

    val callService: CallService by lazy {
        retrofit.create(CallService::class.java)
    }

    val studentService: StudentService by lazy {
        retrofit.create(StudentService::class.java)
    }



    val authenticationService: AuthenticationService by lazy {
        retrofit.create(AuthenticationService::class.java)
    }

    val adminService: AdminService by lazy {
        retrofit.create(AdminService::class.java)
    }



    // Método para obtener el token desde SharedPreferences
    private fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", null)
    }

    // Interceptor para agregar el token a cada solicitud
    private fun authInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val token = getToken(context)
            val request: Request = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token") // Token en el header
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
    }

    // Cliente OkHttp con el interceptor
    private fun okHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor(context))
            .build()
    }

    // Retrofit con el cliente OkHttp
    fun getRetrofitInstance(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient(context)) // Cliente con el token
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <S> createService(context: Context, serviceClass: Class<S>): S {
        return getRetrofitInstance(context).create(serviceClass)
    }


}