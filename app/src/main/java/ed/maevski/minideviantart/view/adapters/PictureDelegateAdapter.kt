package ed.maevski.minideviantart.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ed.maevski.remote_module.Item
import ed.maevski.minideviantart.databinding.ItemPictureBinding
import ed.maevski.minideviantart.view.rv_adapters.PictureRecyclerAdapter

class PictureDelegateAdapter(private val clickListener: PictureRecyclerAdapter.OnItemClickListener) :
    AbsListItemAdapterDelegate<ed.maevski.remote_module.entity.DeviantPicture, Item, PictureDelegateAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        val picture = binding.poster
        val title = binding.title
        val author = binding.author
        val description = binding.description
        val item_container = binding.itemContainer
    }

    override fun isForViewType(item: Item, items: MutableList<Item>, position: Int): Boolean {
        return item is ed.maevski.remote_module.entity.DeviantPicture
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemPictureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(item: ed.maevski.remote_module.entity.DeviantPicture, holder: ViewHolder, payloads: MutableList<Any>) {
//        holder.picture.setImageResource(item.picture)

        //Указываем контейнер, в котором будет "жить" наша картинка
        Glide.with(holder.item_container)
            //Загружаем сам ресурс
            .load(item.urlThumb150)
            //Центруем изображение
            .centerCrop()
            //Указываем ImageView, куда будем загружать изображение
            .into(holder.picture)

        holder.title.text = item.title
        holder.author.text = item.author
        holder.description.text = item.description

        holder.item_container.setOnClickListener{
            clickListener.click(item)
        }
    }
}