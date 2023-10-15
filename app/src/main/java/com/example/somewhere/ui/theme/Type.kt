package com.example.somewhere.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.somewhere.ui.theme.TextType.*

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),

    //headline bold
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),

    //title bold    app bar title
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),

    //body not bold
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    //label not bold    button, icon text
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall =TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)

//val Typography = Typography(
//    h1 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Bold,
//        fontSize = 24.sp
//    ),
//    h2 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp
//    ),
//    h3 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 20.sp
//    ),
//    h4 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 18.sp
//    ),
//    h5 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        color = gray,
//        fontSize = 16.sp
//    ),
//    h6 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 14.sp
//    ),
//
//    subtitle1 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        color = gray,
//        fontSize = 18.sp
//    ),
//    subtitle2 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        color = gray,
//        fontSize = 16.sp
//    ),
//
//    body1 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 18.sp
//    ),
//    body2 = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    )
//)

enum class TextType{

    TOP_BAR__TITLE,
    TOP_BAR__SUBTITLE,

    NAVIGATION_BAR_SELECTED,
    NAVIGATION_BAR_NOT_SELECTED,

    // TRIP ========================================================================================
    TRIP_LIST_ITEM__TITLE,
    TRIP_LIST_ITEM__TITLE_NULL,
    TRIP_LIST_ITEM__SUBTITLE,

    CARD__TITLE,
    CARD__TITLE_ERROR,
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
    PROGRESS_BAR__ICON_TEXT_HIGHLIGHT,
    PROGRESS_BAR__ICON_TEXT,


    BUTTON,     //for Cancel, Save button
    BUTTON_NULL,
    //BUTTON__SMALL,   //for dialog button

    FAB,

    DIALOG__TITLE,
    DIALOG__BODY,
    DIALOG__BODY_NULL,
    DIALOG__BODY_SMALL,
    DIALOG__BUTTON,





    // SETTING =====================================================================================
    MAIN,
    APP_NAME,
    GROUP_CARD_TITLE,
    GROUP_CARD_ITEM_BODY1,
    GROUP_CARD_ITEM_BODY2




}

@Composable
fun getTextStyle(textType: TextType): TextStyle {
    return when(textType){
        TOP_BAR__TITLE              -> MaterialTheme.typography.titleLarge
        TOP_BAR__SUBTITLE           -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)

        NAVIGATION_BAR_SELECTED     -> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        NAVIGATION_BAR_NOT_SELECTED -> MaterialTheme.typography.labelMedium

        // TRIP ====================================================================================
        TRIP_LIST_ITEM__TITLE       -> MaterialTheme.typography.titleMedium
        TRIP_LIST_ITEM__TITLE_NULL  -> MaterialTheme.typography.titleMedium.copy(color = n60)
        TRIP_LIST_ITEM__SUBTITLE    -> MaterialTheme.typography.bodySmall.copy(color = n60)

        CARD__TITLE                 -> MaterialTheme.typography.bodySmall.copy(color = n60)
        CARD__TITLE_ERROR           -> MaterialTheme.typography.bodySmall.copy(color = e60)
        CARD__BODY                  -> MaterialTheme.typography.bodyLarge
        CARD__BODY_NULL             -> MaterialTheme.typography.bodyLarge.copy(color = n60)
        CARD__SPOT_TYPE             -> MaterialTheme.typography.labelLarge

        GRAPH_LIST_ITEM__MAIN       -> MaterialTheme.typography.bodyLarge
        GRAPH_LIST_ITEM__MAIN_NULL  -> MaterialTheme.typography.bodyLarge.copy(color = n60)
        GRAPH_LIST_ITEM__SIDE       -> MaterialTheme.typography.bodySmall.copy(color = n60)
        GRAPH_LIST_ITEM__EXPAND     -> MaterialTheme.typography.bodySmall.copy(color = n60)
        GRAPH_LIST_ITEM__ICON       -> MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)

        PROGRESS_BAR__UPPER_HIGHLIGHT       -> MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__UPPER                 -> MaterialTheme.typography.bodySmall.copy(color = n60)
        PROGRESS_BAR__LOWER_HIGHLIGHT       -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__LOWER_HIGHLIGHT_NULL  -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = n60)
        PROGRESS_BAR__LOWER                 -> MaterialTheme.typography.bodySmall
        PROGRESS_BAR__ICON_TEXT_HIGHLIGHT   -> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        PROGRESS_BAR__ICON_TEXT             -> MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)

        BUTTON                      -> MaterialTheme.typography.labelLarge
        BUTTON_NULL                 -> MaterialTheme.typography.labelLarge.copy(color = n60)

        FAB                         -> MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

        DIALOG__TITLE               -> MaterialTheme.typography.headlineSmall
        DIALOG__BODY                -> MaterialTheme.typography.bodyLarge
        DIALOG__BODY_NULL           -> MaterialTheme.typography.bodyLarge.copy(color = n60)
        DIALOG__BODY_SMALL          -> MaterialTheme.typography.bodyMedium
        DIALOG__BUTTON              -> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)

        // SETTING =================================================================================
        MAIN                        -> MaterialTheme.typography.headlineMedium
        APP_NAME                    -> MaterialTheme.typography.headlineMedium
        GROUP_CARD_TITLE            -> MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = n60)
        GROUP_CARD_ITEM_BODY1       -> MaterialTheme.typography.bodyLarge
        GROUP_CARD_ITEM_BODY2       -> MaterialTheme.typography.bodyMedium.copy(color = n60)
    }
}
