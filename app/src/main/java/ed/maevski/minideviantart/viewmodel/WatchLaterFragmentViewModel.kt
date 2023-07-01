package ed.maevski.minideviantart.viewmodel

import androidx.lifecycle.ViewModel
import ed.maevski.minideviantart.App
import ed.maevski.minideviantart.domain.Interactor
import ed.maevski.minideviantart.view.notifications.entity.Notification
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WatchLaterFragmentViewModel: ViewModel() {

    lateinit var notifications: Flow<List<Notification>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        println("WatchLaterFragmentViewModel: Init")
        App.instance.dagger.inject(this)
    }
}