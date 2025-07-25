package uz.kabir.irregularverbs.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_progress")
data class ProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "groupId") val groupId: Int,
    @ColumnInfo(name = "test_state") val testState: Int,
    @ColumnInfo(name = "option_test_star") val optionTestStar: Boolean,
    @ColumnInfo(name = "listen_test_star") val listenTestStar: Boolean,
    @ColumnInfo(name = "write_tes_star") val writeTestStar: Boolean
)