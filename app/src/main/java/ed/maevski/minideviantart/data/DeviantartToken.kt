package ed.maevski.minideviantart.data

import ed.maevski.remote_module.entity.DeviantartResponse
import ed.maevski.remote_module.entity_token.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeviantartToken {
    @GET("token")
    fun getToken(
        @Query("grant_type") grantType: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,

        ): Call<ed.maevski.remote_module.entity_token.TokenResponse>
}