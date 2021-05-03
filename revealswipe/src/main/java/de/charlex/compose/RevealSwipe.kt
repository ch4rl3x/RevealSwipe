package de.charlex.compose

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SnapSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
typealias RevealState = SwipeableState<RevealValue>

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RevealSwipe(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    onContentClick: () -> Unit = {},
    onBackgroundStartClick: () -> Unit = {},
    onBackgroundEndClick: () -> Unit = {},
    closeOnContentClick: Boolean = true,
    closeOnBackgroundClick: Boolean = true,
    animateBackgroundCardColor: Boolean = true,
    contentClickHandledExtern: Boolean = false,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    alphaEasing: Easing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f),
    maxRevealDp: Dp = 75.dp,
    maxAmountOfOverflow: Dp = 250.dp,
    directions: Set<RevealDirection> = setOf(
        RevealDirection.StartToEnd,
        RevealDirection.EndToStart
    ),
    contentColor: Color = MaterialTheme.colors.onPrimary,
    backgroundCardModifier: Modifier = Modifier,
    backgroundCardElevation: Dp = 0.dp,
    backgroundCardStartColor: Color = MaterialTheme.colors.secondaryVariant,
    backgroundCardEndColor: Color = MaterialTheme.colors.secondary,
    backgroundCardContentColor: Color = MaterialTheme.colors.onSecondary,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: RevealState = rememberRevealState(),
    hiddenContentEnd: @Composable RowScope.() -> Unit = {},
    hiddenContentStart: @Composable RowScope.() -> Unit = {},
    content: @Composable (Shape) -> Unit
) {
    val contentClick = {
        if (state.targetValue != RevealValue.Default && closeOnContentClick) {
            coroutineScope.launch {
                state.reset()
            }
            Unit
        } else onContentClick()
    }
    val backgroundStartClick = {
        if (state.targetValue == RevealValue.FullyRevealedStart && closeOnBackgroundClick) {
            coroutineScope.launch {
                state.reset()
            }
        }
        onBackgroundStartClick()
    }

    val backgroundEndClick = {
        if (state.targetValue == RevealValue.FullyRevealedEnd && closeOnBackgroundClick) {
            coroutineScope.launch {
                state.reset()
            }
        }
        onBackgroundEndClick()
    }

    Box {
        var shapeSize: Size by remember { mutableStateOf(Size(0f, 0f)) }

        val density = LocalDensity.current

        val cornerRadiusBottomEnd = remember(shapeSize, density) {
            shape.bottomEnd.toPx(
                shapeSize = shapeSize,
                density = density
            )
        }
        val cornerRadiusTopEnd = remember(shapeSize, density) {
            shape.topEnd.toPx(
                shapeSize = shapeSize,
                density = density
            )
        }

        val cornerRadiusBottomStart = remember(shapeSize, density) {
            shape.bottomStart.toPx(
                shapeSize = shapeSize,
                density = density
            )
        }
        val cornerRadiusTopStart = remember(shapeSize, density) {
            shape.topStart.toPx(
                shapeSize = shapeSize,
                density = density
            )
        }

        val minDragAmountForStraightCorner =
            kotlin.math.max(cornerRadiusTopEnd, cornerRadiusBottomEnd)

        val cornerFactorEnd =
            (-state.offset.value / minDragAmountForStraightCorner).nonNaNorZero().coerceIn(0f, 1f)

        val cornerFactorStart =
            (state.offset.value / minDragAmountForStraightCorner).nonNaNorZero().coerceIn(0f, 1f)

        val animatedCornerRadiusTopEnd: Float = lerp(cornerRadiusTopEnd, 0f, cornerFactorEnd)
        val animatedCornerRadiusBottomEnd: Float = lerp(cornerRadiusBottomEnd, 0f, cornerFactorEnd)

        val animatedCornerRadiusTopStart: Float = lerp(cornerRadiusTopStart, 0f, cornerFactorStart)
        val animatedCornerRadiusBottomStart: Float = lerp(cornerRadiusBottomStart, 0f, cornerFactorStart)

        val animatedShape = shape.copy(
            bottomStart = CornerSize(animatedCornerRadiusBottomStart),
            bottomEnd = CornerSize(animatedCornerRadiusBottomEnd),
            topStart = CornerSize(animatedCornerRadiusTopStart),
            topEnd = CornerSize(animatedCornerRadiusTopEnd)
        )

        // alpha for background
        val maxRevealPx = with(LocalDensity.current) { maxRevealDp.toPx() }
        val draggedRatio =
            (state.offset.value.absoluteValue / maxRevealPx.absoluteValue).coerceIn(0f, 1f)

        // cubic parameters can be evaluated here https://cubic-bezier.com/
        val alpha = alphaEasing.transform(draggedRatio)

        val animatedBackgroundEndColor = if (alpha in 0f..1f && animateBackgroundCardColor) backgroundCardEndColor.copy(
            alpha = alpha
        ) else backgroundCardEndColor

        val animatedBackgroundStartColor = if (alpha in 0f..1f && animateBackgroundCardColor) backgroundCardStartColor.copy(
            alpha = alpha
        ) else backgroundCardStartColor

        // non swipable with hidden content
        Card(
            contentColor = backgroundCardContentColor,
            backgroundColor = Color.Transparent,
            modifier = backgroundCardModifier
                .matchParentSize(),
            shape = shape,
            elevation = backgroundCardElevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .background(animatedBackgroundStartColor)
                        .clickable(onClick = backgroundStartClick),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentStart
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight()
                        .background(animatedBackgroundEndColor)
                        .clickable(onClick = backgroundEndClick),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = hiddenContentEnd
                )
            }
        }

        CompositionLocalProvider(
            LocalContentColor provides contentColor,
        ) {
            Box(
                modifier = modifier
                    .then(
                        if (enableSwipe) Modifier
                            .offset {
                                IntOffset(
                                    state.offset.value.roundToInt(),
                                    0
                                )
                            }
                            .revealSwipable(
                                state = state,
                                maxRevealPx = maxRevealPx,
                                maxAmountOfOverflow = maxAmountOfOverflow,
                                directions = directions
                            ) else Modifier
                    )
                    .clickable(
                        onClick = {
                            contentClick()
                        },
                        enabled = contentClickHandledExtern.not()
                    ),
            ) {
                content(animatedShape)
            }
            // This box is used to determine shape size.
            // The box is sized to match it's parent, which in turn is sized according to its first child - the card.
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                shapeSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun Modifier.revealSwipable(
    maxRevealPx: Float,
    maxAmountOfOverflow: Dp,
    directions: Set<RevealDirection>,
    state: RevealState,
) = composed {

    val maxAmountOfOverflowPx = with(LocalDensity.current) { maxAmountOfOverflow.toPx() }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val anchors = mutableMapOf(0f to RevealValue.Default)

    if (RevealDirection.StartToEnd in directions) anchors += maxRevealPx to RevealValue.FullyRevealedEnd
    if (RevealDirection.EndToStart in directions) anchors += -maxRevealPx to RevealValue.FullyRevealedStart

    val thresholds = { _: RevealValue, _: RevealValue ->
        FractionalThreshold(0.5f)
    }

    val minFactor =
        if (RevealDirection.EndToStart in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor
    val maxFactor =
        if (RevealDirection.StartToEnd in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor

    Modifier.swipeable(
        state = state,
        anchors = anchors,
        thresholds = thresholds,
        orientation = Orientation.Horizontal,
        enabled = true, // state.value == RevealValue.Default,
        reverseDirection = isRtl,
        resistance = ResistanceConfig(
            basis = maxAmountOfOverflowPx,
            factorAtMin = minFactor,
            factorAtMax = maxFactor
        )
    )
}

private fun Float.nonNaNorZero() = if (isNaN()) 0f else this

enum class RevealDirection {
    /**
     * Can be dismissed by swiping in the reading direction.
     */
    StartToEnd,

    /**
     * Can be dismissed by swiping in the reverse of the reading direction.
     */
    EndToStart
}

/**
 * Possible values of [RevealState].
 */
enum class RevealValue {
    /**
     * Indicates the component has not been revealed yet.
     */
    Default,

    /**
     * Fully revealed to end
     */
    FullyRevealedEnd,

    /**
     * Fully revealed to start
     */
    FullyRevealedStart,
}

/**
 * Create and [remember] a [RevealState] with the default animation clock.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberRevealState(
    initialValue: RevealValue = RevealValue.Default,
    confirmStateChange: (RevealValue) -> Boolean = { true },
): RevealState {
    return rememberSwipeableState(
        initialValue = initialValue,
        confirmStateChange = confirmStateChange
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@ExperimentalMaterialApi
suspend fun RevealState.reset() {
    animateTo(
        targetValue = RevealValue.Default,
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@ExperimentalMaterialApi
suspend fun RevealState.resetFast() {
    animateTo(
        targetValue = RevealValue.Default,
        anim = SnapSpec(1)
    )
}

@Preview
@Composable
fun RevealSwipegPreview() {
    val dummyList = listOf(
        Pair("Item 1", 0xFF505160),
        Pair("Item 2", 0xFF68829E),
        Pair("Item 3", 0xFFAEBD38),
        Pair("Item 4", 0xFF598234)
    )
    MaterialTheme {
        Surface(
            modifier = Modifier.width(400.dp).height(600.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(10.dp)
            ) {
                items(dummyList) { item ->
                    RevealSwipe(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        backgroundCardModifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        directions = setOf(
                            RevealDirection.StartToEnd,
                            RevealDirection.EndToStart
                        ),
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        hiddenContentEnd = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize().requiredHeight(80.dp),
                            backgroundColor = Color(item.second),
                            shape = it,
                        ) {

                            Text(
                                modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                                text = item.first
                            )
                        }
                    }
                }
            }
        }
    }
}