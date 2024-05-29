package com.newpaper.somewhere.core.firebase_storage.dataSource.image

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.newpaper.somewhere.core.firebase_common.TRIP
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.utils.extractTripIdFromImagePath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

private const val FIREBASE_STORAGE_IMAGE_TAG = "Firebase-Storage-Image"

class ImageStorageApi @Inject constructor(
    @ApplicationContext private var context: Context,
    private val storageDb: FirebaseStorage
): ImageRemoteDataSource {

    override fun downloadTripImage(
        tripManagerId: String,
        imagePath: String,
        result: (Boolean) -> Unit
    ){
        val tripId = extractTripIdFromImagePath(imagePath)

        if (tripId != null) {
            val storageRef = storageDb.reference
            val imageRef = storageRef.child("$USERS/$tripManagerId/$TRIP$tripId/$imagePath")

            val imageUri = Uri.fromFile(File(context.filesDir, imagePath))

            imageRef.getFile(imageUri)
                .addOnSuccessListener {
                    Log.d(FIREBASE_STORAGE_IMAGE_TAG, "download trip image success - imageRef: $imageRef")
                    result(true)
                }
                .addOnFailureListener{
                    Log.e(
                        FIREBASE_STORAGE_IMAGE_TAG,
                        "download trip image fail - imageRef: $imageRef",
                        it
                    )
                    result(false)
                }
        }
        else {
            Log.e(
                FIREBASE_STORAGE_IMAGE_TAG,
                "download trip image fail - trip id is null - imagePath: $imagePath"
            )
            result(false)
        }
    }

    override fun downloadProfileImage(
        profileUserId: String,
        imagePath: String,
        result: (Boolean) -> Unit
    ){
        val storageRef = storageDb.reference
        val imageRef = storageRef.child("$USERS/$profileUserId/$imagePath")

        val imageUri = Uri.fromFile(File(context.filesDir, imagePath))

        imageRef.getFile(imageUri)
            .addOnSuccessListener {
                Log.d(FIREBASE_STORAGE_IMAGE_TAG, "download profile image - imageRef: $imageRef")
                result(true)
            }
            .addOnFailureListener{
                Log.e(FIREBASE_STORAGE_IMAGE_TAG, "download profile image fail - imageRef: $imageRef", it)
                result(false)
            }
    }

    override fun uploadTripImages(
        tripManagerId: String,
        imagePaths: List<String>
    ){
        for (imagePath in imagePaths){
            val tripId = extractTripIdFromImagePath(imagePath)

            if (tripId != null) {
                uploadTripImage(
                    tripManagerId = tripManagerId,
                    tripId = tripId,
                    imagePath = imagePath
                )
            }
            else {
                Log.e(
                    FIREBASE_STORAGE_IMAGE_TAG,
                    "upload trip image fail - trip id is null - imagePath: $imagePath"
                )
            }
        }
    }

    override suspend fun updateProfileImage(
        userId: String,
        imagePath: String?
    ){
        //delete existed profile image
        deleteExistedProfileImage(userId)

        //upload new profile image
        if (imagePath != null) {
            uploadProfileImage(
                userId = userId,
                imagePath = imagePath
            )
        }
    }

    override fun deleteTripImages(
        tripManagerId: String,
        imagePaths: List<String>
    ){
        for (imagePath in imagePaths){
            val tripId = extractTripIdFromImagePath(imagePath)

            if (tripId != null) {
                deleteTripImage(
                    tripManagerId = tripManagerId,
                    tripId = tripId,
                    imagePath = imagePath
                )
            }
            else {
                Log.e(
                    FIREBASE_STORAGE_IMAGE_TAG,
                    "delete trip image fail - trip id is null - imagePath: $imagePath"
                )
            }
        }
    }














    //upload trip image to firebase storage
    private fun uploadTripImage(
        tripManagerId: String,
        tripId: Int,
        imagePath: String,
    ){
        val storageRef = storageDb.reference
        val imageRef = storageRef.child("$USERS/$tripManagerId/$TRIP$tripId/$imagePath")

        val imageUri = Uri.fromFile(File(context.filesDir, imagePath))

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask
            .addOnSuccessListener {
                Log.d(FIREBASE_STORAGE_IMAGE_TAG, "upload trip image - imageRef: $imageRef")
            }
            .addOnFailureListener{ e ->
                Log.e(FIREBASE_STORAGE_IMAGE_TAG, "upload trip image fail - imageRef: $imageRef", e)
            }
    }

    //upload profile image to firebase storage
    private fun uploadProfileImage(
        userId: String,
        imagePath: String,
    ){
        val storageRef = storageDb.reference
        val imageRef = storageRef.child("$USERS/$userId/$imagePath")

        val imageUri = Uri.fromFile(File(context.filesDir, imagePath))

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask
            .addOnSuccessListener {
                Log.d(FIREBASE_STORAGE_IMAGE_TAG, "upload profile image - imagePath: $imageRef")
            }
            .addOnFailureListener { e ->
                Log.e(FIREBASE_STORAGE_IMAGE_TAG, "upload profile image fail - imagePath: $imageRef", e)
            }
    }

    //delete existed profile image
    private suspend fun deleteExistedProfileImage(
        userId: String
    ){
        val storageRef = storageDb.reference
        val tripFolderRef = storageRef.child("$USERS/$userId")

        try {
            val listResult = tripFolderRef.listAll().await()
            val imageRefs = listResult.items

            for (imageRef in imageRefs) {

                if ("profile_" in imageRef.name) {

                    val deleteImageRef = storageRef.child("$USERS/$userId/${imageRef.name}")

                    deleteImageRef.delete()
                        .addOnSuccessListener { taskSnapshot ->
                            Log.d(FIREBASE_STORAGE_IMAGE_TAG, "delete profile image $taskSnapshot")
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                FIREBASE_STORAGE_IMAGE_TAG,
                                "delete profile image fail - imageRef: $imageRef",
                                e
                            )
                        }
                }
            }
        } catch (e: Exception) {
            Log.e(FIREBASE_STORAGE_IMAGE_TAG, "delete profile images fail", e)
        }

    }

    //delete trip image
    private fun deleteTripImage(
        tripManagerId: String,
        tripId: Int,
        imagePath: String,
    ){
        val storageRef = storageDb.reference
        val imageRef = storageRef.child("$USERS/$tripManagerId/$TRIP$tripId/$imagePath")

        imageRef.delete()
            .addOnSuccessListener { taskSnapshot ->
                Log.d(FIREBASE_STORAGE_IMAGE_TAG, "delete trip image $taskSnapshot")
            }
            .addOnFailureListener{e ->
                Log.e(FIREBASE_STORAGE_IMAGE_TAG, "delete trip image fail", e)
            }
    }
}