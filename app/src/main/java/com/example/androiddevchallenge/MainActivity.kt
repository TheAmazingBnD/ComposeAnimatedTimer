/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View.GONE
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Timer Time") },
                elevation = 4.dp
            )
        },
        content = {
            TimerView()
        }
    )
}

@ExperimentalAnimationApi
@Composable
fun TimerView() {
    Column {
        var visibility by rememberSaveable { mutableStateOf(true) }
        var count by rememberSaveable { mutableStateOf(0) }
        var timerCount by rememberSaveable { mutableStateOf(0) }

        AnimatedVisibility(visible = visibility) {
            TimeSelection(
                onSelection = {
                    count = it.first
                    timerCount = it.second
                    visibility = false
                }
            )
        }
        if (count > 0) {
            Hourglass(count = count, timerCount = timerCount) {
                visibility = it
                count = 0
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun Hourglass(
    count: Int = 60,
    timerCount: Int,
    timerSelectionVisibility: (Boolean) -> Unit
) {
    var visibility by rememberSaveable { mutableStateOf(true) }
    var buttonVisibility by rememberSaveable { mutableStateOf(false) }
    var isAnimating by rememberSaveable { mutableStateOf(true) }
    var timerVisibility by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val customView = remember { LottieAnimationView(context) }
    var timer by rememberSaveable { mutableStateOf(0) }

    isAnimating = customView.isAnimating

    val countDownTimer = object : CountDownTimer((timerCount * 1000).toLong(), 1000) {
        override fun onTick(p0: Long) {
            timer = (p0 / 1000).toInt()
        }

        override fun onFinish() {
            timerVisibility = false
        }
    }

    timerVisibility = true
    countDownTimer.start()


    if (!isAnimating) {
        buttonVisibility = true
        AnimatedVisibility(visible = buttonVisibility) {
            Button(
                onClick = {
                    buttonVisibility = false
                    visibility = false
                    customView.visibility = GONE
                    timerSelectionVisibility(true)
                },
                content = { Text(text = "Restart") },
                modifier = Modifier
                    .padding(10.dp)
                    .height(50.dp)
                    .fillMaxWidth()
            )
        }
    }

    AnimatedVisibility(visible = timerVisibility) {
        RenderTimerUpdate(time = timer)
        if (timer == 0) {
            countDownTimer.onFinish()
        }
    }

    AnimatedVisibility(visible = visibility) {
        AndroidView({ customView }) { view ->
            with(view) {
                setAnimation(R.raw.hourglass)
                repeatMode = LottieDrawable.RESTART
                foregroundGravity = Gravity.CENTER
                speed = 0.8f
                repeatCount = count
                elevation = 4f
                playAnimation()
            }
        }
    }
}

@Composable
fun RenderTimerUpdate(time: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time.toString(),
            modifier = Modifier
                .padding(10.dp),
            fontSize = 42.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TimeSelection(modifier: Modifier = Modifier, onSelection: (Pair<Int, Int>) -> Unit) {
    Column {
        Button(
            onClick = { onSelection(Pair(3, 10)) },
            content = { Text(text = "10") },
            modifier = modifier
                .padding(10.dp)
                .height(50.dp)
                .fillMaxWidth()
        )
        Button(
            onClick = { onSelection(Pair(5, 15)) },
            content = { Text(text = "15") },
            modifier = modifier
                .padding(10.dp)
                .height(50.dp)
                .fillMaxWidth()
        )
        Button(
            onClick = { onSelection(Pair(11, 30)) },
            content = { Text(text = "30") },
            modifier = modifier
                .padding(10.dp)
                .height(50.dp)
                .fillMaxWidth()
        )
        Button(
            onClick = { onSelection(Pair(21, 60)) },
            content = { Text(text = "60") },
            modifier = modifier
                .padding(10.dp)
                .height(50.dp)
                .fillMaxWidth()
        )
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
