package ed.maevski.minideviantart.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ed.maevski.remote_module.DeviantartApi
import ed.maevski.minideviantart.data.MainRepository
import ed.maevski.minideviantart.data.PreferenceProvider
import ed.maevski.minideviantart.domain.Interactor
import ed.maevski.minideviantart.domain.Token
import javax.inject.Singleton

@Module
class DomainModule(val context: Context) {
    //Нам нужно контекст как-то провайдить, поэтому создаем такой метод
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    //Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)

/*    @Singleton
    @Provides
    fun provideToken() = Token()*/

    @Singleton
    @Provides
    fun provideInteractor(
        repository: MainRepository,
        deviantartApi: DeviantartApi,
        token: Token,
        preferenceProvider: PreferenceProvider
    ) = Interactor(
        repo = repository,
        retrofitService = deviantartApi,
        token = token,
        preferences = preferenceProvider
    )

}