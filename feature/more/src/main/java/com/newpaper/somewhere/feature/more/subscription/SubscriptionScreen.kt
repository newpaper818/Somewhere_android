package com.newpaper.somewhere.feature.more.subscription

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.ui.card.AppIconWithAppNameProCard
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.more.R
import com.newpaper.somewhere.feature.more.subscription.component.AdditionalDescription
import com.newpaper.somewhere.feature.more.subscription.component.CancelSubscriptionButton
import com.newpaper.somewhere.feature.more.subscription.component.PlansTable
import com.newpaper.somewhere.feature.more.subscription.component.RestorePurchasesButton
import com.newpaper.somewhere.feature.more.subscription.component.SubscribeButton


@Composable
fun SubscriptionRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    internetEnabled: Boolean,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val activity = context as Activity

    subscriptionViewModel.billingClientStartConnection()

    val subscriptionUiState by subscriptionViewModel.subscriptionUiState.collectAsState()


    //TODO: check internet connection
    // prevent button double click
    // cancel subs

    SubscriptionScreen(
        use2Panes = use2Panes,
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        internetEnabled = internetEnabled,

        showErrorPage = subscriptionUiState.showErrorPage,
        isUsingSomewherePro = subscriptionUiState.isUsingSomewherePro,
        formattedPrice = subscriptionUiState.formattedPrice,
        onClickSubscription = {
            subscriptionViewModel.launchBillingFlow(activity)
        },

        navigateUp = navigateUp,
        modifier = modifier,
    )
}

@Composable
private fun SubscriptionScreen(
    use2Panes: Boolean,
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    internetEnabled: Boolean,

    showErrorPage: Boolean,
    isUsingSomewherePro: Boolean,
    formattedPrice: String?,
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
                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = { navigateUp() }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
//                if (showErrorPage){
//                    ErrorPage()
//                }
//                else {
                    SubscriptionPage(
                        isUsingSomewherePro = isUsingSomewherePro,
                        formattedPrice = formattedPrice,
                        onClickSubscription = onClickSubscription
                    )
//                }
            }
        }
    }
}

@Composable
private fun ErrorPage(

){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Text(text = "error")
    }
}

@Composable
private fun SubscriptionPage(
    isUsingSomewherePro: Boolean,
    formattedPrice: String?,
    onClickSubscription: () -> Unit,
){
    val itemModifier = Modifier.widthIn(max = itemMaxWidthSmall)

    AppIconWithAppNameProCard()

    MySpacerColumn(height = 24.dp)

    PlansTable(
        freeTrips = 15,
        proTrips = 200,
        freeInviteFriends = 5,
        proInviteFriends = 50
    )

    MySpacerColumn(height = 24.dp)

    Text(
        text = "is using pro: $isUsingSomewherePro",
        style = MaterialTheme.typography.displayLarge
    )


    SubscribeButton(
        formattedPrice = formattedPrice ?: "",
        onClick = onClickSubscription,
        enabled = !isUsingSomewherePro
    )

    MySpacerColumn(height = 24.dp)

    if (!isUsingSomewherePro) {
        RestorePurchasesButton(
            onClick = { }
        )
    }
    else {
        CancelSubscriptionButton(
            //open google play?
            onClick = { }
        )
    }

    MySpacerColumn(height = 24.dp)

    //cancel on Google play
    //can be change pro features
    AdditionalDescription()
}