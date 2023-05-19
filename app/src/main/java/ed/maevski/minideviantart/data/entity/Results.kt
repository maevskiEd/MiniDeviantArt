package ed.maevski.minideviantart.data.entity

import com.google.gson.annotations.SerializedName

data class Results(
    @SerializedName("allows_comments") val allows_comments: Boolean,
    @SerializedName("author") val author: Author,
    @SerializedName("category") val category: String,
    @SerializedName("category_path") val category_path: String,
    @SerializedName("content") val content: Content,
    @SerializedName("daily_deviation") val daily_deviation: DailyDeviation,
    @SerializedName("deviationid") val deviationid: String,
    @SerializedName("download_filesize") val download_filesize: Int,
    @SerializedName("is_blocked") val is_blocked: Boolean,
    @SerializedName("is_deleted") val is_deleted: Boolean,
    @SerializedName("is_downloadable") val is_downloadable: Boolean,
    @SerializedName("is_favourited") val is_favourited: Boolean,
    @SerializedName("is_mature") val is_mature: Boolean,
    @SerializedName("is_published") val is_published: Boolean,
    @SerializedName("preview") val preview: Preview,
    @SerializedName("printid") val printid: Any,
    @SerializedName("published_time") val published_time: String,
    @SerializedName("stats") val stats: Stats,
    @SerializedName("thumbs") val thumbs: List<Thumb>,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)