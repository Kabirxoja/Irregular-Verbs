package uz.kabir.irregularverbs.data.remote.model

import com.google.gson.annotations.SerializedName
import uz.kabir.irregularverbs.data.remote.model.VerbDto


data class VerbResponse(
    @SerializedName("update_version") val updateVersion: Int,
    val verbs: List<VerbDto>
)