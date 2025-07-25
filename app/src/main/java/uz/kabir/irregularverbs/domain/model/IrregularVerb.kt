package uz.kabir.irregularverbs.domain.model

data class IrregularVerb(
    val id: Int,
    val baseForm: String,
    val pastSimple: String,
    val pastParticiple: String,
    val pastSimpleOption1: String,
    val pastSimpleOption2: String,
    val pastParticipleOption1: String,
    val pastParticipleOption2: String,
    val level: String,
    val uzbekTranslation: String,
    val russianTranslation: String,
    val groupId: Int,
    val verb1: String,
    val verb1Russian: String,
    val verb1Uzbek: String,
    val verb2: String,
    val verb2Russian: String,
    val verb2Uzbek: String,
    val verb3: String,
    val verb3Russian: String,
    val verb3Uzbek: String
)


