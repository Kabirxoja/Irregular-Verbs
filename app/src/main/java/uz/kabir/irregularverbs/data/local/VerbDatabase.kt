package uz.kabir.irregularverbs.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.kabir.irregularverbs.data.local.VerbDao
import uz.kabir.irregularverbs.data.local.VerbEntity

@Database(entities = [VerbEntity::class, ProgressEntity::class], version = 1, exportSchema = false)
abstract class VerbDatabase : RoomDatabase() {
    abstract fun verbDao(): VerbDao
    abstract fun progressDao(): ProgressDao
}
