package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(private val settingsRepository: SettingsRepository)  {
    suspend operator fun invoke(languageCode: AppLanguage){
        settingsRepository.setLanguageCode(languageCode)
    }
}