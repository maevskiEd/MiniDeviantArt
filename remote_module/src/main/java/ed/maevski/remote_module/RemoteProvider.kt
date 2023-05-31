package ed.maevski.remote_module

interface RemoteProvider {
    fun provideRemote(): DeviantartApi
}