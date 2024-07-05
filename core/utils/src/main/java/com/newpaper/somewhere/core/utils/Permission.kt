package com.newpaper.somewhere.core.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

//permission list
val USER_LOCATION_PERMISSION_LIST = listOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION
)

const val USER_LOCATION_PERMISSION_FINE = android.Manifest.permission.ACCESS_FINE_LOCATION
val USER_LOCATION_PERMISSION_COARSE = android.Manifest.permission.ACCESS_COARSE_LOCATION

val USER_LOCATION_PERMISSION_ARRAY = arrayOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION
)

//permission launcher
//@Composable
//fun launcher(
//
//):Boolean {
//    var re = false
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissionsMap ->
//        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
//        /** 권한 요청시 동의 했을 경우 **/
//        re = if (areGranted) {
//            Log.d("test", "권한이 동의되었습니다.")
//            true
//        }
//        /** 권한 요청시 거부 했을 경우 **/
//        else {
//            Log.d("test", "권한이 거부되었습니다.")
//            false
//        }
//    }
//
//    return re
//}

/**
 * check permission is granted
 *
 * @param context
 * @param permission what you will checked permission
 * @return true: permission is granted
 *         false: permission is denied
 */
fun checkPermission(
    context: Context,
    permission: String
): Boolean{
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

}

fun checkPermissions(
    context: Context,
    permissions: Array<String>
): Boolean{
    return permissions.all{
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * check user location permission is granted
 *
 * @param context
 * @return true: user location permission is granted,
 *         false: user location permission is denied
 */
fun checkPermissionUserLocation(
    context: Context,
): Boolean{
    return checkPermission(context, USER_LOCATION_PERMISSION_COARSE)
            || checkPermission(context, USER_LOCATION_PERMISSION_FINE)
}

fun checkAndRequestPermissionForUserLocation(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    permissionRequestEnd: Boolean,
    permissions: Array<String> = USER_LOCATION_PERMISSION_ARRAY
): Boolean? {   //return permission granted (can use user location)
                //true(granted), false(deny), null(request not end)

    //if permissions granted (PRECISE or APPROXIMATE)
    if (checkPermissionUserLocation(context)){
        return true
    }

    //if not granted(denied), request permissions
    else{
        //launch permission request
        permissionLauncher.launch(permissions)


        if (permissionRequestEnd){
            //check is granted
            return checkPermissionUserLocation(context)
        }
        else{
            return null
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun checkPermissionsIsGranted(
    permissionsState: MultiplePermissionsState
): Boolean {
    return permissionsState.allPermissionsGranted
}

//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun requestPermissions(
//    coroutineScope: CoroutineScope,
//    permissionsState: MultiplePermissionsState
//): Boolean{
//
//    //permissionsState.launchMultiplePermissionRequest()
//
//
//    val requestPermissionLauncher = rememberLauncherForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ){ permissionsMap ->
//            val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
//
//            if (areGranted){
//                Log.d("test", "granted")
//            }
//            else{
//                Log.d("test", "denied")
//            }
//        }
//
//
//    return checkPermissionsIsGranted(permissionsState)
//}

//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun getUserLocationPermission(
//    showToast: Boolean = true
//): Boolean{
//    //user location permission
//    val userLocationPermissionState =
//        rememberMultiplePermissionsState(permissions = USER_LOCATION_PERMISSION_LIST)
//
//    //request
//    val enabled = requestPermissions(userLocationPermissionState)
//
//    if(showToast) {
//        val context = LocalContext.current
//        val toastText = stringResource(id = R.string.toast_location_permission_denied)
//
//        //if location permission denied
//        if (!enabled) {
//            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    return enabled
//}