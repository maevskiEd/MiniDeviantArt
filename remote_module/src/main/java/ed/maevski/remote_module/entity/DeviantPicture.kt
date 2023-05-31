package ed.maevski.remote_module.entity

import android.content.ClipData
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ed.maevski.remote_module.Item
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cached_arts", indices = [Index(value = ["title"], unique = true)])
data class DeviantPicture(
    @PrimaryKey override val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String,
    val picture: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "url") val url: String,
    val urlThumb150: String,
    @ColumnInfo(name = "countFavorites") val countFavorites: Int = 0,
    @ColumnInfo(name = "countComments") val comments: Int = 0,
    @ColumnInfo(name = "countViews") val countViews: Int = 0,
    override var isInFavorites: Boolean = false,
    @ColumnInfo(name = "setting") val setting: String
) : Item, Parcelable

