package uz.kabir.irregularverbs.domain.usecase

import kotlinx.coroutines.flow.first
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import javax.inject.Inject
import kotlin.collections.map

class GetVerbsUseCase @Inject constructor(
    private val localVerbRepository: LocalVerbRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): List<IrregularVerbTranslated> {
        val verbs = localVerbRepository.getIrregularVerbs().first()
        val languageCode = settingsRepository.getLanguageCode().first()


        return verbs.map { verb ->
            IrregularVerbTranslated(
                baseForm = verb.baseForm,
                pastSimple = verb.pastSimple,
                pastParticiple = verb.pastParticiple,
                pastSimpleOption1 = verb.pastSimpleOption1,
                pastSimpleOption2 = verb.pastSimpleOption2,
                pastParticipleOption1 = verb.pastParticipleOption1,
                pastParticipleOption2 = verb.pastParticipleOption2,
                level = verb.level,
                groupId = verb.groupId,
                translation = when (languageCode) {
                    AppLanguage.UZBEK -> verb.uzbekTranslation
                    AppLanguage.RUSSIAN -> verb.russianTranslation
                    AppLanguage.ENGLISH -> ""
                    AppLanguage.AUTO -> ""
                    else -> ""
                },
                verb1 = verb.verb1,
                verb1Translation = when (languageCode) {
                    AppLanguage.UZBEK -> verb.verb1Uzbek
                    AppLanguage.RUSSIAN -> verb.verb1Russian
                    AppLanguage.ENGLISH -> ""
                    AppLanguage.AUTO -> ""
                    else -> ""
                },
                verb2 = verb.verb2,
                verb2Translation = when (languageCode) {
                    AppLanguage.UZBEK -> verb.verb2Uzbek
                    AppLanguage.RUSSIAN -> verb.verb2Russian
                    AppLanguage.ENGLISH -> ""
                    AppLanguage.AUTO -> ""
                    else -> ""
                },
                verb3 = verb.verb3,
                verb3Translation = when (languageCode) {
                    AppLanguage.UZBEK -> verb.verb3Uzbek
                    AppLanguage.RUSSIAN -> verb.verb3Russian
                    AppLanguage.ENGLISH -> ""
                    AppLanguage.AUTO -> ""
                    else -> ""
                }
            )
        }
    }
}