package com.example.somewhere.ui.screenUtils

import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.runtime.Composable
import com.example.somewhere.ui.theme.gray

@Composable
fun MyRadioButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        colors = RadioButtonDefaults.colors(
            selectedColor = MaterialTheme.colors.primary,
            unselectedColor = gray,
            disabledColor = gray,
        )
    )
}