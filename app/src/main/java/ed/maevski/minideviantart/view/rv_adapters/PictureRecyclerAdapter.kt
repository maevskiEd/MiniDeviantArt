package ed.maevski.minideviantart.view.rv_adapters

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import ed.maevski.remote_module.Item
import ed.maevski.minideviantart.view.adapters.AdDelegateAdapter
import ed.maevski.minideviantart.view.adapters.PictureDelegateAdapter

class PictureRecyclerAdapter(private val clickListener: OnItemClickListener) : ListDelegationAdapter<List<Item>>() {

    init {
        delegatesManager.addDelegate(PictureDelegateAdapter(clickListener))
        delegatesManager.addDelegate(AdDelegateAdapter())
    }

    override fun setItems(items: List<Item>?) {
        super.setItems(items)
        notifyDataSetChanged()
    }

    //Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(picture: ed.maevski.remote_module.entity.DeviantPicture)
    }
}