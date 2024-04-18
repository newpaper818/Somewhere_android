package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.SpotTypeGroupCard
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

@Composable
fun CircularLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
){
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = 5.dp,
        strokeCap = StrokeCap.Round
    )
}

























@Composable
@PreviewLightDark
private fun CircularLoadingIndicatorPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(100.dp)
        ) {
            CircularLoadingIndicator()
        }
    }
}