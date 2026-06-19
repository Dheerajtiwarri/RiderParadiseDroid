package com.droid.riderparadise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.riderparadise.ui.theme.BrandGreen
import com.droid.riderparadise.ui.theme.CardWhite
import com.droid.riderparadise.ui.theme.Ink

/**
 * Six-box OTP entry. A single hidden [BasicTextField] owns focus/IME; the boxes are a visual
 * representation of [value]. The active box (next empty slot) is highlighted.
 */
@Composable
fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
) {
    BasicTextField(
        value = value,
        onValueChange = { new ->
            if (new.length <= length && new.all { it.isDigit() }) onValueChange(new)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = modifier.fillMaxWidth(),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                repeat(length) { i ->
                    val char = value.getOrNull(i)?.toString() ?: ""
                    val isActive = i == value.length
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardWhite)
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive) BrandGreen else Ink.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            char,
                            color = if (isActive) BrandGreen else Ink,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        },
    )
}
