package com.example.citizencompanion.viewfir

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.citizencompanion.R
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.Utils.LoadingClassCustomLoader
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_view_fir.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class ViewFir : AppCompatActivity() {

    val firestore = Firebase.firestore

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_fir)

        loadFIRDetails()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun loadFIRDetails() {
        val firId = CommonUtils.firdata["firID"].toString()
        firestore.collection("FIR")
            .document(firId)
            .get()
            .addOnSuccessListener { task ->
                if (task.data != null) {
                    incidentTypeValue.setText(task.data?.get("incidenttype").toString())
                    incidentPlaceValue.setText(task.data?.get("incidentplacename").toString())
                    incidentPlaceTypeValue.setText(task.data?.get("incidentplacetype").toString())
                    incidentDateValue.setText(task.data?.get("incidentdate").toString())
                    val dateValue: Timestamp = task.getTimestamp("date") as Timestamp
                    firDateValue.setText(dateValue.toDate().toString())
                    firNameValue.setText(task.data?.get("name").toString())
                    phoneNumberValue.setText(task.data?.get("phone").toString())

                    val locationMap = task.data?.get("location") as Map<String, String>
                    setNavigationButton(locationMap)
                    setDownloadbleContent(task.data?.get("evidenceList") as ArrayList<*>)
                }
            }
    }

    fun setNavigationButton(locationMap: Map<String, String>) {
        navButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${locationMap["latitude"]},${locationMap["longitude"]}&mode=d")
            )
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setDownloadbleContent(imageUrls: ArrayList<*>) {
        downladbleContent.setOnClickListener {
            val loading = LoadingClassCustomLoader(this)
            loading.startLoading("Downloading All Resouces")

            val permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val REQUEST_PERMISSION = arrayOf<String>(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUEST_PERMISSION,
                    1
                )
            }

            if (imageUrls.size == 0) {
                var totalDownloads = 0;
                for (imageUrl in imageUrls) {
                    totalDownloads += 1
                    // Get video reference using URL
                    val storageRef: StorageReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(
                            imageUrl as String
                        )
                    storageRef.metadata
                        .addOnSuccessListener { storageMetadata ->
                            // Get basic metadata
                            val fileName = storageMetadata.name
                            val fileType = storageMetadata.contentType?.split('/')?.get(1)
                            val storageDirectory =
                                "${Environment.getExternalStoragePublicDirectory("").absolutePath}/CitizenCompanion/${CommonUtils.firdata["firID"].toString()}"
                            val downloadDirectory = File(storageDirectory)
                            if (!downloadDirectory.exists()) {
                                if (!downloadDirectory.mkdir()) {
                                    downloadDirectory.mkdirs()
                                }
                            }
                            // Download the file
                            val localFile = File.createTempFile("images", ".$fileType")
                            storageRef.getFile(localFile).addOnSuccessListener { fileDownload ->
                                // Local temp file has been created
                                val path1 = Paths.get(localFile.toURI())
                                val path2 = Paths.get("${storageDirectory}/$fileName.$fileType")
                                Files.move(path1, path2)

                                if (totalDownloads == imageUrls.size) {
                                    loading.isDismiss()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT)
                                Log.e("ViewFIR Download", exception.toString())
                            }
                        }
                }
            }
        }
    }
}

/* Code for downloading to Downloads Directory
    // val storageDirectory = "${Environment.getStorageDirectory().absolutePath}/CitizenCompanion/"
    val fileP = File(Environment.getExternalStoragePublicDirectory(""), "CitizenCompanion")
    fileP.mkdir()
    val storageDirectory = "${Environment.getExternalStoragePublicDirectory("").absolutePath}/CitizenCompanion/"
     //val storageDirectory = Environment.DIRECTORY_DOWNLOADS

    // Initialize DownloadManager
    val downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // URI to be downloaded
    val uri = Uri.parse(imageUrl)

    // Create download request, new request for each download
    val request = DownloadManager.Request(uri)
    // Notification visibility
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir("" + storageDirectory,"${fileName}.png");

    // Add request to queue, can add multiple queues
    downloadManager.enqueue(request)
*/