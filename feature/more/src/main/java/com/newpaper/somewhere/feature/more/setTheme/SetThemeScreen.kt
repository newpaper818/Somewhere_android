package com.newpaper.somewhere.feature.more.setTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.MapTheme
import com.newpaper.somewhere.core.ui.item.ItemWithRadioButton
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.feature.more.R
import kotlinx.coroutines.launch

@Composable
fun SetThemeRoute(
    spacerValue: Dp,
    theme: Theme,
    updatePreferencesValue: () -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    use2Panes: Boolean = false,

    setThemeViewModel: SetThemeViewModel = hiltViewModel()
){
    val coroutineScope = rememberCoroutineScope()

    SetThemeScreen(
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        theme = theme,
        saveUserPreferences = { appTheme, mapTheme ->
            coroutineScope.launch {
                //save to dataStore
                setThemeViewModel.saveThemePreferences(
                    appTheme, mapTheme
                )

                //update preferences at appViewModel
                updatePreferencesValue()
            }
        },
        navigateUp = navigateUp,
        modifier = modifier,
        use2Panes = use2Panes
    )

}

@Composable
private fun SetThemeScreen(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    theme: Theme,

    saveUserPreferences: (appTheme: AppTheme?, mapTheme: MapTheme?) -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    use2Panes: Boolean
){
    val appTheme = theme.appTheme
    val mapTheme = theme.mapTheme

    val appThemeList = enumValues<AppTheme>()
    val mapThemeList = enumValues<MapTheme>()


    Scaffold(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
            .displayCutoutPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.theme),
                navigationIcon = if (!use2Panes) TopAppBarIcon.back else null,
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
                    for (oneAppTheme in appThemeList){
                        ItemWithRadioButton(
                            isSelected = appTheme == oneAppTheme,
                            text = stringResource(id = oneAppTheme.textId),
                            onItemClick = {
                                if (oneAppTheme != appTheme){
                                    saveUserPreferences(oneAppTheme, null)
                                }
                            }
                        )
                    }
                }
            }

            //setting map theme
            item {
                ListGroupCard(title = stringResource(id = R.string.map_theme)) {
                    for (oneMapTheme in mapThemeList){
                        ItemWithRadioButton(
                            isSelected = mapTheme == oneMapTheme,
                            text = stringResource(id = oneMapTheme.textId),
                            onItemClick = {
                                if (oneMapTheme != mapTheme){
                                    saveUserPreferences(null, oneMapTheme)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}