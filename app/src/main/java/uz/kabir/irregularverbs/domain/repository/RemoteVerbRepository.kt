package uz.kabir.irregularverbs.domain.repository

import uz.kabir.irregularverbs.domain.model.IrregularVerbCore

interface RemoteVerbRepository {
    suspend fun getIrregularVerbs(): IrregularVerbCore
}
