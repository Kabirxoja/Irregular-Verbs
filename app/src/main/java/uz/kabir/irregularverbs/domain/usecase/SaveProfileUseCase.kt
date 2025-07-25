package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.domain.model.Profile
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(profile: Profile){
        settingsRepository.setProfile(profile)
    }
}