package ed.maevski.minideviantart.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ed.maevski.minideviantart.data.dao.ArtDao
import ed.maevski.minideviantart.data.entity.DeviantPicture

@Database(entities = [DeviantPicture::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}