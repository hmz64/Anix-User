package com.anix.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.anix.app.core.di.PreferencesKeys
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.AccentLime
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Secondary
import com.anix.app.core.theme.TextBlack
import com.anix.app.ui.components.NeoButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Text(
            text = "Skip",
            color = Primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clickable {
                    scope.launch {
                        ServiceLocator.getDataStore()?.edit { it[PreferencesKeys.ONBOARDING_DONE] = true }
                        onComplete()
                    }
                }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
        ) { page ->
            OnboardingPageContent(page = page)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 12.dp else 10.dp)
                            .background(
                                if (pagerState.currentPage == index) Primary else Color.White,
                                RoundedCornerShape(6.dp)
                            )
                            .border(2.dp, BorderBlack, RoundedCornerShape(6.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            NeoButton(
                text = if (pagerState.currentPage == 2) "Get Started" else "Next",
                onClick = {
                    if (pagerState.currentPage == 2) {
                        scope.launch {
                            ServiceLocator.getDataStore()?.edit { it[PreferencesKeys.ONBOARDING_DONE] = true }
                            onComplete()
                        }
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Primary
            )
        }
    }
}

private data class OnboardingPage(val title: String, val description: String, val emoji: String, val accentColor: Color)

@Composable
private fun OnboardingPageContent(page: Int) {
    val (title, description, emoji, accentColor) = when (page) {
        0 -> OnboardingPage(
            "Welcome to Anix",
            "Discover, watch, and track your favorite anime series all in one place with a bold new experience.",
            "\uD83C\uDFAC",
            Primary
        )
        1 -> OnboardingPage(
            "Watch Anywhere",
            "Stream your favorite anime on mobile, tablet, or desktop. Pick up right where you left off across all your devices.",
            "\uD83D\uDCF1",
            AccentLime
        )
        2 -> OnboardingPage(
            "Join Community",
            "Connect with fellow fans, join exclusive clans, participate in epic giveaways, and share your passion for anime.",
            "\uD83E\uDD1D",
            Secondary
        )
        else -> OnboardingPage("", "", "", Color.White)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(3.dp, BorderBlack, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 64.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 6.dp, y = 6.dp)
                        .background(BorderBlack, RoundedCornerShape(8.dp))
                )
                Box(
                    modifier = Modifier
                        .background(accentColor, RoundedCornerShape(8.dp))
                        .border(2.dp, BorderBlack, RoundedCornerShape(8.dp))
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlack,
                textAlign = TextAlign.Center
            )
        }
    }
}
