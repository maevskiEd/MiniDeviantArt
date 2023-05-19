package ed.maevski.minideviantart.data.entity

import com.google.gson.annotations.SerializedName

data class DailyDeviation(
    @SerializedName("body") val body: String,
    @SerializedName("giver") val giver: Giver,
    @SerializedName("suggester") val suggester: Suggester,
    @SerializedName("time") val time: String
)