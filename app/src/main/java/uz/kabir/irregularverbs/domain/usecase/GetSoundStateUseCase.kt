package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSoundStateUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> {
        return settingsRepository.getSoundState()
    }
}