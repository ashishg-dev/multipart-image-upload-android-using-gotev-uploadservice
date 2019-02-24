package com.test.multipartexample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import net.gotev.uploadservice.UploadService



class NotificationActionsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || !NotificationActions.INTENT_ACTION.equals(intent.getAction())) {
            return;
        }

        if (NotificationActions.ACTION_CANCEL_UPLOAD.equals(intent.getStringExtra(NotificationActions.PARAM_ACTION))) {
            onUserRequestedUploadCancellation(context!!, intent.getStringExtra(NotificationActions.PARAM_UPLOAD_ID));
        }
    }

    private fun onUserRequestedUploadCancellation(context: Context, uploadId: String) {
        Log.e("CANCEL_UPLOAD", "User requested cancellation of upload with ID: $uploadId")
        UploadService.stopUpload(uploadId)
    }
}