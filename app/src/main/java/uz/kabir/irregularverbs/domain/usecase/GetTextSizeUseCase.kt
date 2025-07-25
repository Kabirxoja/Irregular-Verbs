package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.first
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import javax.inject.Inject

class GetTextSizeUseCase @Inject constructor(private val settingRepository: SettingsRepository)  {
    suspend operator fun invoke(): TextMode {
        return settingRepository.getTextSize().first()
    }
}