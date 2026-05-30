package com.neoconcept.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colors
object SwissColor {
    val bg = Color.White
    val fg = Color.Black
    val muted = Color(0xFFF2F2F2)
    val accent = Color(0xFFFF3000)
    val border = Color.Black
    val success = Color.Black
    val error = Color(0xFFFF3000)
}

// Spacing (4px grid)
object SwissSpace {
    val space1 = 4.dp
    val space2 = 8.dp
    val space3 = 12.dp
    val space4 = 16.dp
    val space5 = 20.dp
    val space6 = 24.dp
    val space8 = 32.dp
    val space10 = 40.dp
}

// Border
object SwissBorder {
    val width = 2.dp
    val widthThick = 3.dp
    val radius = 0.dp
}

// Typography
object SwissTypography {
    val titleLg = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )

    val title = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )

    val titleSm = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )

    val labelLg = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )

    val label = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )

    val labelSm = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )

    val body = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )

    val bodySm = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    )

    val caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
}

// Touch target
object SwissTouch {
    val minHeight = 44.dp
}
