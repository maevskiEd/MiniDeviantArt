package ed.maevski.minideviantart.view.notifications.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "notifications",
    primaryKeys = ["id", "dateTimeInMillis"],
    indices = [
        Index(value = ["dateTimeInMillis"]),
        Index(value = ["id", "dateTimeInMillis"], unique = true),
    ],
)
data class Notification(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "urlThumb150") val urlThumb150: String,
    @ColumnInfo(name = "action") val action: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "dateTimeInMillis") var dateTimeInMillis: Long
) : Parcelable
