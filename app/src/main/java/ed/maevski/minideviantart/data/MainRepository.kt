package ed.maevski.minideviantart.data

import ed.maevski.minideviantart.data.dao.ArtDao
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.flow.Flow

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
}