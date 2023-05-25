package ed.maevski.minideviantart.view.rv_adapters

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ed.maevski.remote_module.Item
import ed.maevski.minideviantart.view.adapters.ArtDelegateAdapter
import ed.maevski.remote_module.entity.DeviantPicture

class ArtRecyclerAdapter (private val clickListener: OnItemClickListener) : ListDelegationAdapter<List<Item>>() {

    init {
        delegatesManager.addDelegate(ArtDelegateAdapter(clickListener))
    }

    override fun setItems(items: List<Item>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }

    //Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(picture: DeviantPicture)
    }
}