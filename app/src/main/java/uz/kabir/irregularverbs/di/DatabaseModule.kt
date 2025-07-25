package uz.kabir.irregularverbs.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.kabir.irregularverbs.data.local.ProgressDao
import uz.kabir.irregularverbs.data.local.VerbDao
import uz.kabir.irregularverbs.data.local.VerbDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
    object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): VerbDatabase {
        return Room.databaseBuilder(
            context,
            VerbDatabase::class.java,
            "irregular_verbs"
        ).build()
    }

    @Provides
    fun provideVerbDao(verbDatabase: VerbDatabase): VerbDao {
        return verbDatabase.verbDao()
    }

    @Provides
    fun provideProgressDao(verbDatabase: VerbDatabase): ProgressDao {
        return verbDatabase.progressDao()
    }

}