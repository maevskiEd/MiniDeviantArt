package ed.maevski.minideviantart.view.rv_adapters

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ed.maevski.remote_module.Item
import ed.maevski.minideviantart.view.adapters.AdDelegateAdapter
import ed.maevski.minideviantart.view.adapters.FavoriteDelegateAdapter

class FavoriteRecyclerAdapter() : ListDelegationAdapter<List<Item>>() {

    init {
        delegatesManager.addDelegate(AdDelegateAdapter())
        delegatesManager.addDelegate(FavoriteDelegateAdapter())
    }

    override fun setItems(items: List<Item>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }
}