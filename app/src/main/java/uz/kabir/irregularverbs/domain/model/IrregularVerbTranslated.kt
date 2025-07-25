package uz.kabir.irregularverbs.domain.model

data class IrregularVerbTranslated(
    val baseForm: String,
    val pastSimple: String,
    val pastParticiple: String,
    val pastSimpleOption1: String,
    val pastSimpleOption2: String,
    val pastParticipleOption1: String,
    val pastParticipleOption2: String,
    val level: String,
    val groupId: Int,
    val translation: String,
    val verb1: String,
    val verb1Translation: String,
    val verb2: String,
    val verb2Translation: String,
    val verb3: String,
    val verb3Translation: String
)