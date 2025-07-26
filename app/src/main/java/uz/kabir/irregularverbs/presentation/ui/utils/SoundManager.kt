package uz.kabir.irregularverbs.presentation.ui.utils

import android.content.Context
import android.media.MediaPlayer
import uz.kabir.irregularverbs.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SoundManager(private val context: Context) {
    fun playClickSound() {
        val player = MediaPlayer.create(context, R.raw.click_sound)
        player.setOnCompletionListener {
            it.release()
        }
        player.start()
    }
}