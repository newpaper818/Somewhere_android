package com.example.somewhere.utils


import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import kotlin.math.sign



enum class SlideState {
    NONE,
    UP,
    DOWN
}

fun <T> Modifier.dragAndDrop(
    item: T,
    items: MutableList<T>,
    itemHeight: Int,
    updateSlideState: (item: T, slideState: SlideState) -> Unit,
    isDraggedAfterLongPress: Boolean,
    setOffsetY: (offsetY: Float) -> Unit,
    onStartDrag: () -> Unit,
    onStopDrag: (currentIndex: Int, destinationIndex: Int) -> Unit,
): Modifier = composed {

    val offsetY = remember { Animatable(0f) }

    val haptic = LocalHapticFeedback.current
    //val context = LocalContext.current

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
                    setOffsetY(offsetY.value)
                }
                onStartDrag()
            }

            //on drag
            val onDrag = {change: PointerInputChange ->
                val verticalDragOffset = offsetY.value + change.positionChange().y

                launch {
                    offsetY.snapTo(verticalDragOffset)

                    setOffsetY(offsetY.value)

                    val offsetSign = offsetY.value.sign.toInt()
                    previousNumberOfItems = numberOfItems
                    numberOfItems = calculateNumberOfSlideItems(
                        offsetY.value * offsetSign,
                        itemHeight,
                        offsetToSlide,
                        previousNumberOfItems
                    )

                    if (previousNumberOfItems > numberOfItems) {
                        updateSlideState(
                            items[itemIdx + previousNumberOfItems * offsetSign],
                            SlideState.NONE
                        )
                    } else if (numberOfItems != 0) {
                        try {
                            updateSlideState(
                                items[itemIdx + numberOfItems * offsetSign],
                                if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                            )
                        } catch (e: IndexOutOfBoundsException) {
                            numberOfItems = previousNumberOfItems
                            Log.i("DragToReorder", "Item is outside or at the edge")
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
                    setOffsetY(offsetY.value)
                    onStopDrag(itemIdx, itemIdx + listOffset)
                }
            }


//            if (isDraggedAfterLongPress)
//                detectDragGesturesAfterLongPress(
//                    onDragStart = {
//                        //haptic feedback?
//
//                        onDragStart()
//                    },
//                    onDrag = { change, _ -> onDrag(change) },
//                    onDragEnd = { onDragEnd() }
//                )
//            else
//                detectDragGestures(
//                    onDragStart = {
//                        //haptic feedback?
//
//                        onDragStart()
//                    },
//                    onDrag = { change, _ -> onDrag(change) },
//                    onDragEnd = { onDragEnd() }
//                )
        }
    }
//        .offset {
//            // Use the animating offset value here.
//            IntOffset(0, offsetY.value.roundToInt())
//        }
}

fun Modifier.dragHandle(
    isDraggedAfterLongPress: Boolean,
    onDragStart: () -> Unit,
    onDrag: (change: PointerInputChange) -> Unit,
    onDragEnd: () -> Unit
): Modifier = composed{
    val haptic = LocalHapticFeedback.current

    pointerInput(Unit) {
        if (isDraggedAfterLongPress)
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDragStart()
                },
                onDrag = { change, _ -> onDrag(change) },
                onDragEnd = { onDragEnd() }
            )
        else
            detectDragGestures(
                onDragStart = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDragStart()
                },
                onDrag = { change, _ -> onDrag(change) },
                onDragEnd = { onDragEnd() }
            )
    }
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


















