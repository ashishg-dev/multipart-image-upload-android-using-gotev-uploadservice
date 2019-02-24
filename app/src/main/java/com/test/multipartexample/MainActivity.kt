package com.test.multipartexample

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.test.multipartexample.receiver.NotificationActions
import com.test.multipartexample.receiver.UploadReceiver
import kotlinx.android.synthetic.main.activity_main.*
import net.gotev.uploadservice.*
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CAMERA = 1
    private val MY_PERMISSIONS_REQUEST_STORAGE = 2
    private val MY_PERMISSIONS_REQUEST_CAMERA_COPY = 3
    private val MY_PERMISSIONS_REQUEST_STORAGE_COPY = 4
    private lateinit var uploadReceiver: UploadReceiver
    private var file_name = ""
    private var uploadId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadId = UUID.randomUUID().toString()
        file_name = uploadId + ".png"

        button.setOnClickListener {
            selectImage()
        }

        uploadReceiver = UploadReceiver()

        uploadReceiver.setDelegate(object : UploadReceiver.Delegate {

            override fun onProgress(context: Context?, uploadInfo: UploadInfo?) {
                Toast.makeText(this@MainActivity, "Progress", Toast.LENGTH_SHORT).show()
            }


            override fun onCancelled(context: Context?, uploadInfo: UploadInfo?) {
                Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onCompleted(context: Context?, uploadInfo: UploadInfo?, serverResponse: ServerResponse?) {
                try {

                    val jsonObject = JSONObject(String(serverResponse!!.body, charset("UTF-8")))
                    Toast.makeText(this@MainActivity, "Completed", Toast.LENGTH_SHORT).show()
                    Log.d("profile_pic", jsonObject.toString())


                } catch (e: java.lang.Exception) {
                    Log.d("profile_pic", e.message)
                }

            }

            override fun onError(exception: java.lang.Exception) {
                Toast.makeText(this@MainActivity, exception.message.toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onReceive(context: Context?, intent: Intent?) {

            }


        })

    }

    override fun onResume() {
        super.onResume()
        uploadReceiver.register(this)
    }

    override fun onPause() {
        super.onPause()
        uploadReceiver.unregister(this)
    }

    private fun selectImage() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set Profile Photo")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {

                if (checkCameraPermission(MY_PERMISSIONS_REQUEST_CAMERA)) {
                    startCamera()
                }

            } else if (options[item] == "Choose from Gallery") {

                if (checkStoragePermission(MY_PERMISSIONS_REQUEST_STORAGE)) {
                    openGallery()
                }

            }
        }

        builder.show()

    }

    private fun checkCameraPermission(requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // we can request the permission.
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.CAMERA),
                requestCode
            )
            return false
        } else {
            return true
        }
    }

    private fun checkStoragePermission(requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(baseContext, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // we can request the permission.
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode
            )
            return false
        } else {
            return true
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 1)
    }

    private fun openGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, 2)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                }
            }
            MY_PERMISSIONS_REQUEST_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()

                }
            }

            MY_PERMISSIONS_REQUEST_STORAGE_COPY -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) { // camera

                /*                Bitmap bitmap;

                File f = new File(Environment.getExternalStorageDirectory() + "/.FNF/");

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);*/

                val photo = data!!.extras!!.get("data") as Bitmap
                imageViewProfilePic.setImageBitmap(photo)
                createFolder(photo)

            } else if (requestCode == 2) { //gallery
                val selectedImage = data!!.data
                imageViewProfilePic.setImageURI(selectedImage)
                var photo: Bitmap? =
                    null
                if (selectedImage != null) {
                    // compress the image

                    try {
                        //                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        val options = BitmapFactory.Options()
                        options.inSampleSize = 5
                        var fileDescriptor: AssetFileDescriptor? = null
                        try {
                            fileDescriptor = baseContext.contentResolver.openAssetFileDescriptor(
                                selectedImage,
                                "r"
                            )
                        } catch (e: FileNotFoundException) {
                            Toast.makeText(baseContext, "" + e.message, Toast.LENGTH_SHORT).show()
                        } finally {
                            try {
                                photo = BitmapFactory.decodeFileDescriptor(
                                    fileDescriptor!!.fileDescriptor,
                                    null, options
                                )
                                fileDescriptor.close()
//                                MyAsyncTaskForBase64().execute(photo)
                            } catch (e: IOException) {
                                Toast.makeText(baseContext, "" + e.message, Toast.LENGTH_SHORT).show()
                            }

                        }
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(baseContext, "" + e.message, Toast.LENGTH_SHORT).show()
                    }

                    createFolder(photo!!)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Path not found", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createFolder(image: Bitmap) {

        var dir = File(Environment.getExternalStorageDirectory().toString() + "/.ashish");
        try {
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    createProfileImage(image);
                } else {
                    Toast.makeText(
                        this, "Error while creating file",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                createProfileImage(image);
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show();
        }
    }

    // create image into our directory
    private fun createProfileImage(image: Bitmap) {
        val fileDP = File(
            Environment.getExternalStorageDirectory().toString() + "/.ashish/",
            file_name
        )
        try {
            val fos = FileOutputStream(fileDP);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush()
            fos.close()
/*            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 90, bos);
            byte[] bb = bos.toByteArray();
            Log.d(TAG, "createProfileImage: " + Base64.encodeToString(bb, Base64.DEFAULT));*/
            uploadMultipart()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show();
        }
    }

    private fun uploadMultipart() {
        try {

            MultipartUploadRequest(this, uploadId, ConstantKey.SERVERURL)
                .addFileToUpload(
                    Environment.getExternalStorageDirectory().toString() + "/.ashish/" + file_name,
                    "file", file_name
                )
                .addHeader("Content-Type", ConstantKey.CONTENT_TYPE)
                .addHeader("Authorization", ConstantKey.AUTHORIZATION)
                .addParameter("employeeid", ConstantKey.EMPLOYEEID)
                .setNotificationConfig(getNotificationConfig(uploadId))
                .setMaxRetries(2)
                .startUpload()

        } catch (exc: Exception) {
            Toast.makeText(this, "" + exc.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getNotificationConfig(uplodId: String): UploadNotificationConfig {

        val config = UploadNotificationConfig()
        val clickIntent = PendingIntent.getActivity(
            this, 1, Intent(this, SplashScreenActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        config.setTitleForAllStatuses("Profile Upload")
            .setRingToneEnabled(true)
            .setClickIntentForAllStatuses(clickIntent)
            .setClearOnActionForAllStatuses(true)

        config.getProgress().message = "Uploading"
        config.getProgress().iconResourceID = android.R.drawable.ic_dialog_alert
        config.getProgress().iconColorResourceID = Color.BLUE
        config.getProgress().actions.add(
            UploadNotificationAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Cancel", NotificationActions.getCancelUploadAction(
                    this, 1,
                    uploadId
                )
            )
        );

        config.getCompleted().message = "Success"
        config.getCompleted().iconResourceID = android.R.drawable.ic_dialog_alert
        config.getCompleted().iconColorResourceID = Color.GREEN;

        config.getError().message = "Error"
        config.getError().iconResourceID = android.R.drawable.ic_menu_close_clear_cancel
        config.getError().iconColorResourceID = Color.RED;

        config.getCancelled().message = "Cancelled"
        config.getCancelled().iconResourceID = R.drawable.abc_ic_arrow_drop_right_black_24dp
        config.getCancelled().iconColorResourceID = Color.YELLOW;

        return config
    }


}
