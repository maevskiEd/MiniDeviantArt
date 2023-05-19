package ed.maevski.minideviantart.data.entity

import com.google.gson.annotations.SerializedName

data class Author(
    @SerializedName("type") val type: String,
    @SerializedName("usericon") val usericon: String,
    @SerializedName("userid") val userid: String,
    @SerializedName("username") val username: String
)