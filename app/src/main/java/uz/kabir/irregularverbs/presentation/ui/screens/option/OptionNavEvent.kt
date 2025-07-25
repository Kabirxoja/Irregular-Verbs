package uz.kabir.irregularverbs.presentation.ui.screens.option

sealed interface OptionNavEvent {
    data class NavigateToOptionResult(val groupId: Int) : OptionNavEvent
    object NavigateToHome : OptionNavEvent
}