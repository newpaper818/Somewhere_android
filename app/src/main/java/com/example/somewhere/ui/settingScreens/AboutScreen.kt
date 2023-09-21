package com.example.somewhere.ui.settingScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screens.SomewhereTopAppBar
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

object AboutDestination: NavigationDestination {
    override val route = "about"
    override var title = "About"
}

@Composable
fun AboutScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){

    Scaffold(
        topBar = {
            SomewhereTopAppBar(
                title = AboutDestination.title,
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
            item{
                //app icon image
                AppIconImageWithAppName()
                MySpacerColumn(height = 32.dp)
            }

            item{
                //app version
                ListCard {
                    OneListItem(titleText = "Version", bodyText = "1.0.1")
                }
            }

            item{
                //app version
                ListCard {
                    OneListItem(titleText = "Developer", bodyText = "Sejong Lee")
                }
            }
        }
    }
}

@Composable
private fun AppIconImageWithAppName(

){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.somewhere_app_icon),
                modifier = Modifier.size(160.dp)
            )

            Text(
                text = stringResource(id = R.string.app_name),
                style = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE)
            )
        }
    }
}

@Composable
private fun OneListItem(
    titleText: String,
    bodyText: String? = null,

    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY)
){
    Card(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
        ) {
            Text(
                text = titleText,
                style = bodyTextStyle
            )

            if(bodyText != null) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = bodyText,
                    style = bodyTextStyle
                )
            }
        }
    }
}