package uz.kabir.irregularverbs.domain.usecase

import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import javax.inject.Inject

class SaveTextSizeUseCase @Inject constructor(private val settingRepository: SettingsRepository) {
    suspend operator fun invoke(textMode: TextMode) {
        settingRepository.setTextSize(textMode)
    }
}