package ed.maevski.minideviantart.data

import androidx.room.ColumnInfo
import ed.maevski.minideviantart.data.dao.ArtDao
import ed.maevski.minideviantart.view.notifications.entity.Notification
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainRepository(private val artDao: ArtDao) {
    fun putToDb(films: List<DeviantPicture>) {
        artDao.insertAll(films)
    }

    fun getAllFromDB(): Flow<List<DeviantPicture>> {
        return artDao.getCachedFilms()
    }

    fun getCategoryFromDB(setting: String): Flow<List<DeviantPicture>> {
        return artDao.getCachedFilmsWithCategory(setting)
    }

    fun deleteFromDB(deviantPicture: DeviantPicture) {
        artDao.deleteArt(deviantPicture)
    }

    fun putNotificationToDb(notification: Notification) {
        artDao.insertNotification(notification)
    }

    fun getNotifications(): Flow<List<Notification>> {
        return artDao.getNotifications()
    }

    fun deleteNotificationFromDb(notification: Notification) {
        artDao.deleteNotification(notification)
    }

    fun updateNotification(notification: Notification, oldDateTimeInMillis: Long) {
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru", "RU"))

        println("MainRepository: updateNotification: Время в милисекундках эпохи: oldDateTimeInMillis: ${oldDateTimeInMillis} ")
        println(
            "MainRepository: updateNotification: Дата в удобном виде: oldDateTimeInMillis:${
                format.format(
                    Date(oldDateTimeInMillis)
                )
            }"
        )

        println("MainRepository: updateNotification: Время в милисекундках эпохи: ${notification.dateTimeInMillis} ")
        println(
            "MainRepository: updateNotification: Дата в удобном виде:${
                format.format(
                    Date(notification.dateTimeInMillis)
                )
            }"
        )

                artDao.updateNotification(
                    notification.id,
                    notification.dateTimeInMillis,
                    oldDateTimeInMillis
                )
    }
}