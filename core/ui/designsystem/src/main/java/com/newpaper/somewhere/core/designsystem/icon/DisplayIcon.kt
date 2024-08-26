package com.newpaper.somewhere.core.designsystem.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

//draw icon
@Composable
fun DisplayIcon(
    icon: MyIcon,

    enabled: Boolean = true,/**color priority: [enabled] > [color] > [icon.color]**/
    color: Color? = null,
    contentDescriptionIsNull: Boolean = false
){
    val imageVector = icon.imageVector
    val contentDescription = if (!contentDescriptionIsNull) stringResource(id = icon.descriptionTextId)
                                else null


    val modifier = Modifier.size(icon.size)

    if (!enabled || icon.isGray) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    else {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = color ?: icon.color ?: LocalContentColor.current
        )
    }
}