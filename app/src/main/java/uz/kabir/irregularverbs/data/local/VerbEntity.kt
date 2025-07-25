package uz.kabir.irregularverbs.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "irregular_verbs")
data class VerbEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo("base_form") val baseForm: String,
    @ColumnInfo("past_simple") val pastSimple: String,
    @ColumnInfo("past_participle") val pastParticiple: String,
    @ColumnInfo("past_simple_option_1") val pastSimpleOption1: String,
    @ColumnInfo("past_simple_option_2") val pastSimpleOption2: String,
    @ColumnInfo("past_participle_option_1") val pastParticipleOption1: String,
    @ColumnInfo("past_participle_option_2") val pastParticipleOption2: String,
    @ColumnInfo("level") val level: String,
    @ColumnInfo("uzbek_translation") val uzbekTranslation: String,
    @ColumnInfo("russian_translation") val russianTranslation: String,
    @ColumnInfo("group_id") val groupId: Int,
    @ColumnInfo("verb1") val verb1: String,
    @ColumnInfo("verb1_uzbek") val verb1Uzbek: String,
    @ColumnInfo("verb1_russian") val verb1Russian: String,
    @ColumnInfo("verb2") val verb2: String,
    @ColumnInfo("verb2_uzbek") val verb2Uzbek: String,
    @ColumnInfo("verb2_russian") val verb2Russian: String,
    @ColumnInfo("verb3") val verb3: String,
    @ColumnInfo("verb3_uzbek") val verb3Uzbek: String,
    @ColumnInfo("verb3_russian") val verb3Russian: String
)