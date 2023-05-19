package ed.maevski.minideviantart.data.entity

import com.google.gson.annotations.SerializedName

data class DeviantartResponse(
    @SerializedName("has_more") val has_more: Boolean,
    @SerializedName("results") val results: List<Results>
)