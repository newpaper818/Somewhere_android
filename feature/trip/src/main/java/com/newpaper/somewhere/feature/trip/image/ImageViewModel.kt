package com.newpaper.somewhere.feature.trip.image

import android.content.Context
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val commonImageRepository: CommonImageRepository,
    private val getImageRepository: GetImageRepository
): ViewModel() {

    fun getImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ) {
        getImageRepository.getImage(
            imagePath = imagePath,
            imageUserId = imageUserId,
            result = result
        )
    }

    fun deleteUnusedProfileImageFiles(
        context: Context,
        usingProfileImage: String?
    ){
        commonImageRepository.deleteUnusedProfileImageFiles(
            usingProfileImage = usingProfileImage
        )
    }
}