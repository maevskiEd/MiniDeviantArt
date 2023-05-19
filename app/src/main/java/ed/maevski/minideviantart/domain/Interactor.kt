package ed.maevski.minideviantart.domain

import ed.maevski.minideviantart.data.*
import ed.maevski.minideviantart.data.entity.DeviantPicture
import ed.maevski.minideviantart.data.entity.DeviantartResponse
import ed.maevski.minideviantart.data.entity_token.TokenPlaceboResponse
import ed.maevski.minideviantart.data.entity_token.TokenResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
            .enqueue(object : Callback<DeviantartResponse> {

                override fun onResponse(
                    call: Call<DeviantartResponse>,
                    response: Response<DeviantartResponse>
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

                override fun onFailure(call: Call<DeviantartResponse>, t: Throwable) {
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
                        .enqueue(object : Callback<TokenResponse> {

                            override fun onResponse(
                                call: Call<TokenResponse>,
                                response: Response<TokenResponse>
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

                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
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
                        .enqueue(object : Callback<TokenPlaceboResponse> {

                            override fun onResponse(
                                call: Call<TokenPlaceboResponse>,
                                response: Response<TokenPlaceboResponse>
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

                            override fun onFailure(call: Call<TokenPlaceboResponse>, t: Throwable) {
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
}
