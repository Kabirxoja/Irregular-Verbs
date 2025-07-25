package uz.kabir.irregularverbs.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.logging.Level

@Dao
interface
VerbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerbs(verbs: List<VerbEntity>)

    @Query("SELECT * FROM irregular_verbs")
    fun getIrregularVerbs(): Flow<List<VerbEntity>>

    @Query("DELETE FROM irregular_verbs")
    suspend fun deleteAllVerbs()

    @Query("SELECT * FROM irregular_verbs WHERE level = :level")
    suspend fun getVerbsByLevel(level: String): List<VerbEntity>

    @Query("SELECT * FROM irregular_verbs WHERE group_id = :groupId")
    suspend fun getVerbsByGroupId(groupId: Int): List<VerbEntity>

    @Query("SELECT * FROM irregular_verbs WHERE base_form LIKE '%' || :query || '%' OR past_simple LIKE '%' || :query || '%' OR past_participle LIKE '%' || :query || '%'")
    suspend fun getSearchVerbs(query: String): List<VerbEntity>

    @Query("SELECT DISTINCT `group_id` FROM irregular_verbs ORDER BY `group_id` ASC")
    suspend fun getTestAmount(): List<Int>
}