package com.newpaper.somewhere.feature.dialog.setSpotType

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun SetSpotTypeDialog(
    initialSpotType: SpotType,
    onDismissRequest: () -> Unit,
    onOkClick: (spotType: SpotType) -> Unit,
    viewModel: SetSpotTypeViewModel = hiltViewModel()
){
    val setSpotTypeUiState by viewModel.setSpotTypeUiState.collectAsStateWithLifecycle()

    SetSpotTypeDialog(
        setSpotTypeUiState = setSpotTypeUiState,
        onChangeSpotTypeGroup = viewModel::updateSpotTypeGroup,
        onChangeSpotType = viewModel::updateSpotType,
        onDismissRequest = onDismissRequest,
        onOkClick = onOkClick
    )
}

@Composable
internal fun SetSpotTypeDialog(
    setSpotTypeUiState: SetSpotTypeUiState,
    onChangeSpotTypeGroup: (spotTypeGroup: SpotTypeGroup) -> Unit,
    onChangeSpotType: (spotType: SpotType) -> Unit,

    onDismissRequest: () -> Unit,
    onOkClick: (spotType: SpotType) -> Unit,
){
    val spotTypeGroupList = enumValues<SpotTypeGroup>()

    val currentSpotTypeGroup = setSpotTypeUiState.currentSpotTypeGroup
    val currentSpotType = setSpotTypeUiState.currentSpotType

    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,

        titleText = stringResource(id = R.string.set_spot_type),
        bodyContent = {

            //spot type group row list
            LazyRow(
                modifier = Modifier
                    //.height(400.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceBright)
                    .padding(0.dp, 8.dp)
            ) {

                item {
                    MySpacerRow(width = 8.dp)
                }

                items(spotTypeGroupList) {
                    val cardColor = if (it == currentSpotTypeGroup) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent

                    val cardTextStyle = if (it == currentSpotTypeGroup) MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        else MaterialTheme.typography.bodyMedium

                    ClickableBox(
                        shape = MaterialTheme.shapes.small,
                        containerColor = cardColor,
                        modifier = Modifier.height(36.dp),
                        onClick = {
                            onChangeSpotTypeGroup(it)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(12.dp, 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = it.textId),
                                style = cardTextStyle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(40.dp)
                            )
                        }
                    }
                    MySpacerRow(width = 8.dp)
                }
            }

            MySpacerColumn(height = 8.dp)



            //spot type list
            Column(
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceBright)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(currentSpotTypeGroup.memberList) {
                        val cardColor = if (it == currentSpotType) MaterialTheme.colorScheme.primaryContainer
                                        else Color.Transparent

                        val cardTextStyle = if (it == currentSpotType) MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            else MaterialTheme.typography.bodyMedium

                        ClickableBox(
                            containerColor = cardColor,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .height(46.dp)
                                .fillMaxWidth(),
                            onClick = {
                                onChangeSpotType(it)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp, 0.dp)
                            ) {
                                Text(
                                    text = stringResource(id = it.textId),
                                    style = cardTextStyle,
                                )
                            }
                        }
                    }
                }
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    onClick = {
                        onOkClick(currentSpotType)
                    }
                )
            }
        }
    )
}
























@Composable
@PreviewLightDark
private fun SetSpotTypeDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            SetSpotTypeDialog(
                setSpotTypeUiState = SetSpotTypeUiState(),
                onChangeSpotTypeGroup = {},
                onChangeSpotType = {},
                onDismissRequest = {},
                onOkClick = {}
            )
        }
    }
}