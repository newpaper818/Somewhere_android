package com.newpaper.somewhere.ui.screens.moreScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.BuildConfig
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SPACER_BIG
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ItemWithText
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ListGroupCard
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

@Composable
fun AboutScreen(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
    use2Panes: Boolean = false
){
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.about),
                navigationIcon = if (!use2Panes) MyIcons.back else null,
                navigationIconOnClick = { navigateUp() }
            )
        }
    ){ paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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