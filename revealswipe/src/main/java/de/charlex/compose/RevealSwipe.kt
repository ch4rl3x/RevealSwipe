package de.charlex.compose


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RevealSwipe(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    onContentClick: (() -> Unit)? = null,
    onContentLongClick: ((DpOffset) -> Unit)? = null,
    backgroundStartActionLabel: String?,
    onBackgroundStartClick: () -> Boolean = { true },
    backgroundEndActionLabel: String?,
    onBackgroundEndClick: () -> Boolean = { true },
    closeOnContentClick: Boolean = true,
    closeOnBackgroundClick: Boolean = true,
    shape: CornerBasedShape,
    alphaEasing: Easing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f),
    backgroundCardStartColor: Color,
    backgroundCardEndColor: Color,
    card: @Composable BoxScope.(
        shape: Shape,
        content: @Composable ColumnScope.() -> Unit
    ) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: RevealState = rememberRevealState(
        maxRevealDp = 75.dp,
        directions = setOf(
            RevealDirection.StartToEnd,
            RevealDirection.EndToStart
        )
    ),
    hiddenContentEnd: @Composable BoxScope.() -> Unit = {},
    hiddenContentStart: @Composable BoxScope.() -> Unit = {},
    content: @Composable (Shape) -> Unit
) {
    val closeOnContentClickHandler: () -> Unit = remember(coroutineScope, state) {
        {
            coroutineScope.launch {
                state.reset()
            }
        }
    }

    val backgroundStartClick = remember(coroutineScope, state, onBackgroundStartClick) {
        {
            if (closeOnBackgroundClick) {
                coroutineScope.launch {
                    state.reset()
                }
            }
            onBackgroundStartClick()
        }
    }

    val backgroundEndClick = remember(coroutineScope, state, onBackgroundEndClick) {
        {
            if (closeOnBackgroundClick) {
                coroutineScope.launch {
                    state.reset()
                }
            }
            onBackgroundEndClick()
        }
    }

    val hapticFeedback = LocalHapticFeedback.current
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }

    BaseRevealSwipe(
        modifier = modifier.semantics {
            customActions = buildList {
                backgroundStartActionLabel?.let {
                    add(
                        CustomAccessibilityAction(
                            it,
                            onBackgroundStartClick
                        )
                    )
                }
                backgroundEndActionLabel?.let {
                    add(
                        CustomAccessibilityAction(
                            it,
                            onBackgroundEndClick
                        )
                    )
                }
            }
        },
        enableSwipe = enableSwipe,
        animateBackgroundCardColor = enableSwipe,
        shape = shape,
        alphaEasing = alphaEasing,
        backgroundCardStartColor = backgroundCardStartColor,
        backgroundCardEndColor = backgroundCardEndColor,
        card = card,
        state = state,
        hiddenContentEnd = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        backgroundEndClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                hiddenContentEnd()
            }
        },
        hiddenContentStart = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        backgroundStartClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                hiddenContentStart()
            }
        },
        content = {
            val clickableModifier = when {
                onContentClick != null && !closeOnContentClick -> {
                    Modifier.combinedClickable(
                        onClick = onContentClick,
                        onLongClick = {
                            onContentLongClick?.let {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                it.invoke(pressOffset)
                            }
                        }
                    )
                }
                onContentClick == null && closeOnContentClick -> {
                    // if no onContentClick handler passed, add click handler with no indication to enable close on content click
                    Modifier.combinedClickable(
                        onClick = closeOnContentClickHandler,
                        onLongClick = {
                            onContentLongClick?.let {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                it.invoke(pressOffset)
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                }
                onContentClick != null && closeOnContentClick -> {
                    // decide based on state:
                    // 1. if open, just close without indication
                    // 2. if closed, call click handler
                    Modifier.combinedClickable(
                        onClick =
                        {
                            val isOpen =
                                state.anchoredDraggableState.targetValue != RevealValue.Default
                            // if open, just close. No click event.
                            if (isOpen) {
                                closeOnContentClickHandler()
                            } else {
                                onContentClick()
                            }
                        },
                        onLongClick = {
                            onContentLongClick?.let {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                it.invoke(pressOffset)
                            }
                        },
                        // no indication if just closing
                        indication = if (state.anchoredDraggableState.targetValue != RevealValue.Default) null else LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                }
                else -> Modifier
            }

            Box(
                modifier = clickableModifier.pointerInput(true) {
                        kotlinx.coroutines.coroutineScope {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                pressOffset = DpOffset(
                                    down.position.x.toDp(),
                                    down.position.y.toDp()
                                )
                            }
                        }
                    }
            ) {
                content(it)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseRevealSwipe(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    animateBackgroundCardColor: Boolean = true,
    shape: CornerBasedShape,
    alphaEasing: Easing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f),
    backgroundCardStartColor: Color,
    backgroundCardEndColor: Color,
    card: @Composable BoxScope.(
        shape: Shape,
        content: @Composable ColumnScope.() -> Unit
    ) -> Unit,
    state: RevealState = rememberRevealState(
        maxRevealDp = 75.dp,
        directions = setOf(
            RevealDirection.StartToEnd,
            RevealDirection.EndToStart
        )
    ),
    hiddenContentEnd: @Composable BoxScope.() -> Unit = {},
    hiddenContentStart: @Composable BoxScope.() -> Unit = {},
    content: @Composable (Shape) -> Unit
) {
    Box(
        modifier = modifier
    ) {
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
            (-state.anchoredDraggableState.offset / minDragAmountForStraightCorner).nonNaNorZero().coerceIn(0f, 1f).or(0f) {
                state.directions.contains(RevealDirection.EndToStart).not()
            }

        val cornerFactorStart =
            (state.anchoredDraggableState.offset / minDragAmountForStraightCorner).nonNaNorZero().coerceIn(0f, 1f).or(0f) {
                state.directions.contains(RevealDirection.StartToEnd).not()
            }

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
        val maxRevealPx = with(LocalDensity.current) { state.maxRevealDp.toPx() }
        val draggedRatio = (state.anchoredDraggableState.offset.absoluteValue / maxRevealPx.absoluteValue).coerceIn(0f, 1f)

        // cubic parameters can be evaluated here https://cubic-bezier.com/
        val alpha = alphaEasing.transform(draggedRatio)

        val animatedBackgroundEndColor = if (alpha in 0f..1f && animateBackgroundCardColor) backgroundCardEndColor.copy(
            alpha = alpha
        ) else backgroundCardEndColor

        val animatedBackgroundStartColor = if (alpha in 0f..1f && animateBackgroundCardColor) backgroundCardStartColor.copy(
            alpha = alpha
        ) else backgroundCardStartColor

        // non swipeable with hidden content
        card(shape) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
            ) {
                val hasStartContent = state.directions.contains(RevealDirection.StartToEnd)
                val hasEndContent = state.directions.contains(RevealDirection.EndToStart)
                if (hasStartContent) {
                    Box(
                        modifier = Modifier
                            .width(state.maxRevealDp)
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .background(animatedBackgroundStartColor),
                        content = hiddenContentStart
                    )
                }
                if (hasEndContent) {
                    Box(
                        modifier = Modifier
                            .width(state.maxRevealDp)
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .background(animatedBackgroundEndColor),
                        content = hiddenContentEnd
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .then(
                    if (enableSwipe)
                        Modifier
                            .offset {
                                IntOffset(
                                    x = state.anchoredDraggableState
                                        .requireOffset()
                                        .roundToInt(),
                                    y = 0,
                                )
                            }
                            .anchoredDraggable(
                                state = state.anchoredDraggableState,
                                orientation = Orientation.Horizontal,
                                enabled = true, // state.value == RevealValue.Default,
                                reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl
                            )
                    else Modifier
                )
        ) {
            content(animatedShape)
        }

        // This box is used to determine shape size.
        // The box is sized to match it's parent, which in turn is sized according to its first child - the card.
        BoxWithConstraints(
            modifier = Modifier.matchParentSize()
        ) {
            shapeSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        }
    }
}

/**
 * Return an alternative value if whenClosure is true. Replaces if/else
 */
private fun <T> T.or(orValue: T, whenClosure: T.() -> Boolean): T {
    return if (whenClosure()) orValue else this
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
@Composable
fun rememberRevealState(
    maxRevealDp: Dp = 75.dp,
    directions: Set<RevealDirection> = setOf(RevealDirection.StartToEnd, RevealDirection.EndToStart),
    initialValue: RevealValue = RevealValue.Default,
    positionalThreshold: (totalDistance: Float) -> Float = { distance: Float -> distance * 0.5f },
    velocityThreshold: (() -> Float)? = null,
    animationSpec: AnimationSpec<Float> = tween(),
//    decayAnimationSpec:DecayAnimationSpec<Float> = exponentialDecay(),
    confirmValueChange: (newValue: RevealValue) -> Boolean = { true }
): RevealState {
    val density = LocalDensity.current
    return remember {
        RevealState(
            maxRevealDp = maxRevealDp,
            directions = directions,
            density = density,
            initialValue = initialValue,
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold ?: { with(density) { 100.dp.toPx() } },
            animationSpec = animationSpec,
//            decayAnimationSpec = decayAnimationSpec,
            confirmValueChange = confirmValueChange
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
data class RevealState(
    val maxRevealDp: Dp = 75.dp,
    val directions: Set<RevealDirection>,
    private val density: Density,
    private val initialValue: RevealValue = RevealValue.Default,
    private val positionalThreshold: (totalDistance: Float) -> Float = { distance: Float -> distance * 0.5f },
    private val velocityThreshold: (() -> Float)? = null,
    private val animationSpec: AnimationSpec<Float> = tween(),
//    private val decayAnimationSpec: DecayAnimationSpec<Float>,
    private val confirmValueChange: (newValue: RevealValue) -> Boolean = { true }
) {
    @OptIn(ExperimentalFoundationApi::class)
    val anchoredDraggableState: AnchoredDraggableState<RevealValue> = AnchoredDraggableState(
        initialValue = initialValue,
        anchors = DraggableAnchors {
            RevealValue.Default at 0f
            if (RevealDirection.StartToEnd in directions) RevealValue.FullyRevealedEnd at with(density) { maxRevealDp.toPx() }
            if (RevealDirection.EndToStart in directions) RevealValue.FullyRevealedStart at -with(density) { maxRevealDp.toPx() }
        },
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold ?: { with(density) { 10.dp.toPx() } },
        animationSpec = animationSpec,
//        decayAnimationSpec = decayAnimationSpec,
        confirmValueChange = confirmValueChange
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@OptIn(ExperimentalFoundationApi::class)
suspend fun RevealState.reset() {
    anchoredDraggableState.animateTo(
        targetValue = RevealValue.Default,
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@OptIn(ExperimentalFoundationApi::class)
suspend fun RevealState.resetFast() {
    anchoredDraggableState.snapTo(
        targetValue = RevealValue.Default,
    )
}
