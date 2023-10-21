package com.newpaper.somewhere.ui.settingScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.settingScreenUtils.ItemWithText
import com.newpaper.somewhere.ui.settingScreenUtils.ListGroupCard
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

object AboutDestination: NavigationDestination {
    override val route = "about"
    override var title = ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        topBar = {
            SomewhereTopAppBar(
                title = stringResource(id = R.string.about),
                navigationIcon = MyIcons.back,
                navigationIconOnClick = { navigateUp() }
            )
        }
    ){ paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            item{
                //app icon image
                AppIconImageWithAppName()
                MySpacerColumn(height = 16.dp)
            }

            item{
                //app version
                ListGroupCard {
                    ItemWithText(body1Text = stringResource(id = R.string.version), body2Text = BuildConfig.VERSION_NAME)
                }
            }

            item{
                //app version
                ListGroupCard {
                    ItemWithText(body1Text = stringResource(id = R.string.developer), body2Text = stringResource(id = R.string.developer_name))
                    ItemWithText(body2Text = stringResource(id = R.string.developer_info))
                }
            }
        }
    }
}

@Composable
private fun AppIconImageWithAppName(
    textStyle: TextStyle = getTextStyle(TextType.APP_NAME)
){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.somewhere_app_icon),
            modifier = Modifier
                .size(160.dp)
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            style = textStyle
        )
    }
}