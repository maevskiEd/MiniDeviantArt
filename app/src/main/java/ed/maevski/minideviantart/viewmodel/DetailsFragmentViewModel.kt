package ed.maevski.minideviantart.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import ed.maevski.minideviantart.App
import ed.maevski.minideviantart.domain.Interactor
import ed.maevski.minideviantart.view.notifications.NotificationHelper
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DetailsFragmentViewModel : ViewModel() {

    //Инициализируем интерактор
    //Используем для того, чтобы работать с interactor в DetailsFragment, а также передать в
    //NotificationHelper в методе инициализации
    @Inject
    lateinit var interactor: Interactor

    init {
        println("WatchLaterFragmentViewModel: Init")
        App.instance.dagger.inject(this)
    }


    suspend fun loadWallpaper(url: String): Bitmap {
        return suspendCoroutine {
            val url = URL(url)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            it.resume(bitmap)
        }
    }
}