package ed.maevski.minideviantart.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ed.maevski.minideviantart.view.notifications.NotificationConstants
import ed.maevski.minideviantart.view.notifications.NotificationHelper
import ed.maevski.minideviantart.view.notifications.entity.Notification
import ed.maevski.remote_module.entity.DeviantPicture

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getBundleExtra(NotificationConstants.ART_BUNDLE_KEY)
        val notification: Notification = bundle?.get(NotificationConstants.ART_KEY) as Notification

        NotificationHelper.createLightNotification(context!!, notification)
    }
}