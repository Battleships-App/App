package com.isel.battleshipsAndroid.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun SquareImageView(
    background: Color,
    selected: Boolean = false,
    highlight: Boolean = false,
    onClick: () -> Unit = { }
) = Box(
    modifier = Modifier
        .size(50.dp)
        .then(if (selected) Modifier.border(width = 1.dp, color = Color.Red) else Modifier.border(BorderStroke(1.dp, Color.Black)))
        .background(background)
        .clickable(true) {
            onClick()
        }
) {
    if (highlight) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x7700FF00),
                center = Offset(x = size.width / 2, y = size.height / 2),
                radius = (size.minDimension / 2) * 0.7F
            )
        }
    }
}