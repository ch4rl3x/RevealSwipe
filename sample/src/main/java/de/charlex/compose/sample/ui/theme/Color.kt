package de.charlex.compose.sample.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

object CustomColors {
    val one = Color(80,81,96)
    val two = Color(104, 130, 158)
    val three =  Color(174, 189, 56)
    val four = Color(89, 130, 52)
}

val Colors.one: Color
    get() = CustomColors.one

val Colors.two: Color
    get() = CustomColors.two

val Colors.three: Color
    get() = CustomColors.three

val Colors.four: Color
    get() = CustomColors.four