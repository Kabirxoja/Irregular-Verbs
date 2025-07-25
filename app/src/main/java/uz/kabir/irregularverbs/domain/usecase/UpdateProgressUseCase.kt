package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.data.local.ProgressEntity
import uz.kabir.irregularverbs.data.mapper.toEntity
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import javax.inject.Inject

class UpdateProgressUseCase @Inject constructor(private val localVerbRepository: LocalVerbRepository) {
    suspend operator fun invoke(progress: UserProgress) {
        localVerbRepository.updateProgress(progress)
    }
}