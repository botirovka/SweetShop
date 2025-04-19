package com.botirovka.sweetshopcompose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.botirovka.sweetshopcompose.R


val MontseratFontFamily = FontFamily(
    Font(R.font.montserrat, FontWeight(400), FontStyle.Normal),
    Font(R.font.montserrat_medium, FontWeight(500), FontStyle.Normal),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MontseratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MontseratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MontseratFontFamily,
        fontWeight =  FontWeight.Medium,
        fontSize = 36.sp,
        color = Color.White,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)