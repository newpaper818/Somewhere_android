package com.newpaper.somewhere.core.local_image_file.dataSource

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.extractTripIdFromImagePath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.sqrt

private const val LOCAL_IMAGE_TAG = "Local-Storage-Image"

private const val IMAGE_MAX_SIZE_MB = 2.3    //Mebibyte
private const val PROFILE_IMAGE_MAX_SIZE_MB = 0.05    //Mebibyte

class ImageLocalApi @Inject constructor(
    @ApplicationContext private val context: Context
):ImageLocalDataSource {
    override fun saveImageToInternalStorage(
        tripId: Int,
        index: Int,
        uri: Uri,
        isProfileImage: Boolean
    ): String? {
        //convert uri to bitmap
        val bitmap = getBitMapFromUri(uri)

        //compress bitmap
        val newBitmap = compressBitmap(isProfileImage, bitmap, uri)

        //make file name : tripId date time index
        val fileName = getImageFileName(isProfileImage, tripId, index) + "jpg"

        //save
        return try{
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { stream ->
                val quality = if (isProfileImage) 80 else 90

                if (!newBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)){
                    throw IOException("Couldn't save bitmap")
                }
            }
            fileName

        } catch(e: IOException){
            e.printStackTrace()
            null
        }
    }

    override fun saveImageToExternalStorage(
        imageFileName: String
    ): Boolean {
        val internalImageFile = File(context.filesDir, imageFileName)

        if (!internalImageFile.exists())
            return false

        val inputStream: InputStream = FileInputStream(internalImageFile)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val imageCollection =
            if (Build.VERSION.SDK_INT >= 29) MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName.substringAfter("_"))
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Somewhere")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        return try {
            context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream == null || !bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create Media store entry")
            true
        }
        catch (e:IOException){
            e.printStackTrace()
            false
        }
    }

    override fun saveUriToExternalStorage(
        uri: Uri
    ): Boolean {
        return try {
            // 1. Uri → Bitmap 변환
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: throw IOException("Couldn't decode bitmap from Uri")

            // 2. 저장 경로 지정 (Android Q 이상과 이하 분리)
            val imageCollection =
                if (Build.VERSION.SDK_INT >= 29) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            // 3. 저장할 파일명
            val fileName = getImageFileName(false, 0, 0).substringAfter("_") + ".png"

            // 4. MediaStore에 메타데이터 설정
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Somewhere")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }

            // 5. 저장 시도
            context.contentResolver.insert(imageCollection, contentValues)?.also { savedUri ->
                context.contentResolver.openOutputStream(savedUri).use { outputStream ->
                    if (outputStream == null || !bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        throw IOException("Couldn't save PNG bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create Media store entry")

            true // 성공
        } catch (e: IOException) {
            e.printStackTrace()
            false // 실패
        }
    }

    override fun deleteFilesFromInternalStorage(
        files: List<String>
    ){
        files.forEach {
            deleteFileFromInternalStorage(it)
        }
    }

    override fun deleteAllImagesFromInternalStorage(

    ){
        Log.d(LOCAL_IMAGE_TAG, "delete all images from local storage")

        try {
            val fileList = context.filesDir.listFiles()

            for (file in fileList!!) {
                if (file.isFile && file.extension.equals("jpg", ignoreCase = true)) {
                    file.delete()
                }
            }

        } catch (e: Exception) {
            Log.e(LOCAL_IMAGE_TAG, "delete all images from local storage fail - ", e)
        }
    }

    override fun deleteUnusedImageFilesForAllTrips(
        allTrips: List<Trip>
    ){
        //all trip id : 1 4 5 8
        //internal image path names(trip id) : 1 4 4 5 8 9
        // -> delete 9

        val allTripIdList = allTrips.map { it.id }

        //get all .jpg files in internal storage
        val internalStorageDir = context.filesDir
        val allImageFiles = internalStorageDir.listFiles { file ->
            file.isFile && file.extension.equals("jpg", ignoreCase = true)
        }

        //filter is not include in [allTripIdList]
        val unusedImageFiles = allImageFiles?.filter {
            "profile_" !in it.name && extractTripIdFromImagePath(it.name) !in allTripIdList
        } ?: listOf()

        //delete unused image files
        deleteFilesFromInternalStorage(
            files = unusedImageFiles.map { it.name }
        )
    }

    override fun deleteUnusedProfileImageFiles(
        usingProfileImage: String?
    ){
        //get all .jpg files in internal storage
        val internalStorageDir = context.filesDir
        val allImageFiles = internalStorageDir.listFiles { file ->
            file.isFile && file.extension.equals("jpg", ignoreCase = true)
        }

        //filter unused profile image
        val unusedImageFiles = allImageFiles?.filter {
            "profile_" in it.name && it.name != usingProfileImage
        } ?: listOf()

        //delete unused image files
        deleteFilesFromInternalStorage(
            files = unusedImageFiles.map { it.name }
        )
    }
























    private fun getBitMapFromUri(
        uri: Uri,
    ): Bitmap {
        return if (Build.VERSION.SDK_INT >= 28) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        else{
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private fun compressBitmap(
        isProfileImage: Boolean,
        bitmap: Bitmap,
        uri: Uri
    ): Bitmap {
        var imageFileSize: Float = 0f
        //TODO -------- no runblocking?
        runBlocking {
            imageFileSize = getFileSizeFromUri(uri)
        }

        val width = bitmap.width
        val height = bitmap.height


        //if is over 2.3 Mebibyte
        var scale: Double = 1.0

        if (isProfileImage && imageFileSize > PROFILE_IMAGE_MAX_SIZE_MB){
            scale = sqrt(PROFILE_IMAGE_MAX_SIZE_MB / imageFileSize)
        }
        else if (imageFileSize > IMAGE_MAX_SIZE_MB){
            scale = sqrt(IMAGE_MAX_SIZE_MB / imageFileSize)
        }


        return if (scale < 1.0){
            if (scale < 0.01f){
                scale = 0.01
            }

            val newBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (width * scale).toInt(),
                (height * scale).toInt(),
                true
            )

            newBitmap
        }
        else
            bitmap
    }

    private fun getFileSizeFromUri(
        uri: Uri
    ): Float {

        var fileSize: Long = 0

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

            cursor.moveToFirst()

            fileSize = cursor.getLong(sizeIndex)
        }

        //Byte to Mebibyte
        return fileSize.toFloat() / 1024 / 1024
    }

    /**
     * return
     * "001_231011_103012157_0" or
     *
     * "profile_231011_103012157"
     */
    private fun getImageFileName(
        isProfileImage: Boolean,
        tripId: Int,
        index: Int
    ): String {
        val df = DecimalFormat("000")
        val now = ZonedDateTime.now(ZoneId.of("UTC"))
        val dateTime = now.format(DateTimeFormatter.ofPattern("yyMMdd_HHmmssSSS"))
        return if (isProfileImage) "profile_${dateTime}"
                else "${df.format(tripId)}_${dateTime}_${index}"
    }

    private fun deleteFileFromInternalStorage(
        filePath: String
    ): Boolean{
        return try {
            context.deleteFile(filePath)
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}