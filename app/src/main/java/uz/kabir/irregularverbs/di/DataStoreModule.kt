package uz.kabir.irregularverbs.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.kabir.irregularverbs.data.datastore.SettingParameterDataStore
import uz.kabir.irregularverbs.data.datastore.UpdateVersionDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("settings")
        }
    }

    @Provides
    @Singleton
    fun provideSettingParameterDataStore(
        dataStore: DataStore<Preferences>
    ): SettingParameterDataStore {
        return SettingParameterDataStore(dataStore)
    }

    @Provides
    @Singleton
    fun provideUpdateVersionDataStore(
        dataStore: DataStore<Preferences>
    ): UpdateVersionDataStore {
        return UpdateVersionDataStore(dataStore)
    }
}