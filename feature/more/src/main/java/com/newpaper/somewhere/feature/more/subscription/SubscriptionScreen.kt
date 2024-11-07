package com.newpaper.somewhere.feature.more.subscription

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.ui.InternetUnavailableText
import com.newpaper.somewhere.core.ui.card.AppIconWithAppNameProCard
import com.newpaper.somewhere.core.utils.FREE_MAX_INVITE_FRIENDS
import com.newpaper.somewhere.core.utils.FREE_MAX_TRIPS
import com.newpaper.somewhere.core.utils.PLAY_STORE_SUBSCRIPTIONS_URL
import com.newpaper.somewhere.core.utils.PRO_MAX_INVITE_FRIENDS
import com.newpaper.somewhere.core.utils.PRO_MAX_TRIPS
import com.newpaper.somewhere.feature.more.R
import com.newpaper.somewhere.feature.more.subscription.component.ManageSubscriptionButton
import com.newpaper.somewhere.feature.more.subscription.component.NoticeText
import com.newpaper.somewhere.feature.more.subscription.component.OneFreeWeekText
import com.newpaper.somewhere.feature.more.subscription.component.PlansTable
import com.newpaper.somewhere.feature.more.subscription.component.RestorePurchasesButton
import com.newpaper.somewhere.feature.more.subscription.component.SubscribeButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SubscriptionRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    internetEnabled: Boolean,

    updateIsUsingSomewherePro: (isUsingSomewherePro: Boolean) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val activity = context as Activity
    val coroutineScope = rememberCoroutineScope()

    val subscriptionUiState by subscriptionViewModel.subscriptionUiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val snackbarTextError = stringResource(id = R.string.snackbar_error_do_it_later)
    val snackbarTextPurchasesNotFound = stringResource(id = R.string.snackbar_purchases_not_found_on_this_account)


    subscriptionViewModel.billingClientStartConnection(
        showSnackError = {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = snackbarTextError
                )
            }
        }
    )

    LaunchedEffect(subscriptionUiState.showErrorSnackbar) {
        if (subscriptionUiState.showErrorSnackbar){
            snackBarHostState.showSnackbar(
                message = snackbarTextError
            )
        }
    }

    LaunchedEffect(subscriptionUiState.isUsingSomewherePro) {
        updateIsUsingSomewherePro(subscriptionUiState.isUsingSomewherePro)
    }



    SubscriptionScreen(
        use2Panes = use2Panes,
        startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        endSpacerValue = spacerValue,
        internetEnabled = internetEnabled,

        snackBarHostState = snackBarHostState,
        buttonEnabled = subscriptionUiState.buttonEnabled,
        isUsingSomewherePro = subscriptionUiState.isUsingSomewherePro,
        formattedPrice = subscriptionUiState.formattedPrice,
        oneFreeWeekEnabled = subscriptionUiState.oneFreeWeekEnabled,
        onClickSubscription = {
            subscriptionViewModel.launchBillingFlow(
                activity = activity,
                showSnackbarError = {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = snackbarTextError
                        )
                    }
                }
            )
        },
        onClickRestorePurchases = {
            coroutineScope.launch {
                subscriptionViewModel.billingClientStartConnection(
                    onClickRestorePurchases = true,
                    showSnackBarNotPurchased = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = snackbarTextPurchasesNotFound
                            )
                        }
                    },
                    showSnackError = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = snackbarTextError
                            )
                        }
                    }
                )
                delay(2000)
                subscriptionViewModel.setButtonEnabled(true)
            }
        },

        navigateUp = navigateUp,
        setIsUsingSomewhereProDebug = {
            subscriptionViewModel.setIsUsingSomewherePro(it)
            updateIsUsingSomewherePro(it)
        },
        modifier = modifier,
    )
}

@Composable
private fun SubscriptionScreen(
    use2Panes: Boolean,
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    internetEnabled: Boolean,
    snackBarHostState: SnackbarHostState,

    buttonEnabled: Boolean,
    isUsingSomewherePro: Boolean,
    formattedPrice: String?,
    oneFreeWeekEnabled: Boolean,

    onClickSubscription: () -> Unit,
    onClickRestorePurchases: () -> Unit,
    navigateUp: () -> Unit,

    setIsUsingSomewhereProDebug: (isUsingSomewherePro: Boolean) -> Unit,

    modifier: Modifier = Modifier,
) {
    val scaffoldModifier = if (use2Panes) modifier
        else modifier.navigationBarsPadding()

    Scaffold(
        modifier = scaffoldModifier,
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                title = stringResource(id = R.string.subscription),
                startPadding = startSpacerValue,

                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = { navigateUp() }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .width(500.dp)
                    .navigationBarsPadding(),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                AppIconWithAppNameProCard()
            }

            item {
                PlansTable(
                    freeTrips = FREE_MAX_TRIPS,
                    proTrips = PRO_MAX_TRIPS,
                    freeInviteFriends = FREE_MAX_INVITE_FRIENDS,
                    proInviteFriends = PRO_MAX_INVITE_FRIENDS
                )
            }

            item {
                //internet unavailable
                AnimatedVisibility(
                    visible = !internetEnabled,
                    enter = expandVertically(tween(500)) + fadeIn(tween(500, 200)),
                    exit = shrinkVertically(tween(500, 200)) + fadeOut(tween(500))
                ) {
                    Column {
                        InternetUnavailableText()
                        MySpacerColumn(height = 16.dp)
                    }
                }

                //1 week free
                if (oneFreeWeekEnabled && !isUsingSomewherePro){
                    OneFreeWeekText()
                    MySpacerColumn(height = 12.dp)
                }

                //subscription button
                SubscribeButton(
                    formattedPrice = formattedPrice ?: "",
                    onClick = onClickSubscription,
                    enabled = buttonEnabled && internetEnabled,
                    isUsingSomewherePro = isUsingSomewherePro
                )
            }

            item {
                if (!isUsingSomewherePro) {
                    RestorePurchasesButton(
                        onClick = onClickRestorePurchases,
                        enabled = buttonEnabled && internetEnabled
                    )
                }
                else {
                    val uriHandler = LocalUriHandler.current

                    ManageSubscriptionButton(
                        //open google play subscriptions
                        onClick = {
                            uriHandler.openUri(PLAY_STORE_SUBSCRIPTIONS_URL)
                        },
                        enabled = buttonEnabled && internetEnabled
                    )
                }
            }

            item {
                //cancel on Google play
                //can be change pro features
                NoticeText()
            }














//            if(BuildConfig.DEBUG){
//                item {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Text(text = "using Somewhere Pro (debug)")
//
//                        MySpacerRow(width = 12.dp)
//
//                        Switch(
//                            checked = isUsingSomewherePro,
//                            onCheckedChange = {
//                                setIsUsingSomewhereProDebug(!isUsingSomewherePro)
//                            }
//                        )
//                    }
//                }
//            }
        }
    }
}