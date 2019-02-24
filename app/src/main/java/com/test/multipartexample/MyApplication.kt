package com.test.multipartexample

import android.app.Application
import net.gotev.uploadservice.UploadService

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.test.multipartexample";

    }
}