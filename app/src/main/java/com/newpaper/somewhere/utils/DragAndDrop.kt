package com.newpaper.somewhere.utils


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import kotlin.math.roundToInt
import kotlin.math.sign



enum class SlideState {
    NONE,
    UP,     //== left
    DOWN    //== right
}

fun <T> Modifier.dragAndDropVertical(
    item: T,
    items: List<T>,
    itemHeight: Int,
    updateSlideState: (itemIdx: Int, slideState: SlideState) -> Unit,
    offsetY: Animatable<Float, AnimationVector1D>,
    onStartDrag: () -> Unit,
    onStopDrag: (currentIndex: Int, destinationIndex: Int) -> Unit,

    isDraggedAfterLongPress: Boolean = false,
): Modifier = composed {

    val haptic = LocalHapticFeedback.current

    pointerInput(Unit) {
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            val itemIdx = items.indexOf(item)
            val offsetToSlide = itemHeight / 4
            var numberOfItems = 0
            var previousNumberOfItems: Int
            var listOffset = 0

            //on drag start
            val onDragStart = {
                //haptic
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                // Interrupt any ongoing animation of other items.
                launch {
                    offsetY.stop()
                }
                onStartDrag()
            }

            //on drag
            val onDrag = {change: PointerInputChange ->

                val verticalDragOffset = offsetY.value + change.positionChange().y

                launch {
                    offsetY.snapTo(verticalDragOffset)

                    // -1, 0, 1
                    val offsetSign = offsetY.value.sign.toInt()

                    previousNumberOfItems = numberOfItems

                    //passed item count
                    numberOfItems = calculateNumberOfSlideItems(
                        offsetY.value * offsetSign,
                        itemHeight,
                        offsetToSlide,
                        previousNumberOfItems
                    )

                    if (previousNumberOfItems > numberOfItems) {
                        var idx = itemIdx + previousNumberOfItems * offsetSign
                        if (idx > items.size - 1)    idx = items.size - 1
                        else if (idx < 0)           idx = 0

                        updateSlideState(idx, SlideState.NONE)
                    } else if (numberOfItems != 0) {

                        try {
                            updateSlideState(
                                itemIdx + numberOfItems * offsetSign,
                                if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                            )
                        } catch (e: IndexOutOfBoundsException) {
                            numberOfItems = previousNumberOfItems
                        }
                    }
                    listOffset = numberOfItems * offsetSign
                }
                // Consume the gesture event, not passed to external
                change.consume()
            }

            //on drag end
            val onDragEnd = {
                launch {
                    offsetY.animateTo(itemHeight * numberOfItems * offsetY.value.sign)
                    onStopDrag(itemIdx, itemIdx + listOffset)
                }
            }


            if (isDraggedAfterLongPress)
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart()
                    },
                    onDrag = { change, _ -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                )
            else
                detectDragGestures(
                    onDragStart = {
                        onDragStart()

                    },
                    onDrag = { change, offset -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                )
        }
    }
}

fun <T> Modifier.dragAndDropHorizontal(
    item: T,
    items: List<T>,
    itemWidth: Int,
    updateSlideState: (itemIdx: Int, slideState: SlideState) -> Unit,
    onStartDrag: () -> Unit,
    onStopDrag: (currentIndex: Int, destinationIndex: Int) -> Unit,

    isDraggedAfterLongPress: Boolean = false,
): Modifier = composed {

    val haptic = LocalHapticFeedback.current
    val offsetX = remember { Animatable(0f) }

    pointerInput(Unit) {
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            val itemIdx = items.indexOf(item)
            val offsetToSlide = itemWidth / 4
            var numberOfItems = 0
            var previousNumberOfItems: Int
            var listOffset = 0

            //on drag start
            val onDragStart = {
                //haptic
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                // Interrupt any ongoing animation of other items.
                launch {
                    offsetX.stop()
                }
                onStartDrag()
            }

            //on drag
            val onDrag = {change: PointerInputChange ->
                val horizontalDragOffset = offsetX.value + change.positionChange().x

                launch {
                    offsetX.snapTo(horizontalDragOffset)

                    // -1, 0, 1
                    val offsetSign = offsetX.value.sign.toInt()

                    previousNumberOfItems = numberOfItems

                    //passed item count
                    numberOfItems = calculateNumberOfSlideItems(
                        offsetX.value * offsetSign,
                        itemWidth,
                        offsetToSlide,
                        previousNumberOfItems
                    )

                    if (previousNumberOfItems > numberOfItems) {
                        var idx = itemIdx + previousNumberOfItems * offsetSign
                        if (idx > items.size - 1)    idx = items.size - 1
                        else if (idx < 0)           idx = 0

                        updateSlideState(idx, SlideState.NONE)
                    } else if (numberOfItems != 0) {

                        try {
                            updateSlideState(
                                itemIdx + numberOfItems * offsetSign,
                                if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                            )
                        } catch (e: IndexOutOfBoundsException) {
                            numberOfItems = previousNumberOfItems
                        }
                    }
                    listOffset = numberOfItems * offsetSign
                }
                // Consume the gesture event, not passed to external
                change.consume()
            }

            //on drag end
            val onDragEnd = {
                launch {
                    offsetX.animateTo(itemWidth * numberOfItems * offsetX.value.sign)
                    onStopDrag(itemIdx, itemIdx + listOffset)
                }
            }


            if (isDraggedAfterLongPress)
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart()
                    },
                    onDrag = { change, _ -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                )
            else
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                    },
                    onDrag = { change, _ -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                )
        }
    }
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
}


fun calculateNumberOfSlideItems(
    offsetY: Float,
    itemHeight: Int,
    offsetToSlide: Int,
    previousNumberOfItems: Int
): Int {
    val numberOfItemsInOffset = (offsetY / itemHeight).toInt()
    val numberOfItemsPlusOffset = ((offsetY + offsetToSlide) / itemHeight).toInt()
    val numberOfItemsMinusOffset = ((offsetY - offsetToSlide - 1) / itemHeight).toInt()
    return when {
        offsetY - offsetToSlide - 1 < 0                     -> 0
        numberOfItemsPlusOffset > numberOfItemsInOffset     -> numberOfItemsPlusOffset
        numberOfItemsMinusOffset < numberOfItemsInOffset    -> numberOfItemsInOffset
        else                                                -> previousNumberOfItems
    }
}


















