package ed.maevski.minideviantart.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import ed.maevski.minideviantart.view.MainActivity

class NotificationClose : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationId = intent?.getIntExtra("notificationId", -1)
        println("notificationId -> $notificationId")
        intent?.removeExtra("notificationId")
        println("intent?.action -> ${intent?.action}")

        when (intent?.action) {
            "actionApp" -> {
                val mIntent = Intent(context, MainActivity::class.java)
                mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(mIntent)
            }
            "actionBrowser" -> {
                val mUrl = intent.getStringExtra("notificationBrowserUrl")
                intent.removeExtra("notificationBrowserUrl")
                val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))
                mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(mIntent)
            }
        }

        if (notificationId != -1) {
            val notificationManager = context?.let { NotificationManagerCompat.from(it) }
            if (notificationId != null) {
                notificationManager?.cancel(notificationId)
            }
        }
    }
}