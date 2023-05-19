package ed.maevski.minideviantart

import android.app.Application
import ed.maevski.minideviantart.di.AppComponent
import ed.maevski.minideviantart.di.DaggerAppComponent
import ed.maevski.minideviantart.di.modules.DatabaseModule
import ed.maevski.minideviantart.di.modules.DomainBindsModule
import ed.maevski.minideviantart.di.modules.DomainModule
import ed.maevski.minideviantart.di.modules.RemoteModule
import ed.maevski.minideviantart.domain.Token


class App : Application() {
    lateinit var dagger: AppComponent
//    lateinit var token: Token

    override fun onCreate() {
        super.onCreate()
        //Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        instance = this
        //Создаем компонент
        dagger = DaggerAppComponent.builder()
            .remoteModule(RemoteModule())
            .databaseModule(DatabaseModule())
            .domainModule(DomainModule(this))
            .build()

    }

    companion object {
        //Здесь статически хранится ссылка на экземпляр App
        lateinit var instance: App
            //Приватный сеттер, чтобы нельзя было в эту переменную присвоить что-либо другое
            private set
    }
}