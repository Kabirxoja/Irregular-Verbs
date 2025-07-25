package uz.kabir.irregularverbs.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.kabir.irregularverbs.data.repository.LocalVerbRepositoryImpl
import uz.kabir.irregularverbs.data.repository.RemoteVerbRepositoryImpl
import uz.kabir.irregularverbs.data.repository.SettingsRepositoryImpl
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import uz.kabir.irregularverbs.domain.repository.RemoteVerbRepository
import uz.kabir.irregularverbs.domain.repository.SettingsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindLocalVerbRepository(impl: LocalVerbRepositoryImpl): LocalVerbRepository

    @Binds
    abstract fun bindRemoteVerbRepository(impl: RemoteVerbRepositoryImpl): RemoteVerbRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

}