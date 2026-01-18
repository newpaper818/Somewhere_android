package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.IconTextButtonIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R


@Composable
fun ToggleSortOrderButton(
    isOrderByLatest: Boolean,
    onClick: () -> Unit,
    buttonColor: Color = Color.Transparent
){
    val latestText = stringResource(id = R.string.latest_)
    val oldestText = stringResource(id = R.string.oldest)

    val textStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)

    ClickableBox(
        onClick = onClick,
        shape = SmoothRoundedCornerShape(999.dp, 1f),
        containerColor = buttonColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 14.dp, end = 8.dp)
        ) {
            Text(
                text = if (isOrderByLatest) latestText else oldestText,
                style = textStyle,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            MySpacerRow(2.dp)

            DisplayIcon(if (isOrderByLatest) IconTextButtonIcon.sortArrowDescending
                                else IconTextButtonIcon.sortArrowAscending)
        }
    }
}










@Composable
@PreviewLightDark
private fun FilterChipButtonPreview() {
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(200.dp)
        ) {
            ToggleSortOrderButton(
                isOrderByLatest = true,
                onClick = {}
            )
        }
    }
}