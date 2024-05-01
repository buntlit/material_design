package com.buntlit.pictureoftheday.ui.picture

import com.buntlit.pictureoftheday.ui.rover.RoversAPI
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitImpl {
    private val baseUrl = "https://api.nasa.gov/"

    fun getPODRetrofitImpl(): PictureOfTheDayAPI {
        val podRetrofitImpl = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(CustomInterceptor()))
            .build()

        return podRetrofitImpl.create(PictureOfTheDayAPI::class.java)
    }

    fun getRoversRetrofitImpl(): RoversAPI{
        val roversRetrofitImpl = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(CustomInterceptor()))
            .build()

        return roversRetrofitImpl.create(RoversAPI::class.java)
    }

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        return httpClient.build()
    }


    inner class CustomInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(chain.request())
        }
    }

}