package com.example.humblr.data

import androidx.core.net.toUri
import com.example.humblr.data.model.Comment
import com.example.humblr.data.model.Listing
import com.example.humblr.data.model.Response
import com.example.humblr.data.model.Subreddit
import com.example.humblr.data.model.Thing
import com.example.humblr.util.RedirectUri
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("r/popular?raw_json=1")
    suspend fun getPopularSubreddits(
        @Query("after") after: String?
    ): Response<Listing<Response<Subreddit>>>

    @GET("r/popular/new?raw_json=1")
    suspend fun getNewSubreddits(
        @Query("after") after: String?
    ): Response<Listing<Response<Subreddit>>>

    @GET("search?raw_json=1")
    suspend fun searchSubreddits(
        @Query("after") after: String?,
        @Query("q") query: String?
    ): Response<Listing<Response<Subreddit>>>

    @POST("api/subscribe")
    suspend fun subscribe(
        @Query("action") action: String,
        @Query("sr_name") subredditName: String
    )

    @GET("comments/{id}")
    suspend fun getComments(
        @Path("id") id: String
    ): List<Response<Listing<Response<Thing>>>>

    companion object {
        private const val BaseUrl = "https://oauth.reddit.com/"
        private const val ClientId = "ivpy98FD40P7MW7_uwZO2A"
        val OAuthUri = (
            "https://www.reddit.com/api/v1/authorize.compact" +
                "?client_id=$ClientId" +
                "&response_type=token" +
                "&state=$ClientId" +
                "&redirect_uri=$RedirectUri" +
                "&scope=identity read subscribe mysubreddits"
            ).toUri()

        fun create(credentialsRepository: CredentialsRepository) = with(Retrofit.Builder()) {
            baseUrl(BaseUrl)
            client(
                OkHttpClient.Builder().run {
                    addInterceptor {
                        it.proceed(
                            with(it.request().newBuilder()) {
                                val token = credentialsRepository.token
                                if (token != null) {
                                    addHeader("Authorization", "Bearer $token")
                                }
                                build()
                            }
                        )
                    }
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                    build()
                }
            )
            addConverterFactory(
                GsonConverterFactory.create(
                    with(GsonBuilder()) {
                        setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        registerTypeAdapter(
                            Thing::class.java,
                            JsonDeserializer { json, _, _ ->
                                val obj = json.asJsonObject
                                val kind = obj.get("name").asString
                                if (kind.startsWith("t1")) {
                                    return@JsonDeserializer Comment(
                                        author = obj.get("author").asString,
                                        body = obj.get("body").asString,
                                        createdUtc = obj.get("created_utc").asDouble,
                                        score = obj.get("score").asInt
                                    )
                                } else if (kind.startsWith("t3")) {
                                    return@JsonDeserializer Subreddit(
                                        name = obj.get("name").asString,
                                        id = obj.get("id").asString,
                                        title = obj.get("title").asString,
                                        noFollow = obj.get("no_follow").asBoolean,
                                        selftext = obj.get("selftext").asString,
                                        url = obj.get("url").asString,
                                        author = obj.get("author").asString,
                                        numComments = obj.get("num_comments").asInt,
                                        subreddit = obj.get("subreddit").asString
                                    )
                                }
                                return@JsonDeserializer null
                            }
                        )
                        create()
                    }
                )
            )
            build()
        }
            .create<Api>()
    }
}
