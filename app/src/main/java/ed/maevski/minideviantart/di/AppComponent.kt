package ed.maevski.minideviantart.di

import dagger.Component
import ed.maevski.minideviantart.di.modules.DatabaseModule
import ed.maevski.minideviantart.di.modules.DomainBindsModule
import ed.maevski.minideviantart.di.modules.DomainModule
import ed.maevski.minideviantart.viewmodel.HomeFragmentViewModel
import ed.maevski.minideviantart.viewmodel.MainActivityViewModel
import ed.maevski.minideviantart.viewmodel.SettingsFragmentViewModel
import ed.maevski.remote_module.RemoteProvider
import javax.inject.Singleton

@Singleton
@Component(
    //Внедряем все модули, нужные для этого компонента
    dependencies = [RemoteProvider::class],
    modules = [
        DatabaseModule::class,
        DomainModule::class,
        DomainBindsModule::class
    ]
)
interface AppComponent {
    //метод для того, чтобы появилась внедрять зависимости в HomeFragmentViewModel
    fun inject(mainActivityViewModel: MainActivityViewModel)

    //метод для того, чтобы появилась внедрять зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    //метод для того, чтобы появилась возможность внедрять зависимости в SettingsFragmentViewModel
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}