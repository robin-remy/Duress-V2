package com.duressvault.admin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object WipeManager {

    suspend fun performWipe(context: Context) = withContext(Dispatchers.IO) {
        if (isDeviceOwner(context)) {
            performFullDeviceWipe(context)
        } else {
            performAppDataWipe(context)
        }
    }

    private fun performFullDeviceWipe(context: Context) {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(context, DuressAdminReceiver::class.java)
        if (dpm.isAdminActive(adminComponent)) {
            dpm.wipeData(
                DevicePolicyManager.WIPE_EXTERNAL_STORAGE or
                        DevicePolicyManager.WIPE_RESET_PROTECTION_DATA
            )
        } else {
            throw IllegalStateException("Device Owner no activo")
        }
    }

    private fun performAppDataWipe(context: Context) {
        // Borrar SharedPreferences
        context.getSharedPreferences("duress_pins", Context.MODE_PRIVATE).edit().clear().commit()
        // Borrar bases de datos
        context.deleteDatabase("duress.db")
        // Borrar archivos internos
        deleteRecursively(context.filesDir)
        // Borrar caché
        deleteRecursively(context.cacheDir)
        // Borrar directorio externo de la app
        context.getExternalFilesDir(null)?.let { deleteRecursively(it) }
        // Matar proceso
        Process.killProcess(Process.myPid())
    }

    private fun deleteRecursively(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteRecursively(it) }
        }
        file.delete()
    }

    fun isDeviceOwner(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isDeviceOwnerApp(context.packageName)
    }
}
