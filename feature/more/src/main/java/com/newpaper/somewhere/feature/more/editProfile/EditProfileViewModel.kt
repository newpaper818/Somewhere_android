package com.newpaper.somewhere.feature.more.editProfile

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.ImageRepository
import com.newpaper.somewhere.core.data.repository.more.EditProfileRepository
import com.newpaper.somewhere.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

const val MAX_NAME_LENGTH = 30

data class EditProfileUiState(
    val userName: String? = null,
    val userProfileImage: String? = null,

    val isInvalidUserName: Boolean = false,
    val isSaveButtonEnabled: Boolean = true
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val editProfileRepository: EditProfileRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _editProfileUiState = MutableStateFlow(EditProfileUiState())
    val editProfileUiState = _editProfileUiState.asStateFlow()

    fun initEditAccountViewModel(
        userData: UserData
    ){
        _editProfileUiState.update {
            it.copy(
                userName = userData.userName,
                userProfileImage = userData.profileImagePath,
                isInvalidUserName = (userData.userName?.length ?: 0) > MAX_NAME_LENGTH
            )
        }
    }

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
                userProfileImage = newProfileImage
            )
        }
    }

    suspend fun saveProfile(
        userId: String?,
        currentUserName: String?,
        currentProfileImage: String?,

        showSuccessSnackbar: () -> Unit,
        showFailSnackbar: () -> Unit,
        showNoChangedSnackbar: () -> Unit,

        updateUserState: () -> Unit
    ){
        if (userId == null)
            return

        _editProfileUiState.update {
            it.copy(isSaveButtonEnabled = false)
        }

        val newUserName = _editProfileUiState.value.userName
        val newProfileImage = _editProfileUiState.value.userProfileImage

        //save to firestore db
        //check name is same with before
        //if same, do nothing
        //else, save to firestore


        val updateUserName = currentUserName != _editProfileUiState.value.userName
        val updateProfileImage = currentProfileImage != _editProfileUiState.value.userProfileImage


        if (updateUserName || updateProfileImage){
            //upload profile image
            if (updateProfileImage) {
                storageDb.updateProfileImage(
                    userId = userId,
                    imagePath = newProfileImage
                )
            }

            //update firestore db
            firestoreDb.updateUserProfile(
                userId = initialUserData?.userId,
                newUserName = newUserName,
                newProfileImage = newProfileImage,
                onSuccess = {
                    //update user state
                    updateUserState()

                    //show success snackbar
                    showSuccessSnackbar()
                },
                onFailure = {
                    //show error snackbar
                    showFailSnackbar()
                }
            )
        }
        else {
            showNoChangedSnackbar()
        }

        delay(700)

        _editProfileUiState.update {
            it.copy(isSaveButtonEnabled = true)
        }
    }
}