package ed.maevski.minideviantart.domain

import ed.maevski.minideviantart.data.*
import ed.maevski.minideviantart.view.notifications.entity.Notification
import ed.maevski.remote_module.DeviantartApi
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Interactor(
    private val repo: MainRepository,
    private val retrofitService: DeviantartApi,
    var token: Token,
    private val preferences: PreferenceProvider
) {
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //и страницу, которую нужно загрузить (это для пагинации)
    fun getDeviantArtsFromApi(page: Int) {
        val accessToken = preferences.getToken()
        var list: List<DeviantPicture>? = null

        println("getDeviantArtsFromApi")
        retrofitService.getPictures(getDefaultCategoryFromPreferences(), accessToken, 0, 20)
            .enqueue(object : Callback<ed.maevski.remote_module.entity.DeviantartResponse> {

                override fun onResponse(
                    call: Call<ed.maevski.remote_module.entity.DeviantartResponse>,
                    response: Response<ed.maevski.remote_module.entity.DeviantartResponse>
                ) {
                    println("getDeviantArtsFromApi: onResponse")

                    scope.launch {
                        launch {
                            println("getDeviantArtsFromApi: onResponse: map: Begin")

                            response.body()?.let { response ->
                                list =
                                    listOf(response.results).asFlow()?.flatMapConcat { it.asFlow() }
                                        ?.filter{it.category == "Visual Art"}
                                        ?.map { it ->
                                            DeviantPicture(
                                                id = it.deviationid,
                                                title = it.title,
                                                author = it.author.username,
                                                picture = 0,
                                                description = "",
                                                url = it.preview.src,
                                                urlThumb150 = it.thumbs[0].src,
                                                countFavorites = it.stats.favourites,
                                                comments = it.stats.comments,
                                                countViews = 100000,
                                                isInFavorites = false,
                                                setting = preferences.getDefaultCategory()
                                            )
                                        }?.toList()
                            }
                            println("getDeviantArtsFromApi: onResponse: map: End")
                        }.join()

                        launch {
                            println("call: $call")
                            println("response: $response")
                            println("response.isSuccessful: ${response.isSuccessful}")
                            println("response.body: ${response.body()}")
                            println("response.code: ${response.code()}")
                            println("response.headers: ${response.headers()}")
                            println("response.errorBody: ${response.errorBody()}")
                            println("response.message: ${response.message()}")
                            println("response.raw: ${response.raw()}")
                            println(response.body()?.results)
                            println("list: $list ")

                            list?.let { repo.putToDb(it) }
                        }
                    }
                }

                override fun onFailure(call: Call<ed.maevski.remote_module.entity.DeviantartResponse>, t: Throwable) {
                    println("override fun onFailure(call: Call<DeviantartResponse>, t: Throwable)")
                    //В случае провала вызываем другой метод коллбека
                    println("getDeviantArtsFromApi: onFailure")
                }
            })
    }

    suspend fun getTokenFromApi(scope: CoroutineScope) {
        return suspendCoroutine { continuation ->
            scope.launch {
                launch {
                    retrofitService.getToken("client_credentials", API.CLIENT_ID, API.CLIENT_SECRET)
                        .enqueue(object : Callback<ed.maevski.remote_module.entity_token.TokenResponse> {

                            override fun onResponse(
                                call: Call<ed.maevski.remote_module.entity_token.TokenResponse>,
                                response: Response<ed.maevski.remote_module.entity_token.TokenResponse>
                            ) {
                                println("getTokenFromApi: onResponse")

                                if (response.body()?.status.equals("success")) {
                                    println("getTokenFromApi: onResponse -> success")

                                    saveAccessTokenFromPreferences(
                                        response.body()?.access_token ?: ""
                                    )
                                    continuation.resume(Unit)
                                } else {
                                    continuation.resume(Unit)
//                        errorEvent.post
                                }
                            }

                            override fun onFailure(call: Call<ed.maevski.remote_module.entity_token.TokenResponse>, t: Throwable) {
                                continuation.resume(Unit)
//                    t.printStackTrace()
//                    errorEvent.postValue("connect ERROR")
                            }
                        })
                }
            }
        }
    }

    suspend fun checkToken(scope: CoroutineScope, accessToken: String): String {
        return suspendCoroutine { continuation ->
            scope.launch {
                launch {
                    retrofitService.checkToken(accessToken)
                        .enqueue(object : Callback<ed.maevski.remote_module.entity_token.TokenPlaceboResponse> {

                            override fun onResponse(
                                call: Call<ed.maevski.remote_module.entity_token.TokenPlaceboResponse>,
                                response: Response<ed.maevski.remote_module.entity_token.TokenPlaceboResponse>
                            ) {
                                println("checkToken: onResponse")

                                if (response.body()?.status.equals("success")) {
                                    println("checkToken: onResponse  -> success")
                                    println("response.body()?.status : ${response.body()?.status}")

                                    continuation.resume("success")

                                } else {
                                    println("checkToken: onResponse  -> error")

                                    continuation.resume("error")

                                }
                            }

                            override fun onFailure(call: Call<ed.maevski.remote_module.entity_token.TokenPlaceboResponse>, t: Throwable) {
//                    t.printStackTrace()
                                println("checkToken: onFailure")

                                continuation.resume("Throwable error")
                            }
                        })

                }.join()
                println("checkToken: Главный job этой функции")
            }
        }
    }

    //Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    //Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    fun getAccessTokenFromPreferences() = preferences.getToken()

    fun saveAccessTokenFromPreferences(accessToken: String) {
        preferences.saveToken(accessToken)
    }

    fun getDeviantPicturesFromDB(): Flow<List<DeviantPicture>> = repo.getAllFromDB()

    fun getDeviantPicturesFromDBWithCategory(): Flow<List<DeviantPicture>> {
        println("getDeviantPicturesFromDBWithCategory -> ${preferences.getDefaultCategory()}")
        return repo.getCategoryFromDB(preferences.getDefaultCategory())
    }

    fun putNotificationToDb(notification: Notification){
        scope.launch {
            repo.putNotificationToDb(notification)
        }
    }

    fun updateNotification(notification: Notification, oldDateTimeInMillis: Long){
        val newDateMilis = notification.dateTimeInMillis
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru", "RU"))
        println("Interactor: Время в милисекундках эпохи: oldDateTimeInMillis: ${oldDateTimeInMillis} ")
        println("Interactor: Дата в удобном виде: oldDateTimeInMillis:${format.format(
            Date(oldDateTimeInMillis)
        )}")
        println("Interactor: Время в милисекундках эпохи: ${notification.dateTimeInMillis} ")
        println("Interactor: Дата в удобном виде:${format.format(Date(notification.dateTimeInMillis))}")

        scope.launch {
            println("Scope: Interactor: Время в милисекундках эпохи: oldDateTimeInMillis: ${oldDateTimeInMillis} ")
            println("Scope: Interactor: Дата в удобном виде: oldDateTimeInMillis:${format.format(
                Date(oldDateTimeInMillis)
            )}")

            notification.dateTimeInMillis = newDateMilis

            println("Scope:Interactor: Время в милисекундках эпохи: ${notification.dateTimeInMillis} ")
            println("Scope:Interactor: Дата в удобном виде:${format.format(Date(notification.dateTimeInMillis))}")

            repo.updateNotification(notification, oldDateTimeInMillis)
        }
    }

    fun getNotification(): Flow<List<Notification>> {
        return repo.getNotifications()
    }

    fun deleteNotificationFromDb(notification: Notification) {
        scope.launch {
            repo.deleteNotificationFromDb(notification)
        }
    }
}
