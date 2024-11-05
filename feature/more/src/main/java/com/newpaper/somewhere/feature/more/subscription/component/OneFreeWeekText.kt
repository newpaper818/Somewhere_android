package com.newpaper.somewhere.feature.more.subscription.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.newpaper.somewhere.feature.more.R

@Composable
internal fun OneFreeWeekText(

){
    Text(
        text = stringResource(id = R.string.one_week_free_trial_included),
        style = MaterialTheme.typography.bodyLarge
    )
}