package ed.maevski.minideviantart.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ed.maevski.remote_module.Item

class FavoritesFragmentViewModel: ViewModel() {
    val picturesListLiveData = MutableLiveData<List<Item>>()

//    private var interactor: Interactor = App.instance.interactor

    init {
/*        val films = interactor.getPicturesDB()
        picturesListLiveData.postValue(films)*/
    }
}