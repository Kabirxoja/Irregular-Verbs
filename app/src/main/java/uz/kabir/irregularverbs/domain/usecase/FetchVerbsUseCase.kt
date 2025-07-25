package uz.kabir.irregularverbs.domain.usecase

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import uz.kabir.irregularverbs.data.mapper.toEntity
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.repository.LocalVerbRepository
import uz.kabir.irregularverbs.domain.repository.RemoteVerbRepository
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import javax.inject.Inject

class FetchVerbsUseCase @Inject constructor(
    private val remoteVerbRepository: RemoteVerbRepository,
    private val localVerbRepository: LocalVerbRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        try {
            val data = remoteVerbRepository.getIrregularVerbs()
            val remoteVerbs = data.verbs
            val remoteVersion = data.updateVersion
            val localVersion = settingsRepository.getUpdateVersion().first()

            Log.d("FetchVerbsUseCase", "remoteVersion: $remoteVersion" + "localVersion: $localVersion")

            val remoteGroupIdsList = remoteVerbs.map { it.groupId }.distinct().sorted()
            val localGroupIdsList = localVerbRepository.getTestAmount()

            if (remoteVersion != localVersion) {
                val entities = remoteVerbs.map { it.toEntity() }
                localVerbRepository.insertVerbs(entities)
                settingsRepository.setUpdateVersion(remoteVersion)

                val currentProgress = localVerbRepository.getProgress().first()
                if (currentProgress.isEmpty()) {
                    Log.d("FetchVerbsUseCase", "currentProgress: $currentProgress")
                    val getVerbs = localVerbRepository.getIrregularVerbs().first()

                    val newProgressItems = getVerbs.mapIndexed { index, entities ->
                        UserProgress(
                            groupId = entities.groupId,
                            testState = if (entities.groupId == 1) 0 else -1,
                            optionTestStar = false,
                            listenTestStar = false,
                            writeTestStar = false
                        )
                    }
                    localVerbRepository.insertProgress(newProgressItems)
                }
            }


        } catch (e: Exception) {
            Log.d("FetchVerbsUseCase", "Error: ${e.message}")
        }
    }
}
