package com.newpaper.somewhere.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_BOTTOM_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_DRAWER_BAR_WIDTH
import com.newpaper.somewhere.core.designsystem.component.NAVIGATION_RAIL_BAR_WIDTH


//@Composable
//fun calculateWindowSizeClass(windowSize: DpSize): WindowSizeClass {
//    // Observe view configuration changes and recalculate the size class on each change. We can't
//    // use Activity#onConfigurationChanged as this will sometimes fail to be called on different
//    // API levels, hence why this function needs to be @Composable so we can observe the
//    // ComposeView's configuration changes.
//    return WindowSizeClass.calculateFromSize(windowSize)
//}

@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    // Observe view configuration changes and recalculate the size class on each change. We can't
    // use Activity#onConfigurationChanged as this will sometimes fail to be called on different
    // API levels, hence why this function needs to be @Composable so we can observe the
    // ComposeView's configuration changes.
    val configuration = LocalConfiguration.current
    val windowSizeDp = DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
    return WindowSizeClass.calculateFromSize(windowSizeDp)
}

@Immutable
class WindowSizeClass private constructor(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass,
    val spacerValue: Dp,
    val use2Panes: Boolean
) {
    companion object {
        /**
         * Calculates [WindowSizeClass] for a given [size]. Should be used for testing purposes only
         * - to calculate a [WindowSizeClass] for the Activity's current window see
         * [calculateWindowSizeClass].
         *
         * @param size of the window
         * @return [WindowSizeClass] corresponding to the given width and height
         */
        fun calculateFromSize(size: DpSize): WindowSizeClass {
            val windowWidthSizeClass = WindowWidthSizeClass.fromWidth(size.width)
            val windowHeightSizeClass = WindowHeightSizeClass.fromHeight(size.height)
            val spacerValue = if (windowWidthSizeClass == WindowWidthSizeClass.Compact) 16.dp else 24.dp
            val windowUse2Panes = WindowSizeClass.getUse2Panes(size.width, windowWidthSizeClass)
            return WindowSizeClass(windowWidthSizeClass, windowHeightSizeClass, spacerValue, windowUse2Panes)
        }

        private fun getUse2Panes(
            windowWidth: Dp,
            widthSizeClass: WindowWidthSizeClass
        ): Boolean{

            val navigationBarWidth = when (widthSizeClass){
                WindowWidthSizeClass.Medium -> NAVIGATION_RAIL_BAR_WIDTH
                WindowWidthSizeClass.Expanded -> NAVIGATION_DRAWER_BAR_WIDTH
                else -> NAVIGATION_BOTTOM_BAR_WIDTH
            }

            return windowWidth - navigationBarWidth > 760.dp
        }
    }

    override fun hashCode(): Int {
        var result = widthSizeClass.hashCode()
        result = 31 * result + heightSizeClass.hashCode()
        return result
    }

    override fun toString() = "WindowSizeClass($widthSizeClass, $heightSizeClass)"


}

/**
 * Width-based window size class.
 *
 * A window size class represents a breakpoint that can be used to build responsive layouts. Each
 * window size class breakpoint represents a majority case for typical device scenarios so your
 * layouts will work well on most devices and configurations.
 *
 * For more details see <a href="https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes" class="external" target="_blank">Window size classes documentation</a>.
 */
@Immutable
@kotlin.jvm.JvmInline
value class WindowWidthSizeClass private constructor(private val value: Int) :
    Comparable<WindowWidthSizeClass> {

    override operator fun compareTo(other: WindowWidthSizeClass) = value.compareTo(other.value)

    override fun toString(): String {
        return "WindowWidthSizeClass." + when (this) {
            Compact -> "Compact"
            Medium -> "Medium"
            Expanded -> "Expanded"
            else -> ""
        }
    }

    companion object {
        /** Represents the majority of phones in portrait. */
        val Compact = WindowWidthSizeClass(0)

        /**
         * Represents the majority of tablets in portrait and large unfolded inner displays in
         * portrait.
         */
        val Medium = WindowWidthSizeClass(1)

        /**
         * Represents the majority of tablets in landscape and large unfolded inner displays in
         * landscape.
         */
        val Expanded = WindowWidthSizeClass(2)

        /** Calculates the [WindowWidthSizeClass] for a given [width] */
        internal fun fromWidth(width: Dp): WindowWidthSizeClass {
            require(width >= 0.dp) { "Width must not be negative" }
            return when {
                width < 600.dp -> Compact
                width < 940.dp -> Medium
                else -> Expanded
            }
        }
    }
}

/**
 * Height-based window size class.
 *
 * A window size class represents a breakpoint that can be used to build responsive layouts. Each
 * window size class breakpoint represents a majority case for typical device scenarios so your
 * layouts will work well on most devices and configurations.
 *
 * For more details see <a href="https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes" class="external" target="_blank">Window size classes documentation</a>.
 */
@Immutable
@kotlin.jvm.JvmInline
value class WindowHeightSizeClass private constructor(private val value: Int) :
    Comparable<WindowHeightSizeClass> {

    override operator fun compareTo(other: WindowHeightSizeClass) = value.compareTo(other.value)

    override fun toString(): String {
        return "WindowHeightSizeClass." + when (this) {
            Compact -> "Compact"
            Medium -> "Medium"
            Expanded -> "Expanded"
            else -> ""
        }
    }

    companion object {
        /** Represents the majority of phones in landscape */
        val Compact = WindowHeightSizeClass(0)

        /** Represents the majority of tablets in landscape and majority of phones in portrait */
        val Medium = WindowHeightSizeClass(1)

        /** Represents the majority of tablets in portrait */
        val Expanded = WindowHeightSizeClass(2)

        /** Calculates the [WindowHeightSizeClass] for a given [height] */
        internal fun fromHeight(height: Dp): WindowHeightSizeClass {
            require(height >= 0.dp) { "Height must not be negative" }
            return when {
                height < 480.dp -> Compact
                height < 900.dp -> Medium
                else -> Expanded
            }
        }
    }
}