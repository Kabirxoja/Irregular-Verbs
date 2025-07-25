package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.first
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import javax.inject.Inject

class GetUpdateVersionUseCase @Inject constructor(private val settingRepository: SettingsRepository) {
    suspend operator fun invoke(): Int {
        return settingRepository.getUpdateVersion().first()
    }
}