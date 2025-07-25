package uz.kabir.irregularverbs.presentation.ui.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class TTSManager @Inject constructor(@ApplicationContext private val context: Context) {
    private var tts: TextToSpeech? = null
    fun init(onReady: () -> Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val voices = tts?.voices
                val bestVoice = voices
                    ?.filter { it.locale == Locale.US && !it.isNetworkConnectionRequired }
                    ?.firstOrNull { it.quality == Voice.QUALITY_HIGH }

                val goodVoice = voices
                    ?.filter { it.locale == Locale.US && !it.isNetworkConnectionRequired }
                    ?.firstOrNull { it.quality == Voice.QUALITY_NORMAL }

                val defaultVoice = voices?.firstOrNull { it.locale.language == "en" }
                val checkVoice= bestVoice ?: goodVoice ?: defaultVoice
                checkVoice?.let {
                    tts?.voice = it
                }
                tts?.language = Locale.US
                onReady()
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
