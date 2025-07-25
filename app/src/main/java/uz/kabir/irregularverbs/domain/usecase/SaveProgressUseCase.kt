package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.data.local.ProgressEntity
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import javax.inject.Inject

class SaveProgressUseCase @Inject constructor(private val localVerbRepository: LocalVerbRepository) {
    suspend operator fun invoke(progress:List<UserProgress>){
        localVerbRepository.insertProgress(progress)
    }
}