package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveUpdateVersionUseCase @Inject constructor(private val settingRepository: SettingsRepository)  {
    suspend operator fun invoke(version: Int) {
        settingRepository.setUpdateVersion(version)
    }
}