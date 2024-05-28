package de.charlex.compose

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

@Composable
fun Modifier.nonFocusableClickable(
    onClick: () -> Boolean
): Modifier {
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    return this.pointerInput(onClick) {
        awaitEachGesture {
            val change = awaitFirstDown().also { it.consume() }
            val press = PressInteraction.Press(change.position)
            coroutineScope.launch {
                interactionSource.emit(press)
            }
            val up = waitForUpOrCancellation()
            if (up != null) {
                up.consume()
                coroutineScope.launch {
                    interactionSource.emit(PressInteraction.Release(press))
                }
                onClick()
            }
        }
    }
}