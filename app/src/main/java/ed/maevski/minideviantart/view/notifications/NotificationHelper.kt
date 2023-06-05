package ed.maevski.minideviantart.view.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ed.maevski.minideviantart.R
import ed.maevski.minideviantart.view.MainActivity
import ed.maevski.remote_module.entity.DeviantPicture

object NotificationHelper {
    private val id = 1

    @SuppressLint("MissingPermission")
    // Создали Интент с возвратом на главную страницу приложения
    // После добавления кнопок addAction, нотификация автоматически не удаляется
    fun createNotification(context: Context, deviantArt: DeviantPicture) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val forBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deviantArt.url))
        val forBrowserPendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                forBrowserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.baseline_watch_later_24)
            setContentTitle("Не забудьте посмотреть!")
            setContentText(deviantArt.title)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setOngoing(false)

            addAction(
                R.drawable.baseline_watch_later_24,
                "Вернуться в приложение",
                pendingIntent
            ) // возвращение в приложение
            addAction(
                R.drawable.baseline_watch_later_24,
                "Открыть в браузере",
                forBrowserPendingIntent
            ) // переход в браузер
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }
        val notificationManager = NotificationManagerCompat.from(context)

        Glide.with(context)
            //говорим, что нужен битмап
            .asBitmap()
            //указываем, откуда загружать, это ссылка, как на загрузку с API
            .load(deviantArt.urlThumb150)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                //Этот коллбэк отрабатывает, когда мы успешно получим битмап
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //Создаем нотификации в стиле big picture
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                    //Обновляем нотификацию

                    notificationManager.notify(id, builder.build())
                }
            })
        //Отправляем изначальную нотификацию в стандартном исполнении
        notificationManager.notify(id, builder.build())
    }

    @SuppressLint("MissingPermission")
    // Создали Интент с переходом в браузер по ссылке
    // В этом методе нотификация удаляется автоматически, но здесь задаем setContentIntent(pendingIntent)
    fun createForBrowserNotification(context: Context, deviantArt: DeviantPicture) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deviantArt.url))
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.baseline_watch_later_24)
            setContentTitle("Не забудьте посмотреть!")
            setContentText(deviantArt.title)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        Glide.with(context)
            //говорим, что нужен битмап
            .asBitmap()
            //указываем, откуда загружать, это ссылка, как на загрузку с API
            .load(deviantArt.urlThumb150)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                //Этот коллбэк отрабатывает, когда мы успешно получим битмап
                @SuppressLint("MissingPermission")
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //Создаем нотификации в стиле big picture
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                    //Обновляем нотификацию

                    notificationManager.notify(id, builder.build())
                }
            })
        //Отправляем изначальную нотификацию в стандартном исполнении
        notificationManager.notify(id, builder.build())
    }
}