package kr.ac.hansung.demap.data.remote.source.network

import androidx.databinding.library.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NaverApiClient {

    private const val NAVER_BASE_URL = "https://openapi.naver.com/v1/"

    private val okHttpClient =  OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    })
        .build()

    val retrofit = Retrofit.Builder()
    .baseUrl(NAVER_BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()
    .create(NaverApiService::class.java)
}