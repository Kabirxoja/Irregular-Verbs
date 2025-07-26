package uz.kabir.irregularverbs.presentation.ui.utils

import android.content.Context
import android.media.MediaPlayer
import uz.kabir.irregularverbs.R


class SoundManager(private val context: Context) {
    fun playClickSound() {
        val player = MediaPlayer.create(context, R.raw.click_sound)
        player.setOnCompletionListener {
            it.release()
        }
        player.start()
    }
}