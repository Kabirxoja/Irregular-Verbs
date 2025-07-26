package uz.kabir.irregularverbs.presentation.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.content.ContextCompat.startActivity

class ReportManager(private val context: Context) {
    fun sendBugReport(testNumber: Int, testVerb: String) {
        val subject = "Bug Report - Test №$testNumber ($testVerb)\nThanks for your help. Please clarify the mistake below."
        val body = ""

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kabirtechapps@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(intent)

    }
}
