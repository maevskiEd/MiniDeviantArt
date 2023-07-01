package ed.maevski.minideviantart.view.rv_adapters

import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ed.maevski.minideviantart.view.adapters.NotificationDelegateAdapter

//class WatchLaterRecyclerAdapter: AbsDelegationAdapter<List<Any>>()  {
class WatchLaterRecyclerAdapter: ListDelegationAdapter<List<Any>>()  {

    init {
        delegatesManager.addDelegate(NotificationDelegateAdapter())
    }

    override fun setItems(items: List<Any>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }
}