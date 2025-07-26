package uz.kabir.irregularverbs.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.presentation.navigation.RootNavGraph
import uz.kabir.irregularverbs.presentation.ui.theme.IrregularVerbsTheme
import uz.kabir.irregularverbs.presentation.MainActivityViewModel
import uz.kabir.irregularverbs.presentation.ui.screens.setting.SettingsViewModel
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModelSetting: SettingsViewModel by viewModels()
    private val viewModelActivity: MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModelActivity.syncProgress()
        }

        setContent {
            val theme by viewModelActivity.theme.collectAsState()
            val languageCode by viewModelSetting.languageCode.collectAsState()

//            LaunchedEffect(languageCode) {
//                LanguageApplier.applyLanguage(this@MainActivity, languageCode)
//            }

            IrregularVerbsTheme(themeMode = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}


@Composable
fun MainApp() {
    val navController = rememberNavController()
    RootNavGraph(navHostController = navController)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainApp()
}


