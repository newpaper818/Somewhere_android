package com.newpaper.somewhere.feature.more.subscription

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.more.R


@Composable
fun SubscriptionRoute(
    use2Panes: Boolean,
    spacerValue: Dp,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val activity = context as Activity

    subscriptionViewModel.billingClientStartConnection()

    val subscriptionUiState by subscriptionViewModel.subscriptionUiState.collectAsState()


    //TODO: check internet connection
    // close nav icon
    // prevent button double click

    SubscriptionScreen(
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        use2Panes = use2Panes,

        isUsingSomewherePro = subscriptionUiState.isUsingSomewherePro,
        onClickSubscription = {
            subscriptionViewModel.launchBillingFlow(activity)
        },

        navigateUp = navigateUp,
        modifier = modifier,
    )
}

@Composable
private fun SubscriptionScreen(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    use2Panes: Boolean,

    isUsingSomewherePro: Boolean,
    onClickSubscription: () -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val scaffoldModifier = if (use2Panes) modifier
        else modifier.navigationBarsPadding()

    Scaffold(
        modifier = scaffoldModifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = startSpacerValue,
                title = stringResource(id = R.string.subscription),
                navigationIcon = if (!use2Panes) TopAppBarIcon.back else null,
                onClickNavigationIcon = { navigateUp() }
            )
        }
    ) { paddingValues ->
        val itemModifier = Modifier.widthIn(max = itemMaxWidthSmall)

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(text = "is using pro: $isUsingSomewherePro")
            }

            item {

                Button( onClick = onClickSubscription
                ) {
                    Text(text = "subscription")
                }
            }
        }
    }
}