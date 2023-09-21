package com.example.somewhere.ui.settingScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screens.SomewhereTopAppBar
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

object MainSettingDestination: NavigationDestination {
    override val route = "mainSetting"
    override var title = "Settings"
}

@Composable
fun MainSettingScreen(
    navigateTo: (NavigationDestination) -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier
){

    Scaffold(
        topBar = {
            SomewhereTopAppBar(
                title = MainSettingDestination.title,
                navigationIcon = MyIcons.back,
                navigationIconOnClick = { navigateUp() }
            )
        }
    ){ paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
                .background(MaterialTheme.colors.background)
        ) {

            //setting
            item {
                ListCard {
                    //date time format
                    OneListItem(
                        itemText = stringResource(id = R.string.date_time_format),
                        onItemClick = { navigateTo(SetDateFormatDestination) }
                    )

                    MyDivider()

                    //?
                    OneListItem(
                        itemText = "Theme",
                        onItemClick = { }
                    )
                }
            }

            //feedback and bug report
            item {
                val uriHandler = LocalUriHandler.current
                val feedbackUri = stringResource(id = R.string.feedback_uri)
                val bugReportUri = stringResource(id = R.string.bug_report_uri)

                ListCard {
                    //send feedback - open web browser to google form
                    OneListItem(
                        itemText = stringResource(id = R.string.send_feedback),
                        onItemClick = { uriHandler.openUri(feedbackUri) },
                        isOpenInNew = true
                    )

                    MyDivider()

                    //bug report - open web browser to google form
                    OneListItem(
                        itemText = stringResource(id = R.string.bug_report),
                        onItemClick = { uriHandler.openUri(bugReportUri) },
                        isOpenInNew = true
                    )
                }
            }

            //about
            item {
                ListCard {
                    //send feedback
                    OneListItem(
                        itemText = "About",
                        onItemClick = { navigateTo(AboutDestination) }
                    )
                }
            }
        }
    }
}

@Composable
fun ListCard(
    content: @Composable() (() -> Unit)
) {
    Card(
        elevation = 0.dp,
        backgroundColor = getColor(ColorType.CARD),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OneListItem(
    itemText: String,
    onItemClick: () -> Unit,
    isOpenInNew: Boolean = false,
    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY)
){
    Card(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(60.dp),
        onClick = onItemClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = itemText,
                style = textStyle
            )
            
            if (isOpenInNew){
                MySpacerRow(width = 8.dp)
                
                DisplayIcon(icon = MyIcons.openInNew)
            }
        }
    }
}