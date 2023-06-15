package com.example.somewhere.utils

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

//permission list
val USER_LOCATION_PERMISSION_LIST = listOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION
)

@OptIn(ExperimentalPermissionsApi::class)
fun checkPermissionsIsGranted(
    permissionsState: MultiplePermissionsState
): Boolean {
    return permissionsState.allPermissionsGranted
}

@OptIn(ExperimentalPermissionsApi::class)
fun requestPermissions(
    permissionsState: MultiplePermissionsState
){
    permissionsState.launchMultiplePermissionRequest()
}