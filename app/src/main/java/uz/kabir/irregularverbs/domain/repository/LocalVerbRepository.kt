package uz.kabir.irregularverbs.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.kabir.irregularverbs.data.local.ProgressEntity
import uz.kabir.irregularverbs.data.local.VerbEntity
import uz.kabir.irregularverbs.domain.model.UserProgress

interface LocalVerbRepository {
    suspend fun insertVerbs(verbs: List<VerbEntity>)
    fun getIrregularVerbs(): Flow<List<VerbEntity>>
    suspend fun getVerbsByLevel(level:String): List<VerbEntity>
    suspend fun getVerbsByGroupId(groupId:Int):List<VerbEntity>
    suspend fun getSearchVerbs(searchedWord:String): List<VerbEntity>
    suspend fun getTestAmount(): List<Int>
    suspend fun insertProgress(progress: List<UserProgress>)
    fun getProgress():Flow<List<UserProgress>>
    suspend fun updateProgress(progress: UserProgress)
}