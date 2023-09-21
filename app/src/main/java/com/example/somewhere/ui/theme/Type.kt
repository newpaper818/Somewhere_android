package com.example.somewhere.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.somewhere.ui.theme.TextType.*

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    h3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    h4 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    h5 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        color = gray,
        fontSize = 16.sp
    ),
    h6 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    subtitle1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        color = gray,
        fontSize = 18.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        color = gray,
        fontSize = 16.sp
    ),

    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

enum class TextType{
    TOP_BAR__TITLE,
    TOP_BAR__SUBTITLE,

    TRIP_LIST_ITEM__TITLE,
    TRIP_LIST_ITEM__TITLE_NULL,
    TRIP_LIST_ITEM__SUBTITLE,

    CARD__TITLE,
    CARD__BODY,
    CARD__BODY_NULL,
    CARD__SPOT_TYPE,

    GRAPH_LIST_ITEM__MAIN,
    GRAPH_LIST_ITEM__MAIN_NULL,
    GRAPH_LIST_ITEM__SIDE,
    GRAPH_LIST_ITEM__EXPAND,
    GRAPH_LIST_ITEM__ICON,

    PROGRESS_BAR__UPPER_HIGHLIGHT,
    PROGRESS_BAR__UPPER,
    PROGRESS_BAR__LOWER_HIGHLIGHT,
    PROGRESS_BAR__LOWER_HIGHLIGHT_NULL,
    PROGRESS_BAR__LOWER,
    PROGRESS_BAR__TEXT_HIGHLIGHT,
    PROGRESS_BAR__TEXT,


    BUTTON,     //for Cancel, Save button
    BUTTON_NULL,
    //BUTTON__SMALL,   //for dialog button

    FLOATING_BUTTON,

    DIALOG__TITLE,
    DIALOG__BODY,
    DIALOG__BODY_NULL,
    DIALOG__BODY_SMALL,
    DIALOG__BUTTON
}

@Composable
fun getTextStyle(textType: TextType): TextStyle {
    return when(textType){
        TOP_BAR__TITLE              -> MaterialTheme.typography.h1
        TOP_BAR__SUBTITLE           -> MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)

        TRIP_LIST_ITEM__TITLE       -> MaterialTheme.typography.h2
        TRIP_LIST_ITEM__TITLE_NULL  -> MaterialTheme.typography.h2.copy(color = gray)
        TRIP_LIST_ITEM__SUBTITLE    -> MaterialTheme.typography.h6.copy(color = gray)

        CARD__TITLE                 -> MaterialTheme.typography.h6.copy(color = gray)
        CARD__BODY                  -> MaterialTheme.typography.body1
        CARD__BODY_NULL             -> MaterialTheme.typography.subtitle1
        CARD__SPOT_TYPE             -> MaterialTheme.typography.body2

        GRAPH_LIST_ITEM__MAIN       -> MaterialTheme.typography.h4
        GRAPH_LIST_ITEM__MAIN_NULL  -> MaterialTheme.typography.subtitle1
        GRAPH_LIST_ITEM__SIDE       -> MaterialTheme.typography.h6.copy(color = gray)
        GRAPH_LIST_ITEM__EXPAND     -> MaterialTheme.typography.h6.copy(color = gray)
        GRAPH_LIST_ITEM__ICON       -> MaterialTheme.typography.h6.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)

        PROGRESS_BAR__UPPER_HIGHLIGHT       -> MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__UPPER                 -> MaterialTheme.typography.h5
        PROGRESS_BAR__LOWER_HIGHLIGHT       -> MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__LOWER_HIGHLIGHT_NULL  -> MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__LOWER                 -> MaterialTheme.typography.h5
        PROGRESS_BAR__TEXT_HIGHLIGHT        -> MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__TEXT                  -> MaterialTheme.typography.h6.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)

        BUTTON                      -> MaterialTheme.typography.body2
        BUTTON_NULL                 -> MaterialTheme.typography.subtitle2

        FLOATING_BUTTON             -> MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)

        DIALOG__TITLE               -> MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Bold)
        DIALOG__BODY                -> MaterialTheme.typography.body1
        DIALOG__BODY_NULL           -> MaterialTheme.typography.subtitle1
        DIALOG__BODY_SMALL          -> MaterialTheme.typography.body2
        DIALOG__BUTTON              -> MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
    }
}
