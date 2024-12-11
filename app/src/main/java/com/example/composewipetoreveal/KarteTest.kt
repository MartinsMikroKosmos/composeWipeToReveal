package com.example.composewipetoreveal
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun MagicCard(
    frontImageId: Int,
    backImageId: Int,
    onSwipeComplete: () -> Unit,
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }

    val rotation = animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        )
    )

    val offsetXAnimated = animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
            // Kombiniere Tap und Drag Gesten in einem einzigen pointerInput
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > size.width / 3) {
                            offsetX = if (offsetX > 0) size.width.toFloat() else -size.width.toFloat()
                            onSwipeComplete()
                        } else {
                            offsetX = 0f
                        }
                    },
                    onDragCancel = {
                        offsetX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    isFlipped = !isFlipped
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetXAnimated.value.toInt(), 0) }
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 8 * density
                }
        ) {
            // Vorderseite
            Image(
                painter = painterResource(id = frontImageId),
                contentDescription = "Card Front",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (rotation.value <= 90f) 1f else 0f
                    },
                contentScale = ContentScale.Fit,
            )

            // Rückseite
            Image(
                painter = painterResource(id = backImageId),
                contentDescription = "Card Back",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                        alpha = if (rotation.value > 90f) 1f else 0f
                    },
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun CardGame() {
    var currentCardIndex by remember { mutableStateOf(0) }
    val cards = listOf(
        Pair(R.drawable.card_front1, R.drawable.card_back),
        Pair(R.drawable.card_front2, R.drawable.card_back),
        // Weitere Karten hier hinzufügen
    )

    MagicCard(
        frontImageId = cards[currentCardIndex].first,
        backImageId = cards[currentCardIndex].second,
        onSwipeComplete = {
            currentCardIndex = (currentCardIndex + 1) % cards.size
        },
    )
}

@Preview
@Composable
private fun a() {
    CardGame()
}
