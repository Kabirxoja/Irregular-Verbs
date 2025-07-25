package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.domain.model.Profile
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Profile> {
        return settingsRepository.getProfile()
    }
}