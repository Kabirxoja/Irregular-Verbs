package uz.kabir.irregularverbs.presentation.ui.state

import java.util.Locale

enum class AppLanguage(val locale: Locale?) {
    UZBEK(Locale("uz")),
    RUSSIAN(Locale("ru")),
    ENGLISH(Locale("en")),
    AUTO(null)  //Locale.getDefault()
}