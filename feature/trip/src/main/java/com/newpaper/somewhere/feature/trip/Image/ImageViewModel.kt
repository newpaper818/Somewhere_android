package com.newpaper.somewhere.feature.trip.Image

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
): ViewModel() {

    fun getImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ) {
        return imageRepository.downloadImage(
            imagePath = imagePath,
            imageUserId = imageUserId,
            result = result
        )
    }
}