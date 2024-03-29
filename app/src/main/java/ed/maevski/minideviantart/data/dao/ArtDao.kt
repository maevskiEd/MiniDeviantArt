package ed.maevski.minideviantart.data.dao

import androidx.room.*
import ed.maevski.minideviantart.view.notifications.entity.Notification
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.flow.Flow

//Помечаем, что это не просто интерфейс, а Dao-объект
@Dao
interface ArtDao {
    //Запрос на всю таблицу
    @Query("SELECT * FROM cached_arts")
    fun getCachedFilms(): Flow<List<DeviantPicture>>

    @Query("SELECT * FROM cached_arts WHERE setting LIKE :search")
    fun getCachedFilmsWithCategory(search: String): Flow<List<DeviantPicture>>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<DeviantPicture>)

    @Delete
    fun deleteArt(art: DeviantPicture)

    @Query("SELECT * FROM notifications WHERE dateTimeInMillis > (strftime('%s', 'now') * 1000)")
    fun getNotifications(): Flow<List<Notification>>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)

    @Delete
    fun deleteNotification(notification: Notification)

    @Query("UPDATE notifications SET dateTimeInMillis = :dateTimeInMillis WHERE id = :id AND dateTimeInMillis = :oldDateTimeInMillis")
    fun updateNotification(
        id: String,
        dateTimeInMillis: Long,
        oldDateTimeInMillis: Long
    )
}