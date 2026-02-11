package com.jvcodingsolutions.smartstep.design_system.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.inter_medium
import smartstep.composeapp.generated.resources.inter_regular
import smartstep.composeapp.generated.resources.inter_semibold
import smartstep.composeapp.generated.resources.inter_variable

val Inter @Composable get() = FontFamily(
    Font(
        Res.font.inter_regular,
        weight = FontWeight.Normal
    ),
    Font(
        Res.font.inter_semibold,
        weight = FontWeight.SemiBold
    ),
    Font(
        Res.font.inter_medium,
        weight = FontWeight.Medium
    ),
    Font(
        Res.font.inter_variable
    ),

)

val Typography @Composable get() = Typography(
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

)

val Typography.titleAccent: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 64.sp,
        lineHeight = 70.sp
    )

val Typography.bodyLargeRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

val Typography.bodyLargeMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

val Typography.bodyMediumRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )

val Typography.bodyMediumMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )

val Typography.bodySmallRegular: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )