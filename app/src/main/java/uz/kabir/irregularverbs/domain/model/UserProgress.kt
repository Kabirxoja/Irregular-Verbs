package uz.kabir.irregularverbs.domain.model

data class UserProgress(
    val groupId: Int,
    val testState: Int,
    val optionTestStar: Boolean,
    val listenTestStar: Boolean,
    val writeTestStar: Boolean
)