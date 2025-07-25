package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import javax.inject.Inject

class GetTestAmountUseCase @Inject constructor(private val localVerbRepository: LocalVerbRepository) {
    suspend fun execute(): List<Int> {
        return localVerbRepository.getTestAmount()
    }
}