package ed.maevski.minideviantart.data.dao

import androidx.room.*
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.flow.Flow

//Помечаем, что это не просто интерфейс, а Dao-объект
@Dao
interface ArtDao {
    //Запрос на всю таблицу
    @Query("SELECT * FROM cached_arts")
    fun getCachedFilms(): Flow<List<ed.maevski.remote_module.entity.DeviantPicture>>

    @Query("SELECT * FROM cached_arts WHERE setting LIKE :search")
    fun getCachedFilmsWithCategory(search: String): Flow<List<ed.maevski.remote_module.entity.DeviantPicture>>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<ed.maevski.remote_module.entity.DeviantPicture>)

    @Delete
    fun deleteArt(art: ed.maevski.remote_module.entity.DeviantPicture)
}