package ed.maevski.minideviantart.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ed.maevski.remote_module.Item
import ed.maevski.minideviantart.databinding.ItemFavoritePictureBinding

class FavoriteDelegateAdapter() :
    AbsListItemAdapterDelegate<ed.maevski.remote_module.entity.DeviantPicture, Item, FavoriteDelegateAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemFavoritePictureBinding) : RecyclerView.ViewHolder(binding.root) {
        val pic = binding.pic
    }

    override fun isForViewType(item: Item, items: MutableList<Item>, position: Int): Boolean {
        return item is ed.maevski.remote_module.entity.DeviantPicture
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemFavoritePictureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(item: ed.maevski.remote_module.entity.DeviantPicture, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.pic.setImageResource(item.picture)
    }
}