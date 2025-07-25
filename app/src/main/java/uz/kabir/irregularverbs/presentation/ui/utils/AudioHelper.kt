package uz.kabir.irregularverbs.presentation.ui.utils

import android.content.Context
import android.media.MediaPlayer
import uz.kabir.irregularverbs.R


object AudioHelper {
    fun playClick(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.click_sound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
}