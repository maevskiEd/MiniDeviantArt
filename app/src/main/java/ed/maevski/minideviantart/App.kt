package ed.maevski.minideviantart

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import ed.maevski.minideviantart.di.AppComponent
import ed.maevski.minideviantart.di.DaggerAppComponent
import ed.maevski.minideviantart.di.modules.DatabaseModule
import ed.maevski.minideviantart.di.modules.DomainModule
import ed.maevski.minideviantart.view.notifications.NotificationConstants.CHANNEL_ID
import ed.maevski.remote_module.DaggerRemoteComponent


class App : Application() {
    lateinit var dagger: AppComponent
    var isPromoShown = false
//    lateinit var token: Token

    override fun onCreate() {
        super.onCreate()
        //Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        instance = this
        val remoteProvider = DaggerRemoteComponent.create()
        //Создаем компонент
        dagger = DaggerAppComponent.builder()
            .remoteProvider(remoteProvider)
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Задаем имя, описание и важность канала
            val name = "WatchLaterChannel"
            val descriptionText = "MiniDeviantart notification Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            //Создаем канал, передав в параметры его ID(строка), имя(строка), важность(константа)
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            //Отдельно задаем описание
            mChannel.description = descriptionText
            //Получаем доступ к менеджеру нотификаций
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //Регистрируем канал
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        //Здесь статически хранится ссылка на экземпляр App
        lateinit var instance: App
            //Приватный сеттер, чтобы нельзя было в эту переменную присвоить что-либо другое
            private set
    }
}