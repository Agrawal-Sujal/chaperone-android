package com.raven.chaperone.ui.screens.commonComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.raven.chaperone.R
import com.raven.chaperone.ui.theme.mediumPurple
import com.raven.chaperone.ui.theme.textPurple

@Composable
fun CustomProgressBar() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = mediumPurple,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Every journey begins with a single step.", color = textPurple)
    }

}