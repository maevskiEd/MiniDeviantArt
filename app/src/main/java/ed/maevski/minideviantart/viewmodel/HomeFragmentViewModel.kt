package ed.maevski.minideviantart.viewmodel

import androidx.lifecycle.ViewModel
import ed.maevski.minideviantart.App
import ed.maevski.minideviantart.domain.Interactor
import ed.maevski.remote_module.entity.DeviantPicture
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {

    lateinit var picturesListData: Flow<List<DeviantPicture>>

    //Инициализируем интерактор
    @Inject
    lateinit var interactor: Interactor

    init {
        println("HomeFragmentViewModel: Init")
        App.instance.dagger.inject(this)

//        picturesListData = interactor.getDeviantPicturesFromDBWithCategory()
//        getDeviantArts()
    }


    fun getDeviantArts() {
        println("getDeviantArts")
        interactor.getDeviantArtsFromApi(1)
    }
}