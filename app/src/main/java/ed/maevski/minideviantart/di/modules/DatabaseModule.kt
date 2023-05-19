package ed.maevski.minideviantart.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ed.maevski.minideviantart.data.MainRepository
import ed.maevski.minideviantart.data.dao.ArtDao
import ed.maevski.minideviantart.data.db.AppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideArtDao(context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "art_db"
        ).build().artDao()

    @Provides
    @Singleton
    fun provideRepository(artDao: ArtDao) = MainRepository(artDao)
}