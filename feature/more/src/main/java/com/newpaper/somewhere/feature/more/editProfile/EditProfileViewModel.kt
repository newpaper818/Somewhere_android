package com.newpaper.somewhere.feature.more.editProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import com.newpaper.somewhere.core.data.repository.more.EditProfileRepository
import com.newpaper.somewhere.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val MAX_NAME_LENGTH = 30

data class EditProfileUiState(
    val userName: String? = null,
    val userProfileImagePath: String? = null,

    val isInvalidUserName: Boolean = false,
    val isSaveButtonEnabled: Boolean = true
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val editProfileRepository: EditProfileRepository,
    private val commonImageRepository: CommonImageRepository,
    private val getImageRepository: GetImageRepository,
): ViewModel() {
    private val _editProfileUiState = MutableStateFlow(EditProfileUiState())
    val editProfileUiState = _editProfileUiState.asStateFlow()



    //==============================================================================================
    //init viewModel ===============================================================================
    fun initEditAccountViewModel(
        userData: UserData
    ){
        _editProfileUiState.update {
            it.copy(
                userName = userData.userName,
                userProfileImagePath = userData.profileImagePath,
                isInvalidUserName = (userData.userName?.length ?: 0) > MAX_NAME_LENGTH
            )
        }
    }



    //==============================================================================================
    //image ========================================================================================
    fun getImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ){
        getImageRepository.getImage(
            imagePath = imagePath,
            imageUserId = imageUserId,
            result = result
        )
    }

    fun saveImageToInternalStorage(
        tripId: Int,
        index: Int,
        uri: Uri,
        isProfileImage: Boolean = true
    ): String? {
        return commonImageRepository.saveImageToInternalStorage(
            tripId = tripId,
            index = index,
            uri = uri,
            isProfileImage = isProfileImage
        )
    }



    //==============================================================================================
    //on edit value ================================================================================
    fun onUserNameChanged(
        newUserName: String
    ){
        _editProfileUiState.update {
            it.copy(
                userName = newUserName.ifEmpty { null },
                isInvalidUserName = newUserName.length > MAX_NAME_LENGTH || newUserName.isEmpty()
            )
        }
    }

    fun onUserProfileImageChanged(
        newProfileImage: String?
    ){
        _editProfileUiState.update {
            it.copy(
                userProfileImagePath = newProfileImage
            )
        }
    }



    //==============================================================================================
    //save =========================================================================================
    fun saveProfile(
        userData: UserData,

        showSuccessSnackbar: () -> Unit,
        showFailSnackbar: () -> Unit,
        showNoChangedSnackbar: () -> Unit,

        updateUserState: (userData: UserData) -> Unit
    ){
        viewModelScope.launch {
            _editProfileUiState.update {
                it.copy(isSaveButtonEnabled = false)
            }

            val newUserName = _editProfileUiState.value.userName
            val newProfileImagePath = _editProfileUiState.value.userProfileImagePath

            //save to firestore db
            //check name is same with before
            //if same, do nothing
            //else, save to firestore

            val updateUserName = userData.userName != newUserName
            val updateProfileImage = userData.profileImagePath != newProfileImagePath


            if (updateUserName || updateProfileImage) {
                //upload profile image
                if (updateProfileImage) {
                    editProfileRepository.updateProfileImage(
                        userId = userData.userId,
                        imagePath = newProfileImagePath
                    )
                }

                //update firestore db
                editProfileRepository.updateUserProfile(
                    appUserId = userData.userId,
                    newUserName = newUserName,
                    newProfileImagePath = newProfileImagePath,
                    onSuccess = {
                        //update user state
                        updateUserState(userData.copy(userName = newUserName, profileImagePath = newProfileImagePath))

                        //show success snackbar
                        showSuccessSnackbar()
                    },
                    onFailure = {
                        //show error snackbar
                        showFailSnackbar()
                    }
                )
            } else {
                showNoChangedSnackbar()
            }

            delay(700)

            _editProfileUiState.update {
                it.copy(isSaveButtonEnabled = true)
            }
        }
    }
}