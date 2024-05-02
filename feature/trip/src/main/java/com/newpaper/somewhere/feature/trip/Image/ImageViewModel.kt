package com.newpaper.somewhere.feature.trip.Image

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
): ViewModel() {
    suspend fun getImage(
        imagePath: String,
        profileUserId: String? = null,
        tripManagerId: String? = null
    ): Boolean {
        return imageRepository.downloadImage(
            imagePath = imagePath,
            profileUserId = profileUserId,
            tripManagerId = tripManagerId
        )
    }
}