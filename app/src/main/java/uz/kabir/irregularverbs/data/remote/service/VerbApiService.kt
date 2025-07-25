package uz.kabir.irregularverbs.data.remote.service

import retrofit2.http.GET
import uz.kabir.irregularverbs.data.remote.model.VerbResponse

interface VerbApiService {
    @GET("irregular_verbs.json")
    suspend fun getIrregularVerbs(): VerbResponse
}