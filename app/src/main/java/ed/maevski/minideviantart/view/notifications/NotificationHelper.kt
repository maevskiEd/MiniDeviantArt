package ed.maevski.minideviantart.view.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ed.maevski.minideviantart.R
import ed.maevski.minideviantart.receivers.NotificationClose
import ed.maevski.minideviantart.receivers.ReminderBroadcast
import ed.maevski.minideviantart.view.MainActivity
import ed.maevski.minideviantart.view.notifications.NotificationConstants.ACTION_APP
import ed.maevski.minideviantart.view.notifications.NotificationConstants.ACTION_BROWSER
import ed.maevski.remote_module.entity.DeviantPicture
import java.util.*


object NotificationHelper {
    private val id = 1

    @SuppressLint("MissingPermission")
    // Создали Интент с возвратом на главную страницу приложения и с переходом в браузер
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

    // Создали Интент с возвратом на главную страницу приложения и с переходом в браузер
    // При нажатии на нотификацию, не на кнопки переходим на главную страницу приложения PendingIntent.getActivity
    //При нажатии на кнопки в нотификации отправляем широковещательное сообщение PendingIntent.getBroadcast
    //Принимаем сервисом NotificationClose : BroadcastReceiver()
    // и там уже в коде переходим на главную или на браузер с закрытием нотификации
    @SuppressLint("MissingPermission")
    fun createNotification2(context: Context, deviantArt: DeviantPicture) {
        val notificationId = 1
        val notificationManager = NotificationManagerCompat.from(context)
        val forAppIntent : PendingIntent
        val forBrowserIntent: PendingIntent
        val forAppPendingIntent: PendingIntent
        val forBrowserPendingIntent: PendingIntent

/*        val pendingIntent: PendingIntent
        pendingIntent = */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            println("Build.VERSION_CODES.M or higher")
            forAppIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            forAppPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationClose::class.java).apply {
                    action = ACTION_APP
                    putExtra("notificationId", notificationId)
                },
                PendingIntent.FLAG_IMMUTABLE
            )

            forBrowserPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationClose::class.java).apply {
                    action = ACTION_BROWSER
                    putExtra("notificationId", notificationId)
                    putExtra("notificationBrowserUrl", deviantArt.url)
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            println("lower then Build.VERSION_CODES.M ")
            forAppIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
/*        forBrowserIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(Intent.ACTION_VIEW, Uri.parse(deviantArt.url)),
            PendingIntent.FLAG_UPDATE_CURRENT
        )*/

            forAppPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationClose::class.java).apply {
                    action = "actionApp"
                    putExtra("notificationId", notificationId)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            forBrowserPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationClose::class.java).apply {
                    action = "actionBrowser"
                    putExtra("notificationId", notificationId)
                    putExtra("notificationBrowserUrl", deviantArt.url)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


        val actionApp = NotificationCompat.Action.Builder(
            null,
            "Вернуться в приложение",
            forAppPendingIntent
        ).build()
        val actionBrowser = NotificationCompat.Action.Builder(
            null,
            "Открыть в браузере",
            forBrowserPendingIntent
        ).build()

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.baseline_watch_later_24)
            setContentTitle("Не забудьте посмотреть!")
            setContentText(deviantArt.title)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setOngoing(false)
            addAction(actionApp)
            addAction(actionBrowser)

/*            addAction(
                R.drawable.baseline_watch_later_24,
                "Вернуться в приложение",
                forAppPendingIntent
            )
            addAction(
                R.drawable.baseline_watch_later_24,
                "Открыть в браузере",
                forBrowserPendingIntent
            )*/
            setContentIntent(forAppIntent)
            setAutoCancel(true)
        }

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

    fun notificationSet(context: Context, art: DeviantPicture) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(
            context,
            { _, dpdYear, dpdMonth, dayOfMonth ->
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, pickerMinute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(
                            dpdYear,
                            dpdMonth,
                            dayOfMonth,
                            hourOfDay,
                            pickerMinute,
                            0
                        )
                        val dateTimeInMillis = pickedDateTime.timeInMillis
                        //После того, как получим время, вызываем метод, который создаст Alarm
                        createWatchLaterEvent(context, dateTimeInMillis, art)
                    }

                TimePickerDialog(
                    context,
                    timeSetListener,
                    currentHour,
                    currentMinute,
                    true
                ).show()

            },
            currentYear,
            currentMonth,
            currentDay
        ).show()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createWatchLaterEvent(context: Context, dateTimeInMillis: Long, art: DeviantPicture) {
        //Получаем доступ к AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //Создаем интент для запуска ресивера
        val intent = Intent(art.title, null, context, ReminderBroadcast()::class.java)
        //Кладем в него фильм
        val bundle = Bundle()
        bundle.putParcelable(NotificationConstants.ART_KEY, art)
        intent.putExtra(NotificationConstants.ART_BUNDLE_KEY, bundle)
        //Создаем пендинг интент для запуска извне приложения
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Устанавливаем Alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            dateTimeInMillis,
            pendingIntent
        )
    }
}