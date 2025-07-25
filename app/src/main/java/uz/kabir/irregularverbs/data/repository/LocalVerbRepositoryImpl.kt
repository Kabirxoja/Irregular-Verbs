package uz.kabir.irregularverbs.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.kabir.irregularverbs.data.local.ProgressDao
import uz.kabir.irregularverbs.data.local.ProgressEntity
import uz.kabir.irregularverbs.data.local.VerbDao
import uz.kabir.irregularverbs.data.local.VerbEntity
import uz.kabir.irregularverbs.data.mapper.toDomain
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import javax.inject.Inject
import uz.kabir.irregularverbs.data.mapper.toEntity

class LocalVerbRepositoryImpl @Inject constructor(
    private val verbDao: VerbDao,
    private val progressDao: ProgressDao
) : LocalVerbRepository {
    override suspend fun insertVerbs(verbs: List<VerbEntity>) {
        verbDao.insertVerbs(verbs)
    }

    override  fun getIrregularVerbs(): Flow<List<VerbEntity>> {
        return verbDao.getIrregularVerbs()
    }

    override suspend fun getVerbsByLevel(level: String): List<VerbEntity> {
        return verbDao.getVerbsByLevel(level)
    }

    override suspend fun getVerbsByGroupId(groupId: Int): List<VerbEntity> {
        return verbDao.getVerbsByGroupId(groupId)
    }

    override suspend fun getSearchVerbs(searchedWord: String): List<VerbEntity> {
        return verbDao.getSearchVerbs(searchedWord)
    }

    override suspend fun getTestAmount(): List<Int> {
        return verbDao.getTestAmount()
    }

    override suspend fun insertProgress(progress: List<UserProgress>) {
        progressDao.insertProgress(progress.map { it.toEntity() })
    }

    override fun getProgress(): Flow<List<UserProgress>> {
        return progressDao.getAllProgress().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun updateProgress(progress: UserProgress) {
        progressDao.updateProgress(progress.toEntity())
    }


}