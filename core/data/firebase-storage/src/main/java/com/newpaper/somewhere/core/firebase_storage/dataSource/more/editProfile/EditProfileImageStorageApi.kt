package com.newpaper.somewhere.core.firebase_storage.dataSource.more.editProfile

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.newpaper.somewhere.core.firebase_common.USERS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

private const val FIREBASE_STORAGE_EDIT_PROFILE_TAG = "Firebase-Storage-EditProfile"

class EditProfileImageStorageApi @Inject constructor(
    @ApplicationContext private var context: Context,
    private val storageDb: FirebaseStorage
): EditProfileImageRemoteDataSource {
    override suspend fun updateProfileImage(
        userId: String,
        imagePath: String?
    ) {
        //delete existed profile image
        deleteExistedProfileImage(userId)

        //upload new profile image
        if (imagePath != null) {
            val storageRef = storageDb.reference
            val imageRef = storageRef.child("$USERS/$userId/$imagePath")

            val file = Uri.fromFile(File(context.filesDir, imagePath))

            val uploadTask = imageRef.putFile(file)
            uploadTask
                .addOnSuccessListener {
                    Log.d(FIREBASE_STORAGE_EDIT_PROFILE_TAG, "success to upload profile image: $imageRef")
                }
                .addOnFailureListener { e ->
                    Log.e(FIREBASE_STORAGE_EDIT_PROFILE_TAG, "fail to upload profile image", e)
                }
        }
    }











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
                            Log.d(FIREBASE_STORAGE_EDIT_PROFILE_TAG, "success to delete profile image $taskSnapshot")
                        }
                        .addOnFailureListener { e ->
                            Log.e(FIREBASE_STORAGE_EDIT_PROFILE_TAG, "fail to delete profile image", e)
                        }
                }
            }
        } catch (e: Exception) {
            Log.e(FIREBASE_STORAGE_EDIT_PROFILE_TAG, "delete profile images fail", e)
        }

    }
}