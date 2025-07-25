package uz.kabir.irregularverbs.data.remote.model

import com.google.gson.annotations.SerializedName

data class VerbDto(
    val id: Int,
    @SerializedName("base_form") val baseForm: String,
    @SerializedName("past_simple") val pastSimple: String,
    @SerializedName("past_participle") val pastParticiple: String,
    @SerializedName("past_simple_option_1") val pastSimpleOption1: String,
    @SerializedName("past_simple_option_2") val pastSimpleOption2: String,
    @SerializedName("past_participle_option_1") val pastParticipleOption1: String,
    @SerializedName("past_participle_option_2") val pastParticipleOption2: String,
    val level: String,
    @SerializedName("uzbek_translation") val uzbekTranslation: String,
    @SerializedName("russian_translation") val russianTranslation: String,
    @SerializedName("group_id") val groupId: Int,
    val verb1: String,
    @SerializedName("verb1_russian") val verb1Russian: String,
    @SerializedName("verb1_uzbek") val verb1Uzbek: String,
    val verb2: String,
    @SerializedName("verb2_russian") val verb2Russian: String,
    @SerializedName("verb2_uzbek") val verb2Uzbek: String,
    val verb3: String,
    @SerializedName("verb3_russian") val verb3Russian: String,
    @SerializedName("verb3_uzbek") val verb3Uzbek: String
)
