package uz.kabir.irregularverbs.presentation.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri

object IntentHelper {
    fun reportBug(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("*******@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Bug Report from ListenAp")
            putExtra(Intent.EXTRA_TEXT, "Please describe your problem in detail.")
        }
        startActivity(context, intent, null)
    }
}