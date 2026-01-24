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

    val tripTextData: String = "",

    /**
     * 0: blur / 1: not blur
     */
    val backgroundAssetUris: List<Uri?> = listOf(null, null),
    /**
     * 0: sticker image and text / 1: sticker text
     */
    val stickerAssetUris: List<Uri?> = listOf(null, null),

    val selectedBackgroundAssetUri: Uri? = null,
    val selectedStickerAssetUri: Uri? = null,

    val useBackground: Boolean = true,
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





    fun initStickerAsserUri(size: Int){
        val newStickerAssetUris = List(size) { null as Uri? }

        _shareTripUiState.update {
            it.copy(stickerAssetUris = newStickerAssetUris)
        }
    }

    fun setBackgroundAssetUri(backgroundBlurredAssetUri: Uri, index: Int){
        val newBackgroundAssetUris = _shareTripUiState.value.backgroundAssetUris.toMutableList()
        newBackgroundAssetUris[index] = backgroundBlurredAssetUri

        _shareTripUiState.update {
            it.copy(backgroundAssetUris = newBackgroundAssetUris)
        }
    }

    fun setStickerAssetUri(stickerAssetUri: Uri, index: Int){
        val newStickerAssetUris = _shareTripUiState.value.stickerAssetUris.toMutableList()
        newStickerAssetUris[index] = stickerAssetUri

        _shareTripUiState.update {
            it.copy(stickerAssetUris = newStickerAssetUris)
        }
    }

    fun setSelectedAssets(
        backgroundAssetUri: Uri?,
        stickerAssetUri: Uri?
    ){
        _shareTripUiState.update {
            it.copy(
                selectedBackgroundAssetUri = backgroundAssetUri,
                selectedStickerAssetUri = stickerAssetUri
            )
        }
    }

    fun setUseBackground(useBackground: Boolean){
        _shareTripUiState.update {
            it.copy(useBackground = useBackground)
        }
    }



    fun setTripTextData(
        tripTextData: String
    ){
        _shareTripUiState.update {
            it.copy(tripTextData = tripTextData)
        }
    }


    suspend fun createStickerAsset(
        context: Context,
        tripImagePath: String?,
        tripTitle: String?,
        tripStartDate: String?,
        tripEndDate: String?,
    ){
        withContext(Dispatchers.IO) {
            if (tripImagePath != null) {
                initStickerAsserUri(2)
                val tripImageFile = File(context.filesDir, tripImagePath)

                //stickers
                setStickerAssetUri(createStickerWithTextUri(context, tripImageFile, tripTitle, tripStartDate, tripEndDate), 0)
                setStickerAssetUri(createStickerOnlyTextUri(context, tripTitle, tripStartDate, tripEndDate), 1)

                //background
                val backgroundBlurred = createBlurBackgroundUri(context, tripImageFile)
                if (backgroundBlurred != null)
                    setBackgroundAssetUri(backgroundBlurred, 0)

                val backgroundNotBlurred = createNotBlurBackgroundUri(context, tripImageFile)
                if (backgroundNotBlurred != null)
                    setBackgroundAssetUri(backgroundNotBlurred, 1)
            }
            else {
                initStickerAsserUri(1)
                setStickerAssetUri(createStickerOnlyTextUri(context, tripTitle, tripStartDate, tripEndDate), 0)
            }
        }
    }


    fun onClickShareToInstagramStory(
        context: Context,
        instagramLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    ){
        val backgroundAssetUri = _shareTripUiState.value.selectedBackgroundAssetUri
        val stickerAssetUri = _shareTripUiState.value.selectedStickerAssetUri
//        val backgroundAssetUri = null
//        val stickerAssetUri = null

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
            setDataAndType(backgroundAssetUri, "image/jpeg")

            //stickers
            putExtra("interactive_asset_uri", stickerAssetUri)

            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            context.grantUriPermission("com.instagram.android", backgroundAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.grantUriPermission("com.instagram.android", stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // instagramLauncher launch
        instagramLauncher.launch(instagramIntent)
    }

    fun onClickSaveAsImage(

    ): Boolean {
        val backgroundAssetUri = _shareTripUiState.value.selectedBackgroundAssetUri
        val stickerAssetUri = _shareTripUiState.value.selectedStickerAssetUri

        //back + sticker
        return if (backgroundAssetUri != null && stickerAssetUri != null){
            commonImageRepository.saveUriToExternalStorage(backgroundAssetUri) &&
                commonImageRepository.saveUriToExternalStorage(stickerAssetUri)
        }

        //sticker
        else if (stickerAssetUri != null)
            commonImageRepository.saveUriToExternalStorage(stickerAssetUri)
        else
            false
    }

    fun onClickShareMore(
        context: Context,
    ){
        val stickerAssetUri = _shareTripUiState.value.selectedStickerAssetUri

        if (stickerAssetUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*" // MIME 타입
                putExtra(Intent.EXTRA_STREAM, stickerAssetUri)
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












