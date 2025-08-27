package com.example.scratchmap.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.glassmorphism(
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    color: Color = Color.White.copy(alpha = 0.2f),
    borderColor: Color = Color.White.copy(alpha = 0.3f),
    borderWidth: Float = 1.5f
): Modifier = composed {
    this
        .border(
            width = borderWidth.dp,
            color = borderColor,
            shape = shape
        )
        .background(
            color = color,
            shape = shape
        )
}
