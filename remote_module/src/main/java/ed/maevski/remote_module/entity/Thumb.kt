package ed.maevski.remote_module.entity

import com.google.gson.annotations.SerializedName

data class Thumb(
    @SerializedName("height") val height: Int,
    @SerializedName("src") val src: String,
    @SerializedName("transparency") val transparency: Boolean,
    @SerializedName("width") val width: Int
)