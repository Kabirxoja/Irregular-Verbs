package uz.kabir.irregularverbs.data.repository

import uz.kabir.irregularverbs.data.mapper.toDomain
import uz.kabir.irregularverbs.data.remote.service.VerbApiService
import uz.kabir.irregularverbs.domain.model.IrregularVerbCore
import uz.kabir.irregularverbs.domain.repository.RemoteVerbRepository
import javax.inject.Inject

class RemoteVerbRepositoryImpl @Inject constructor(
    private val apiService: VerbApiService
) : RemoteVerbRepository {

    override suspend fun getIrregularVerbs(): IrregularVerbCore {
        val response = apiService.getIrregularVerbs()

        val domainList = response.verbs.map { it.toDomain() }
        val updateVersion = response.updateVersion

        return IrregularVerbCore(updateVersion = updateVersion, verbs = domainList)
    }
}