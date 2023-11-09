package com.newpaper.somewhere.ui.navigation


//main navigation destination / use at navigation bar
interface MainNavigationDestination {
    val route: String
    var title: String
}

object MyTripsMainDestination : MainNavigationDestination {
    override val route = "main_myTrips"
    override var title = ""
}

object MoreMainDestination : MainNavigationDestination {
    override val route = "main_more"
    override var title = ""
}






//navigation destination / use at screen
interface ScreenDestination {
    val route: String
    var title: String
}

object MyTripsScreenDestination : ScreenDestination {
    override val route = "myTrips"
    override var title = ""
}

object TripMapScreenDestination : ScreenDestination {
    override val route = "tripMap"
    override var title = ""
}

object TripScreenDestination : ScreenDestination {
    override val route = "trip"
    override var title = "Trip screen"
    const val tripIdArg = "tripId"
    val routeWithArgs = "$route/{$tripIdArg}"
}

object DateScreenDestination : ScreenDestination {
    override val route = "date"
    override var title = "Date screen"
    const val dateIdArg = "dateId"
    val routeWithArgs = "$route/{$dateIdArg}"
}

object SpotScreenDestination : ScreenDestination {
    override val route = "spot"
    override var title = ""
}

object ImageScreenDestination : ScreenDestination {
    override val route = "image"
    override var title = ""
}


object MoreScreenDestination: ScreenDestination {
    override val route = "more"
    override var title = ""
}
object SetDateTimeFormatScreenDestination: ScreenDestination {
    override val route = "setDateTimeFormat"
    override var title = ""
}

object SetThemeScreenDestination: ScreenDestination {
    override val route = "setTheme"
    override var title = ""
}

object AboutScreenDestination: ScreenDestination {
    override val route = "about"
    override var title = ""
}
