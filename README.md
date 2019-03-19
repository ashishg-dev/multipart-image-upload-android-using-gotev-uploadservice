# multipart-image-upload-android-using-gotev-uploadservice

<b>Features.</b><br/><br/>
1 - Easy and small library.</br>
2 - Upload file/images to server with multi-part/form-data.</br>
3 - Show progress of uploading on notification.</br>
4 - Customize your own notifiaction.</br>
5 - Work in background thread even if application is in doze mode.</br>
6 - Also supported for Oreo and above.
<br/>

<h3>Add this to your (app level) build.gradle file</h3><br/>

implementation 'net.gotev:uploadservice-ftp:3.5.0
<br/><br/>

<h3> Small Example </h3><br/>
<pre>
MultipartUploadRequest(this, uploadId, ConstantKey.SERVERURL).addFileToUpload(
                    Environment.getExternalStorageDirectory().toString() + "/.ashish/" + file_name,
                    "file", file_name)
                .addHeader("Content-Type", ConstantKey.CONTENT_TYPE)
                .addHeader("Authorization", ConstantKey.AUTHORIZATION)
                .addParameter("employeeid", ConstantKey.EMPLOYEEID)
                .setNotificationConfig(getNotificationConfig(uploadId))
                .setMaxRetries(2)
                .startUpload()
</pre>
<br/>
<h3>Screenshot of the application</h3><br/>
<img src="https://github.com/ashishgupta191193/multipart-image-upload-android-using-gotev-uploadservice/blob/master/result1.jpeg" width="200" heigt="350"/>
<img src="https://github.com/ashishgupta191193/multipart-image-upload-android-using-gotev-uploadservice/blob/master/result2.jpeg" width="200" heigt="350"/>
<img src="https://github.com/ashishgupta191193/multipart-image-upload-android-using-gotev-uploadservice/blob/master/result3.jpeg" width="200" heigt="350"/>
<br/>

<h3>Thank You</h3>
