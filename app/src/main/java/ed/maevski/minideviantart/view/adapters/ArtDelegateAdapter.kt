package ed.maevski.minideviantart.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ed.maevski.minideviantart.data.entity.DeviantPicture
import ed.maevski.minideviantart.databinding.ItemArtBinding
import ed.maevski.minideviantart.domain.Item
import ed.maevski.minideviantart.view.rv_adapters.ArtRecyclerAdapter

class ArtDelegateAdapter (private val clickListener: ArtRecyclerAdapter.OnItemClickListener) :
    AbsListItemAdapterDelegate<DeviantPicture, Item, ArtDelegateAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemArtBinding) : RecyclerView.ViewHolder(binding.root) {
        val art = binding.art
        val item_container = binding.itemContainer
    }

    override fun isForViewType(item: Item, items: MutableList<Item>, position: Int): Boolean {
        return item is DeviantPicture
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(item: DeviantPicture, holder: ViewHolder, payloads: MutableList<Any>) {
//        holder.picture.setImageResource(item.picture)

        //Указываем контейнер, в котором будет "жить" наша картинка
        Glide.with(holder.item_container)
            //Загружаем сам ресурс
            .load(item.urlThumb150)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(holder.art)

        holder.item_container.setOnClickListener{
            clickListener.click(item)
        }
    }
}