package com.test.multipartexample.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import net.gotev.uploadservice.ServerResponse
import net.gotev.uploadservice.UploadInfo
import net.gotev.uploadservice.UploadServiceBroadcastReceiver

class UploadReceiver : UploadServiceBroadcastReceiver() {

    private var mDelegate: Delegate? = null

    interface Delegate {
        fun onReceive(context: Context?, intent: Intent?)
        fun onProgress(context: Context?, uploadInfo: UploadInfo?)
        fun onError(exception: Exception)
        fun onCompleted(context: Context?, uploadInfo: UploadInfo?, serverResponse: ServerResponse?)
        fun onCancelled(context: Context?, uploadInfo: UploadInfo?)
    }

    fun setDelegate(delegate: Delegate) {
        mDelegate = delegate
    }

    override fun onProgress(context: Context?, uploadInfo: UploadInfo?) {
        super.onProgress(context, uploadInfo)
        mDelegate!!.onProgress(context, uploadInfo)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        mDelegate!!.onReceive(context, intent)
    }

    override fun onError(context: Context?, uploadInfo: UploadInfo?, serverResponse: ServerResponse?, exception: Exception?) {
        super.onError(context, uploadInfo, serverResponse, exception)
        mDelegate!!.onError(exception!!)
    }

    override fun onCompleted(context: Context?, uploadInfo: UploadInfo?, serverResponse: ServerResponse?) {
        super.onCompleted(context, uploadInfo, serverResponse)
        Log.d("Hello",""+serverResponse!!.body)
        mDelegate!!.onCompleted(context, uploadInfo, serverResponse)
    }

    override fun onCancelled(context: Context?, uploadInfo: UploadInfo?) {
        super.onCancelled(context, uploadInfo)
        mDelegate!!.onCancelled(context, uploadInfo)
    }

}