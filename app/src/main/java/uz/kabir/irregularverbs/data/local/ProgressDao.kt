package uz.kabir.irregularverbs.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProgress(progress: List<ProgressEntity>)

    @Query("SELECT * FROM test_progress ORDER BY groupId ASC")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Update
    suspend fun updateProgress(progress: ProgressEntity)
}