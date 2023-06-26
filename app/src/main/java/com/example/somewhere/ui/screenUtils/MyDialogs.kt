package com.example.somewhere.ui.screenUtils

import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.maxkeppeker.sheets.core.CoreDialog
import com.maxkeppeker.sheets.core.models.CoreSelection
import com.maxkeppeker.sheets.core.models.base.ButtonStyle
import com.maxkeppeker.sheets.core.models.base.SelectionButton
import com.maxkeppeker.sheets.core.models.base.UseCaseState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteOrNotDialog(
    dialogState: UseCaseState,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit
){
    CoreDialog(
        state = dialogState,
        selection = CoreSelection(
            withButtonView = true,
            negativeButton = SelectionButton(
                text = "Cancel",
                type = ButtonStyle.OUTLINED
            ),
            onNegativeClick = onCancelClick,
            positiveButton = SelectionButton(
                text = "Delete",
                type = ButtonStyle.FILLED
            ),
            onPositiveClick = onDeleteClick
        ),
        onPositiveValid = true,
        body = {
            Text(text = "Are you sure to delete?")
        },
    )
}