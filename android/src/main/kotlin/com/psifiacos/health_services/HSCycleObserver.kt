package com.psifiacos.health_services

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class HSCycleObserver(private val registry: ActivityResultRegistry) :
    DefaultLifecycleObserver {
    private val requestPermissionActivityContract = ActivityResultContracts.RequestPermission()
    private lateinit var requestPermissions: ActivityResultLauncher<String>
    lateinit var responseInternal: (permissionsGranted: Boolean) -> Unit


    override fun onCreate(owner: LifecycleOwner) {
        requestPermissions =
            registry.register(
                Constants.registerPermissionsObserver,
                requestPermissionActivityContract
            ) { granted ->
                responseInternal(granted)
            }
    }

    fun launchRequestPermissions(
        permissions: String,
        response: (permissionsGranted: Boolean) -> Unit
    ) {
        responseInternal = response
        requestPermissions.launch(permissions)
    }
}