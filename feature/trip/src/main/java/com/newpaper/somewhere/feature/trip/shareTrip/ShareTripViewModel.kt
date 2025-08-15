package com.newpaper.somewhere.feature.trip.shareTrip

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.feature.trip.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class ShareTripUiState(
    val backgroundAssetUri: Uri? = null,
    val stickerAssetUri: Uri? = null,

    val useBackground: Boolean = true,
    val useImage: Boolean = true
)


@HiltViewModel
class ShareTripViewModel @Inject constructor(
    private val commonImageRepository: CommonImageRepository,
): ViewModel(){

    private val _shareTripUiState: MutableStateFlow<ShareTripUiState> =
        MutableStateFlow(
            ShareTripUiState()
        )

    val shareTripUiState = _shareTripUiState.asStateFlow()





    fun setBackgroundAssetUri(backgroundAssetUri: Uri?){
        _shareTripUiState.update {
            it.copy(backgroundAssetUri = backgroundAssetUri)
        }
    }

    fun setStickerAssetUri(stickerAssetUri: Uri?) {
        _shareTripUiState.update {
            it.copy(stickerAssetUri = stickerAssetUri)
        }
    }

    fun setUseBackground(useBackground: Boolean){
        _shareTripUiState.update {
            it.copy(useBackground = useBackground)
        }
    }

    fun setUseImage(useImage: Boolean){
        _shareTripUiState.update {
            it.copy(useImage = useImage)
        }
    }





    suspend fun createAsset(
        context: Context,
        tripImagePath: String?,
        tripTitle: String?,
        tripStartDate: String?,
        tripEndDate: String?,
    ){
        withContext(Dispatchers.IO) {
            if (tripImagePath != null) {
                val tripImageFile = File(context.filesDir, tripImagePath)

                setBackgroundAssetUri(createBlurBackgroundUri(context, tripImageFile))
                setStickerAssetUri(createStickerWithTextUri(context, tripImageFile, tripTitle, tripStartDate, tripEndDate))
            }
            else{
                setStickerAssetUri(createStickerOnlyTextUri(context, tripTitle, tripStartDate, tripEndDate))
            }
        }
    }


    fun onClickShareToInstagramStory(
        context: Context,
        instagramLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    ){
        val instagramIntent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            setDataAndType(null, "image/jpeg")
        }

        // check instagram app installed
        if (!isInstagramInstalled(context)){
            redirectToPlayStoreForInstagram(context)
            return
        }

        val facebookAppId = BuildConfig.FACEBOOK_APP_ID

        // make
        instagramIntent.apply {
            putExtra("source_application", facebookAppId)

            //background
            setDataAndType(_shareTripUiState.value.backgroundAssetUri, "image/jpeg")

            //stickers
            putExtra("interactive_asset_uri", _shareTripUiState.value.stickerAssetUri)

            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.grantUriPermission("com.instagram.android", _shareTripUiState.value.backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.grantUriPermission("com.instagram.android", _shareTripUiState.value.stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // instagramLauncher launch
        instagramLauncher.launch(instagramIntent)
    }

    fun onClickSaveAsImage(

    ): Boolean {
        val stickerUri = _shareTripUiState.value.stickerAssetUri
        return if (stickerUri != null)
            commonImageRepository.saveUriToExternalStorage(stickerUri)
        else
            false
    }

    fun onClickShareMore(
        context: Context,
    ){
        val stickerUri = _shareTripUiState.value.stickerAssetUri
        if (stickerUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*" // MIME 타입
                putExtra(Intent.EXTRA_STREAM, stickerUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                Intent.createChooser(shareIntent, null)
            )
        }
    }






    private fun isInstagramInstalled(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    "com.instagram.android",
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
//            @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo("com.instagram.android", 0)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun redirectToPlayStoreForInstagram(
        context: Context
    ) {
        val appStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.instagram.android"))
        appStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appStoreIntent)
    }

    fun getUriFormFile(
        context: Context,
        filename: String
    ): Uri {
        val imageFile = File(context.filesDir, filename)
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, imageFile)
    }
}












