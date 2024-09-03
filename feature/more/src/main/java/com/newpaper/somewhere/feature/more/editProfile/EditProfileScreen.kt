package com.newpaper.somewhere.feature.more.editProfile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.ChangeProfileImageButton
import com.newpaper.somewhere.core.designsystem.component.button.DeleteProfileImageButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.InternetUnavailableText
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.card.ProfileImage
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.more.R
import kotlinx.coroutines.launch

@Composable
fun EditProfileRoute(
    userData: UserData,
    internetEnabled: Boolean,
    spacerValue: Dp,

    updateUserState: (userData: UserData) -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
){
    LaunchedEffect(Unit) {
        editProfileViewModel.initEditAccountViewModel(userData)
    }

    val editProfileUiState by editProfileViewModel.editProfileUiState.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        if (uri != null) {
            val fileName = editProfileViewModel.saveImageToInternalStorage(
                tripId = 0,
                index = 0,
                uri = uri,
                isProfileImage = true
            )

            if (fileName != null) {
                editProfileViewModel.onUserProfileImageChanged(fileName)
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val successText = stringResource(id = R.string.snackbar_success_update_profile)
    val failText = stringResource(id = R.string.snackbar_fail_update_profile)
    val noChangedText = stringResource(id = R.string.snackbar_no_changed)

    val onClickBackButton = {
        if (editProfileViewModel.checkProfileInfoChanged(userData))
            editProfileViewModel.setShowExitDialog(true)
        else
            navigateUp()

    }

    BackHandler {
        onClickBackButton()
    }


    EditProfileScreen(
        editProfileUiState = editProfileUiState,
        userData = userData,
        spacerValue = spacerValue,
        internetEnabled = internetEnabled,
        showExitDialog = editProfileUiState.showExitDialog,
        setShowExitDialog = editProfileViewModel::setShowExitDialog,
        snackBarHostState = snackBarHostState,
        downloadImage = editProfileViewModel::getImage,
        onClickEditImage = {
            galleryLauncher.launch("image/*")
        },
        onClickDeleteImage = {
            editProfileViewModel.onUserProfileImageChanged(null)
        },
        onUserNameChanged = editProfileViewModel::onUserNameChanged,
        onSaveClick = {
            editProfileViewModel.saveProfile(
                userData = userData,
                showSuccessSnackbar = {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = successText)
                    }
                },
                showFailSnackbar = {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = failText)
                    }
                },
                showNoChangedSnackbar = {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(message = noChangedText)
                    }
                },
                updateUserState = updateUserState
            )
        },
        onClickBack = onClickBackButton,
        navigateUp = navigateUp,
        modifier = modifier
    )
}

@Composable
private fun EditProfileScreen(
    editProfileUiState: EditProfileUiState,
    userData: UserData,
    spacerValue: Dp,
    internetEnabled: Boolean,
    showExitDialog: Boolean,
    setShowExitDialog: (Boolean) -> Unit,

    snackBarHostState: SnackbarHostState,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    onClickEditImage: () -> Unit,
    onClickDeleteImage: () -> Unit,
    onUserNameChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onClickBack: () -> Unit,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier,
){
    MyScaffold(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
            .displayCutoutPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        bottomSaveCancelBarVisible = !showExitDialog,
        onClickCancel = onClickBack,
        onClickSave = onSaveClick,
        saveEnabled = internetEnabled
                && !editProfileUiState.isInvalidUserName
                && editProfileUiState.isSaveButtonEnabled,

        topBar = {
            SomewhereTopAppBar(
                startPadding = spacerValue,
                title = stringResource(id = R.string.edit_profile),
                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = { onClickBack() }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .width(500.dp)
                    .padding(bottom = 60.dp)
                    .imePadding(),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        }
    ) { paddingValues ->

        //dialog
        if (showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { setShowExitDialog(false) },
                onClickDelete = {
                    setShowExitDialog(false)
                    navigateUp()
                }
            )
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(spacerValue, 8.dp, spacerValue, 200.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            //profile image
            item {
                EditableProfileImage(
                    internetEnabled = internetEnabled,
                    userId = userData.userId,
                    profileImage = editProfileUiState.userProfileImagePath,
                    downloadImage = downloadImage,
                    onClickEditImage = onClickEditImage,
                    onClickDeleteImage = onClickDeleteImage,
                    modifier  = Modifier.widthIn(max = itemMaxWidthSmall)
                )
            }



            //user name
            item {
                EditableUserName(
                    userName = editProfileUiState.userName,
                    isInvalidText = editProfileUiState.isInvalidUserName,
                    onUserNameChanged = onUserNameChanged,
                    modifier  = Modifier.widthIn(max = itemMaxWidthSmall)
                )
            }

            //internet unavailable
            item {
                AnimatedVisibility(
                    visible = !internetEnabled,
                    enter = expandVertically(tween(500)) + fadeIn(tween(500, 200)),
                    exit = shrinkVertically(tween(500, 200)) + fadeOut(tween(500))
                ) {
                    InternetUnavailableText()
                }
            }

            //email
            item {
                MySpacerColumn(height = 32.dp)
                AccountInfo(userData = userData)
            }
        }
    }
}

@Composable
private fun EditableProfileImage(
    internetEnabled: Boolean,
    userId: String,
    profileImage: String?,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,
    onClickEditImage: () -> Unit,
    onClickDeleteImage: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        Row {
            MySpacerRow(width = 16.dp)
            Text(
                text = stringResource(id = R.string.profile_image),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        MySpacerColumn(height = 6.dp)

        MyCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                ProfileImage(
                    profileUserId = userId,
                    internetEnabled = internetEnabled,
                    profileImagePath = profileImage,
                    downloadImage = downloadImage
                )

                MySpacerRow(12.dp)

                //edit button
                ChangeProfileImageButton(
                    onClick = onClickEditImage,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 88.dp)
                )

                MySpacerRow(8.dp)

                //delete button
                DeleteProfileImageButton(
                    onClick = onClickDeleteImage,
                    enabled = profileImage != null,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 88.dp)
                )
            }
        }
    }
}

@Composable
private fun AccountInfo(
    userData: UserData,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
){
    Text(
        text = userData.email ?: "no email",
        style = textStyle
    )
    
    MySpacerColumn(height = 6.dp)

    var connectedWithInfoText = stringResource(id = R.string.connected_with) + " "

    userData.providerIds.forEachIndexed { index, providerId ->
        if (index != 0) {
            connectedWithInfoText += ", "
        }
        connectedWithInfoText += providerId.providerName
    }

    Text(
        text = connectedWithInfoText,
        style = textStyle
    )
}

@Composable
private fun EditableUserName(
    userName: String?,
    isInvalidText: Boolean,
    onUserNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val borderColor = if (isInvalidText) CustomColor.outlineError
    else Color.Transparent

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        Row {
            MySpacerRow(width = 16.dp)
            Text(
                text = stringResource(id = R.string.name),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        MySpacerColumn(height = 6.dp)

        MyCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                MyTextField(
                    inputText = userName,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    placeholderText = stringResource(id = R.string.no_name),
                    placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    onValueChange = {
                        onUserNameChanged(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp, 16.dp, 0.dp, 16.dp)
                )

                if (userName == "" || userName == null) {
                    MySpacerRow(width = 16.dp)
                }
                else {
                    IconButton(onClick = { onUserNameChanged("") }) {
                        DisplayIcon(icon = MyIcons.clearInputText)
                    }
                }
            }
        }
    }
}