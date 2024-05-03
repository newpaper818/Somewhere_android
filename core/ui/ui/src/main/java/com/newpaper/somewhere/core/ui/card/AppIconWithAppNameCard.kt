package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.suite
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun AppIconWithAppNameCard(

){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageFromDrawable(
            imageDrawable = R.drawable.app_icon_fit,
            contentDescription = stringResource(id = R.string.somewhere_app_icon),
            modifier = Modifier.size(80.dp)
        )

        MySpacerRow(width = 24.dp)

        Column {
            Text(
                text = stringResource(id = R.string.app_name_big),
                fontSize = 24.sp,
                fontFamily = suite,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            MySpacerColumn(height = 8.dp)
            Text(
                text = stringResource(id = R.string.trip_planner_app),
                fontSize = 18.sp,
                fontFamily = suite,
                fontWeight = FontWeight.Bold
            )
        }
        
        MySpacerRow(width = 16.dp)
    }

}























@Composable
@PreviewLightDark
private fun AppIconWithAppNameCardPreview(){
    SomewhereTheme {
        MyScaffold {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                AppIconWithAppNameCard()
            }
        }
    }
}