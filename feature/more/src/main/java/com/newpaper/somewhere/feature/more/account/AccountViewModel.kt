package com.newpaper.somewhere.feature.more.account

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import com.newpaper.somewhere.core.data.repository.signIn.UserRepository
import com.newpaper.somewhere.core.model.enums.ProviderId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getImageRepository: GetImageRepository
): ViewModel() {
    
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

    suspend fun signOut(
        providerIdList: List<ProviderId>,
        signOutResult: (isSignOutSuccess: Boolean) -> Unit
    ){
        userRepository.signOut(
            providerIdList = providerIdList,
            signOutResult = signOutResult
        )
    }
}