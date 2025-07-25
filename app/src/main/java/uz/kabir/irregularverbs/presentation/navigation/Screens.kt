package uz.kabir.irregularverbs.presentation.navigation

sealed class Screens(val route: String) {

    object Home : Screens("home")
    object Settings : Screens("settings")
    object Search : Screens("search")
    object Learn : Screens("learn")

    data class QuizGraph(val groupId: Int) : Screens("quiz_graph/$groupId") {
        companion object {
            const val routeWithArg = "quiz_graph/{groupId}"
        }
    }

    object Option : Screens("quiz_graph/{groupId}/option") {
        fun passGroupId(groupId: Int) = "quiz_graph/$groupId/option"
    }

    data class OptionResult(val groupId: Int) : Screens("quiz_graph/$groupId/option_result") {
        companion object {
            const val routeWithArg = "quiz_graph/{groupId}/option_result"
            fun passGroupId(groupId: Int) = "quiz_graph/$groupId/option_result"
        }
    }

    data class ListenGraph(val groupId:Int): Screens("listen_graph/$groupId"){
        companion object{
            const val routeWithArg = "listen_graph/{groupId}"
        }
    }

    object Listen : Screens("listen_graph/{groupId}/listen"){
        fun passGroupId(groupId: Int) = "listen_graph/$groupId/listen"
    }

    data class ListenResult(val groupId: Int) : Screens("listen_graph/$groupId/listen_result"){
        companion object{
            const val routeWithArg = "listen_graph/{groupId}/listen_result"
            fun passGroupId(groupId: Int) = "listen_graph/$groupId/listen_result"
        }
    }

    data class WriteGraph(val groupId:Int): Screens("write_graph/$groupId"){
        companion object{
            const val routeWithArg = "write_graph/{groupId}"
        }
    }

    object Write: Screens("write_graph/{groupId}/write"){
        fun passGroupId(groupId: Int) = "write_graph/$groupId/write"
    }

    data class WriteResult(val groupId: Int) : Screens("write_graph/$groupId/write_result"){
        companion object{
            const val routeWithArg = "write_graph/{groupId}/write_result"
            fun passGroupId(groupId: Int) = "write_graph/$groupId/write_result"
        }
    }

}
