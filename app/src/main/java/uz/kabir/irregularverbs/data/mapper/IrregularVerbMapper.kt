package uz.kabir.irregularverbs.data.mapper

import uz.kabir.irregularverbs.data.local.ProgressEntity
import uz.kabir.irregularverbs.data.local.VerbEntity
import uz.kabir.irregularverbs.data.remote.model.VerbDto
import uz.kabir.irregularverbs.domain.model.IrregularVerb
import uz.kabir.irregularverbs.data.remote.model.UserProfile
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.model.Profile

// Domain → Room Entity
fun IrregularVerb.toEntity(): VerbEntity {
    return VerbEntity(
        id=id,
        baseForm = baseForm,
        pastSimple = pastSimple,
        pastParticiple = pastParticiple,
        pastSimpleOption1 = pastSimpleOption1,
        pastSimpleOption2 = pastSimpleOption2,
        pastParticipleOption1 = pastParticipleOption1,
        pastParticipleOption2 = pastParticipleOption2,
        level = level,
        uzbekTranslation = uzbekTranslation,
        russianTranslation = russianTranslation,
        groupId = groupId,
        verb1 = verb1,
        verb1Uzbek = verb1Uzbek,
        verb1Russian = verb1Russian,
        verb2 = verb2,
        verb2Uzbek = verb2Uzbek,
        verb2Russian = verb2Russian,
        verb3 = verb3,
        verb3Uzbek = verb3Uzbek,
        verb3Russian = verb3Russian
    )
}

// Room Entity → Domain
fun VerbEntity.toDomain(): IrregularVerb {
    return IrregularVerb(
        id=id,
        baseForm = baseForm,
        pastSimple = pastSimple,
        pastParticiple = pastParticiple,
        pastSimpleOption1 = pastSimpleOption1,
        pastSimpleOption2 = pastSimpleOption2,
        pastParticipleOption1 = pastParticipleOption1,
        pastParticipleOption2 = pastParticipleOption2,
        level = level,
        uzbekTranslation = uzbekTranslation,
        russianTranslation = russianTranslation,
        groupId = groupId,
        verb1 = verb1,
        verb1Russian = verb1Russian,
        verb1Uzbek = verb1Uzbek,
        verb2 = verb2,
        verb2Russian = verb2Russian,
        verb2Uzbek = verb2Uzbek,
        verb3 = verb3,
        verb3Russian = verb3Russian,
        verb3Uzbek = verb3Uzbek
    )
}

// API model → Domain
fun VerbDto.toDomain(): IrregularVerb {
    return IrregularVerb(
        id,
        baseForm,
        pastSimple,
        pastParticiple,
        pastSimpleOption1,
        pastSimpleOption2,
        pastParticipleOption1,
        pastParticipleOption2,
        level,
        uzbekTranslation,
        russianTranslation,
        groupId,
        verb1,
        verb1Russian,
        verb1Uzbek,
        verb2,
        verb2Russian,
        verb2Uzbek,
        verb3,
        verb3Russian,
        verb3Uzbek
    )
}

// Domain → Entity
fun UserProgress.toEntity(): ProgressEntity {
    return ProgressEntity(
        groupId = groupId,
        testState = testState,
        optionTestStar = optionTestStar,
        listenTestStar = listenTestStar,
        writeTestStar = writeTestStar
    )
}

// Entity → Domain
fun ProgressEntity.toDomain(): UserProgress {
    return UserProgress(
        groupId = groupId,
        testState = testState,
        optionTestStar = optionTestStar,
        listenTestStar = listenTestStar,
        writeTestStar = writeTestStar
    )
}

// DataStore → Domain
fun UserProfile.toDomain(): Profile{
    return Profile(
        userName = userName,
        userGender = userGender
    )
}

// Domain → DataStore
fun Profile.toData(): UserProfile {
    return UserProfile(
        userName = userName,
        userGender = userGender
    )
}

