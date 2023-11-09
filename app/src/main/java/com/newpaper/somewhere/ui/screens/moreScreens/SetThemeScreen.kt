package com.newpaper.somewhere.ui.screens.moreScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.enumUtils.AppTheme
import com.newpaper.somewhere.enumUtils.MapTheme
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ItemWithRadioButton
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ListGroupCard
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SPACER_BIG
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.viewModel.Theme

@Composable
fun SetThemeScreen(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    theme: Theme,
    saveUserPreferences: (appTheme: AppTheme?, mapTheme: MapTheme?) -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
    use2Panes: Boolean = false
){
    var appTheme by rememberSaveable { mutableStateOf(theme.appTheme) }
    var mapTheme by rememberSaveable { mutableStateOf(theme.mapTheme) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.theme),
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

            //setting app theme
            item {
                ListGroupCard(title = stringResource(id = R.string.app_theme)) {
                    SelectAppTheme(
                        selectedAppTheme = appTheme,
                        onClickAppTheme = {
                            if (it != appTheme){
                                appTheme = it
                                saveUserPreferences(it, null)
                            }
                        }
                    )
                }
            }

            //setting map theme
            item {
                ListGroupCard(title = stringResource(id = R.string.map_theme)) {
                    SelectMapTheme(
                        selectedMapTheme = mapTheme,
                        onClickMapTheme = {
                            if (it != mapTheme){
                                mapTheme = it
                                saveUserPreferences(null, it)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectAppTheme(
    selectedAppTheme: AppTheme,
    onClickAppTheme: (AppTheme) -> Unit
){
    val appThemeList = rememberSaveable { enumValues<AppTheme>() }

    for (appTheme in appThemeList){
        ItemWithRadioButton(
            isSelected = selectedAppTheme == appTheme,
            text = stringResource(id = appTheme.textId),
            onItemClick = {
                onClickAppTheme(appTheme)
            }
        )
    }
}

@Composable
private fun SelectMapTheme(
    selectedMapTheme: MapTheme,
    onClickMapTheme: (MapTheme) -> Unit
){
    val mapThemeList = rememberSaveable { enumValues<MapTheme>() }

    for (mapTheme in mapThemeList){
        ItemWithRadioButton(
            isSelected = selectedMapTheme == mapTheme,
            text = stringResource(id = mapTheme.textId),
            onItemClick = {
                onClickMapTheme(mapTheme)
            }
        )
    }
}
