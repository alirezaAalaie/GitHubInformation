package org.example.model.github.client
import okhttp3.OkHttpClient
import org.example.model.github.GitHubApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GitHubClient {
    private const val BASE_URL = "https://api.github.com/"
    private const val API_TOKEN = "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"

    val apiService: GitHubApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "token $API_TOKEN")
                            .addHeader("Accept", "application/vnd.github+json")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(GitHubApiService::class.java)
    }
}