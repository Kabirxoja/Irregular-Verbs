package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(private val settingRepository: SettingsRepository)  {
    suspend operator fun invoke(themeMode: ThemeMode){
        settingRepository.setThemeMode(themeMode)
    }
}