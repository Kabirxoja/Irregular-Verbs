package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveSoundStateUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(isEnabled: Boolean) {
        settingsRepository.setSoundState(isEnabled)
    }
}