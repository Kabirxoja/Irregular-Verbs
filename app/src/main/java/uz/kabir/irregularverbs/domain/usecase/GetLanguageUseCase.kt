package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.first
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(private val settingRepository: SettingsRepository) {
    suspend operator fun invoke(): AppLanguage {
        return settingRepository.getLanguageCode().first()
    }
}