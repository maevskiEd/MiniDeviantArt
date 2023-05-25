package ed.maevski.remote_module

import ed.maevski.remote_module.entity.DeviantartResponse
import ed.maevski.remote_module.entity_token.TokenPlaceboResponse
import ed.maevski.remote_module.entity_token.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeviantartApi {

    @GET("api/v1/oauth2/browse/{category}")
    fun getPictures(
        @Path("category") category: String,
        @Query("access_token") tokenKey: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,

        ): Call<ed.maevski.remote_module.entity.DeviantartResponse>

    @GET("oauth2/token")
    fun getToken(
        @Query("grant_type") grantType: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        ): Call<ed.maevski.remote_module.entity_token.TokenResponse>

    @GET("/api/v1/oauth2/placebo")
    fun checkToken(
        @Query("access_token") tokenKey: String,
        ): Call<ed.maevski.remote_module.entity_token.TokenPlaceboResponse>

    object ApiConst {
        const val BASE_URL = "https://www.deviantart.com/"
    }
}


