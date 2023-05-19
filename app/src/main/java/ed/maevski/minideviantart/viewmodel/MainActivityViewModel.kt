package ed.maevski.minideviantart.viewmodel

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import ed.maevski.minideviantart.App
import ed.maevski.minideviantart.domain.Interactor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivityViewModel : ViewModel() {
    var flagToken = Channel<Boolean>(Channel.CONFLATED)
    var errorEvent = Channel<Boolean>(Channel.CONFLATED)

    private var access_token: String

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        println("MainActivityViewModel: Init")

        App.instance.dagger.inject(this)
        access_token = interactor.getAccessTokenFromPreferences()

        MainScope().launch {
            errorEvent.send(false)
            initToken(this)
        }
    }

    fun initToken(scope: CoroutineScope) {
        scope.launch {
            flagToken.send(true)
            if (access_token.isEmpty()) {
                launch {
                    println("initToken: then")
                    interactor.getTokenFromApi(this)
                    println("initToken: MainScope: flagToken - false")
                    flagToken.send(false)
                }
            } else {
                val def = async {
                    println("initToken: else")
                    val s = interactor.checkToken(this, access_token)
                    println("initToken: else, after checkToken")
                    s
                }
                val result = def.await()
                if (result == "error") {
                    launch {
                        println("initToken: else then")
                        interactor.getTokenFromApi(this)
                        println("initToken: MainScope: flagToken - false")
                        flagToken.send(false)
                    }
                } else flagToken.send(false)
            }
        }
    }
}