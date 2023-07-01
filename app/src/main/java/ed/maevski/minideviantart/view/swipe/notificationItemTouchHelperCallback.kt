package ed.maevski.minideviantart.view.swipe

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ed.maevski.minideviantart.view.notifications.NotificationConstants
import ed.maevski.minideviantart.view.notifications.NotificationHelper
import ed.maevski.minideviantart.view.notifications.entity.Notification
import ed.maevski.minideviantart.view.rv_adapters.WatchLaterRecyclerAdapter

class notificationItemTouchHelperCallback(val adapter: WatchLaterRecyclerAdapter) : ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        //Drag & drop не поддерживается
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        //Настраиваем флаги для drag & drop и swipe жестов
        //Перемещение вверх и вниз элементов запрещаем поэтому ставим 0
        //Если захотим разрешить перемещение вверх и вниз, то пишем:
        // ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val dragFlags = 0
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val context = viewHolder.itemView.context
        val position = viewHolder.bindingAdapterPosition
        val notification = adapter.items?.get(position)
        NotificationHelper.notificationDb(
            NotificationConstants.ACTIONDB_DELETE,
            context,
            notification as Notification
        )
    }

}