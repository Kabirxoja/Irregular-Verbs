package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<ThemeMode> {
        return settingsRepository.getThemeMode()
    }

    // Settings ekrani uchun oddiy suspend
    suspend fun getOnce(): ThemeMode {
        return settingsRepository.getThemeMode().first()
    }
}