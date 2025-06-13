package com.example.everday.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import android.content.res.Resources

/**
 * A custom composable that creates the liquid/wave swipe effect.
 * It draws two colored paths that animate based on the swipe progress.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun LiquidSwipeScaffold(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val currentPage = pagerState.currentPage
    val nextPage = (currentPage + 1).coerceAtMost(onboardingPages.lastIndex)
    val pageOffset = pagerState.currentPageOffsetFraction.absoluteValue

    val animatedColor by animateColorAsState(
        targetValue = onboardingPages[currentPage].color,
        animationSpec = tween(600)
    )

    Box(modifier = modifier) {
        // Background color for the current page
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLiquidSwipe(
                color = animatedColor,
                swipeProgress = 1f // always full
            )
        }
        // Animated foreground liquid swipe for the next page
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLiquidSwipe(
                color = onboardingPages[nextPage].color,
                swipeProgress = pageOffset
            )
        }
        content()
    }
}

private fun DrawScope.drawLiquidSwipe(color: Color, swipeProgress: Float) {
    val width = size.width
    val height = size.height
    val minSwipeDistance = 0.15f // Start the wave effect after a bit of a swipe
    val maxWaveHeight = 100.dp.toPx()

    if (swipeProgress < minSwipeDistance) return

    val path = Path()
    path.moveTo(width, 0f)
    path.lineTo(width, height)
    path.lineTo(0f, height)
    path.lineTo(0f, 0f)

    // Calculate the wave's horizontal position based on swipe progress
    val waveStart = (1f - swipeProgress) * width
    val waveControlPointX = waveStart + (width * (swipeProgress - minSwipeDistance) / (1f - minSwipeDistance)) * 0.5f

    path.moveTo(waveStart, 0f)
    path.quadraticBezierTo(
        x1 = waveControlPointX, y1 = height * 0.5f,
        x2 = waveStart, y2 = height
    )
    path.lineTo(width, height)
    path.lineTo(width, 0f)
    path.close()

    drawPath(path, color = color)
}


/**
 * An animated page indicator that shows a "worm" sliding between dots.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WormPageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 10.dp,
    indicatorSpacing: Dp = 12.dp,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.5f)
) {
    val pageCount = pagerState.pageCount
    val totalWidth = indicatorSize * pageCount + indicatorSpacing * (pageCount - 1)
    val indicatorWidthPx = indicatorSize.toPx()
    val spacingPx = indicatorSpacing.toPx()

    // Calculate the x-offset of the sliding "worm"
    val wormOffset by remember(pagerState) {
        derivedStateOf {
            (pagerState.currentPage * (indicatorWidthPx + spacingPx)) +
                    (pagerState.currentPageOffsetFraction * (indicatorWidthPx + spacingPx))
        }
    }

    Box(modifier = modifier.height(indicatorSize)) {
        // Draw the inactive dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(indicatorSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) {
                Box(
                    modifier = Modifier
                        .size(indicatorSize)
                        .clip(CircleShape)
                        .background(inactiveColor)
                )
            }
        }
        // Draw the active, sliding "worm" dot
        Box(
            modifier = Modifier
                .offset(x = wormOffset.toDp())
                .size(indicatorSize)
                .clip(CircleShape)
                .background(activeColor)
        )
    }
}

private fun Float.toDp() = (this / Resources.getSystem().displayMetrics.density).dp
private fun Dp.toPx() = (this.value * Resources.getSystem().displayMetrics.density)



/**
 * An interactive button that transforms from a circle arrow to a pill-shaped button.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun InteractiveButton(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
    val pageOffset = pagerState.currentPageOffsetFraction.absoluteValue

// Removed unused transitionProgress variable
    val buttonWidth by animateDpAsState(
        targetValue = if (isLastPage) 200.dp else 60.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val buttonCornerRadius by animateDpAsState(
        targetValue = if (isLastPage) 16.dp else 30.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier
            .width(buttonWidth)
            .height(60.dp)
            .clip(RoundedCornerShape(buttonCornerRadius))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Animate the visibility of the arrow and text
        val arrowAlpha by animateFloatAsState(targetValue = if (isLastPage) 0f else 1f, animationSpec = tween(200))
        val textAlpha by animateFloatAsState(targetValue = if (isLastPage) 1f else 0f, animationSpec = tween(200))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next",
            tint = Color.Black,
            modifier = Modifier.align(Alignment.Center).graphicsLayer { alpha = arrowAlpha }
        )
        Text(
            text = "Get Started",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center).graphicsLayer { alpha = textAlpha }
        )
    }
}