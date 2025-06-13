package com.example.everday.ui.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.everday.ui.theme.EverdayTheme
import com.example.everday.ui.theme.OnboardingPage1Color
import com.example.everday.ui.theme.OnboardingPage2Color
import com.example.everday.ui.theme.OnboardingPage3Color
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


// Data class and page list remain the same
data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String,
    val color: Color
)

val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.ic_page1,
        title = "Welcome to the App",
        description = "Discover amazing features that will change the way you interact with the world.",
        color = OnboardingPage1Color
    ),
    OnboardingPage(
        imageRes = R.drawable.ic_page2,
        title = "Stay Connected",
        description = "Easily connect with friends and family, share moments, and create memories.",
        color = OnboardingPage2Color
    ),
    OnboardingPage(
        imageRes = R.drawable.ic_page3,
        title = "Let's Get Started",
        description = "You're all set! Jump right in and start your journey with us.",
        color = OnboardingPage3Color
    )
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen() {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    // Use our new LiquidSwipeScaffold for the background effect
    LiquidSwipeScaffold(pagerState = pagerState) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPageContent(
                    page = onboardingPages[pageIndex],
                    pagerState = pagerState,
                    pageIndex = pageIndex
                )
            }

            // Bottom controls section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Use our new WormPageIndicator
                WormPageIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // Use our new InteractiveButton
                InteractiveButton(
                    pagerState = pagerState,
                    onClick = {
                        if (pagerState.currentPage < onboardingPages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1,
                                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                                )
                            }
                        } else {
                            // Handle "Get Started" click action
                            println("Onboarding finished!")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pagerState: PagerState,
    pageIndex: Int
) {
    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

    // Enhanced animations with more bounce
    val imageAlpha by remember { derivedStateOf { 1f - pageOffset.absoluteValue.coerceIn(0f, 1f) } }
    val imageScale by remember { derivedStateOf { 1f - (pageOffset.absoluteValue.coerceIn(0f, 1f) * 0.4f) } }
    val textAlpha by remember { derivedStateOf { 1f - (pageOffset.absoluteValue.coerceIn(0f, 1f) * 1.5f).coerceIn(0f, 1f) } }
    val textTranslationX by remember { derivedStateOf { pageOffset * (size.width * 0.8f) } }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .graphicsLayer {
                // Apply a parallax effect to the whole page content
                translationX = if (pageIndex < pagerState.currentPage) {
                    -size.width * pageOffset.absoluteValue
                } else {
                    size.width * pageOffset.absoluteValue
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(250.dp)
                .graphicsLayer {
                    alpha = imageAlpha
                    scaleX = imageScale
                    scaleY = imageScale
                }
        )
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.graphicsLayer {
                alpha = textAlpha
                translationX = textTranslationX
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingConceptTheme {
        OnboardingView()
    }
}

// Dummy R class for drawable resources for preview purposes.
object R {
    object drawable {
        val ic_page1: Int = android.R.drawable.ic_dialog_map
        val ic_page2: Int = android.R.drawable.ic_dialog_dialer
        val ic_page3: Int = android.R.drawable.ic_dialog_email
    }
}
