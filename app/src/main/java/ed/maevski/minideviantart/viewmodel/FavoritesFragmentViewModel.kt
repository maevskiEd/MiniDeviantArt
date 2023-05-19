package ed.maevski.minideviantart.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ed.maevski.minideviantart.App
import ed.maevski.minideviantart.domain.Interactor
import ed.maevski.minideviantart.domain.Item

class FavoritesFragmentViewModel: ViewModel() {
    val picturesListLiveData = MutableLiveData<List<Item>>()

//    private var interactor: Interactor = App.instance.interactor

    init {
/*        val films = interactor.getPicturesDB()
        picturesListLiveData.postValue(films)*/
    }
}