package ed.maevski.minideviantart.di.modules

import dagger.Binds
import dagger.Module
import ed.maevski.minideviantart.domain.AbstractToken
import ed.maevski.minideviantart.domain.Token
import javax.inject.Singleton

@Module
interface DomainBindsModule {
    @Binds
    @Singleton
    fun bindToken(token: Token) : AbstractToken
}