package com.example.somewhere.utils


import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.somewhere.model.Trip
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import kotlin.math.sign



enum class SlideState {
    NONE,
    UP,
    DOWN
}

fun  Modifier.dragAndDrop(
//    item: T,
    item: Trip,
//    items: MutableList<T>,
    items: List<Trip>,
    itemHeight: Int,
    updateSlideState: (showingTripList: List<Trip>, itemIdx: Int, slideState: SlideState) -> Unit,
    isDraggedAfterLongPress: Boolean,
    offsetY: Animatable<Float, AnimationVector1D>,
    onStartDrag: () -> Unit,
    onStopDrag: (currentIndex: Int, destinationIndex: Int) -> Unit,
): Modifier = composed {
    Log.d("test3", "        on drag drop trip: ${item.titleText} | tripList: ${items.map { it.titleText } }}")


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
                Log.d("test2", "    on drag trip: ${item.titleText} | tripList: ${items.map { it.titleText } }}")
                val verticalDragOffset = offsetY.value + change.positionChange().y

                launch {
                    offsetY.snapTo(verticalDragOffset)

                    val offsetSign = offsetY.value.sign.toInt()
                    previousNumberOfItems = numberOfItems
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

                        updateSlideState(items, idx, SlideState.NONE)
                    } else if (numberOfItems != 0) {
                        try {
                            updateSlideState(
                                items,
                                itemIdx + numberOfItems * offsetSign,
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
                    onStopDrag(itemIdx, itemIdx + listOffset)
                }
            }


            if (isDraggedAfterLongPress)
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        //haptic feedback?

                        onDragStart()
                    },
                    onDrag = { change, _ -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                )
            else
                detectDragGestures(
                    onDragStart = {
                        //haptic feedback?

                        onDragStart()
                    },
                    onDrag = { change, _ -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                )
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


















