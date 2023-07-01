package ed.maevski.minideviantart.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import ed.maevski.minideviantart.R
import ed.maevski.minideviantart.databinding.ItemNotificationBinding
import ed.maevski.minideviantart.utils.DateTimePicker
import ed.maevski.minideviantart.view.notifications.NotificationConstants
import ed.maevski.minideviantart.view.notifications.NotificationHelper
import ed.maevski.minideviantart.view.notifications.entity.Notification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationDelegateAdapter :
    AbsListItemAdapterDelegate<Notification, Any, NotificationDelegateAdapter.ViewHolder>() {
    //    private var items: MutableList<Notification> = mutableListOf()
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru", "RU"))

    class ViewHolder(binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        val pic = binding.pic
        val title = binding.title
        val date_time = binding.dateTime
        val item_container = binding.itemContainer
    }

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is Notification
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(
        item: Notification,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        //обрабатываем долгое нажатие на напоминании для смены даты времени срабатывания нотификации
        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context
            val position = holder.bindingAdapterPosition
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru", "RU"))

            if (position != RecyclerView.NO_POSITION) {
                // Обработка долгого нажатия на элемент
                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                val oldDateTimeInMillis = item.dateTimeInMillis

                println("NotificationDelegateAdapter : Время в милисекундках эпохи: ${item.dateTimeInMillis} ")
                println("NotificationDelegateAdapter :Дата в удобном виде: ${format.format(Date(item.dateTimeInMillis))}")

                val notification = item
                DateTimePicker(context) { dateTimeInMillis ->
                    notification.dateTimeInMillis = dateTimeInMillis

                    println("NotificationDelegateAdapter : DateTimePicker: Время в милисекундках эпохи: ${notification.dateTimeInMillis} ")
                    println("NotificationDelegateAdapter : DateTimePicker: Дата в удобном виде: ${format.format(Date(notification.dateTimeInMillis))}")

                    println("NotificationDelegateAdapter : DateTimePicker: Время в милисекундках эпохи: oldDateTimeInMillis: ${oldDateTimeInMillis} ")
                    println("NotificationDelegateAdapter : DateTimePicker: Дата в удобном виде: oldDateTimeInMillis:${format.format(Date(oldDateTimeInMillis))}")

                    NotificationHelper.notificationDb(
                        NotificationConstants.ACTIONDB_UPDATE,
                        context,
                        notification,
                        oldDateTimeInMillis
                    )
                }
                NotificationHelper.cancelWatchLaterEvent(context, item)
                NotificationHelper.createWatchLaterEvent(context, item)
                true // Возвращаем true, чтобы указать, что событие обработано
            } else {
                false // Возвращаем false, если позиция недействительна
            }
        }

        Glide.with(holder.item_container)
            //Загружаем сам ресурс
            .load(item.urlThumb150)
            .placeholder(R.drawable.ic_round_photo_album)
            .error(R.drawable.ic_baseline_cloud_download_24)
            //Указываем ImageView, куда будем загружать изображение
            .into(holder.pic)

        holder.title.text = item.title
        holder.date_time.text = format.format(Date(item.dateTimeInMillis))
    }
}