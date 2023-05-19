package ed.maevski.minideviantart.data.entity

import com.google.gson.annotations.SerializedName

data class Preview(
    @SerializedName("height") val height: Int,
    @SerializedName("src") val src: String,
    @SerializedName("transparency") val transparency: Boolean,
    @SerializedName("width") val width: Int
)