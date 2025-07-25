package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import uz.kabir.irregularverbs.data.mapper.toDomain
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import javax.inject.Inject

class GetProgressUseCase @Inject constructor(private val localVerbRepository: LocalVerbRepository) {
    fun getProgressFlow(): Flow<List<UserProgress>> {
        return localVerbRepository.getProgress()
    }
}

