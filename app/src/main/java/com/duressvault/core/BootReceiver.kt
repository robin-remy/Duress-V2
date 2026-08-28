package com.duressvault.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.duressvault.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val lockIntent = Intent(context, MainActivity::class.java)
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(lockIntent)
        }
    }
}
