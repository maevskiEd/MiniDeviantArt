package ed.maevski.remote_module.entity

import com.google.gson.annotations.SerializedName

data class Giver(
    @SerializedName("type") val type: String,
    @SerializedName("usericon") val usericon: String,
    @SerializedName("userid")val userid: String,
    @SerializedName("username") val username: String
)