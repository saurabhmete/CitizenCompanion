package com.example.citizencompanion.viewfir

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.citizencompanion.R
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.Utils.LoadingClassCustomLoader
import com.example.citizencompanion.filePath
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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
    lateinit var filePath: Uri

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_fir)

        loadFIRDetails()

        val loading = LoadingClassCustomLoader(this)
        val chooseImageFile = findViewById<Button>(R.id.chooseImageUpdate)
        chooseImageFile.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
        val chooseVideoFile = findViewById<Button>(R.id.chooseVideoUpdate)
        chooseVideoFile.setOnClickListener {
            val intent = Intent()
                .setType("video/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a video"), 111)
        }
        val uploadImage = findViewById<Button>(R.id.uploadImageUpdate)
        uploadImage.setOnClickListener {
            if(::filePath.isInitialized) {
                loading.startLoading("Uploading")
                uploadEvidences(loading)
            } else{
                Toast.makeText(this, "Please select image for upload", Toast.LENGTH_SHORT).show()
            }
        }
        val uploadVideo = findViewById<Button>(R.id.uploadVideoUpdate)
        uploadVideo.setOnClickListener {
            if(::filePath.isInitialized) {
                loading.startLoading("Uploading")
                uploadEvidences(loading)
            } else{
                Toast.makeText(this, "Please select video for upload", Toast.LENGTH_SHORT).show()
            }
        }

        editFIR.setOnClickListener {
            editFIR.visibility = View.INVISIBLE
            updateFIR.visibility = View.VISIBLE

            enableEdit()
        }

        updateFIR.setOnClickListener {
            updateFIR.visibility = View.INVISIBLE
            editFIR.visibility = View.VISIBLE

            disableEdit()
            val data = hashMapOf("name" to firNameValue.text.toString())
            data["phone"] = phoneNumberValue.text.toString()
            data["incidenttype"] = incidentTypeValue.text.toString()
            data["incidentplacename"] = incidentPlaceValue.text.toString()
            data["incidentplacetype"] = incidentPlaceTypeValue.text.toString()
            data["phone"] = phoneNumberValue.text.toString()
            data["suspectname"] = suspectNameValue.text.toString()
            data["suspectaddress"] = suspectAddressValue.text.toString()
            data["suspectage"] = suspectAgeValue.text.toString()

            val loading = LoadingClassCustomLoader(this)
            loading.startLoading("Updating FIR")
            firestore.collection("FIR")
                .document(CommonUtils.firdata["firID"].toString())
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "FIR details updated Successfully", Toast.LENGTH_SHORT).show()
                    loading.isDismiss()
                }
        }
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
                    firNameValue.setText(task.data?.get("name").toString())
                    phoneNumberValue.setText(task.data?.get("phone").toString())
                    suspectNameValue.setText(task.data?.get("suspectname").toString())
                    suspectAddressValue.setText(task.data?.get("suspectaddress").toString())
                    suspectAgeValue.setText(task.data?.get("suspectage").toString())

                    val dateValue: Timestamp = task.getTimestamp("date") as Timestamp
                    firDateValue.setText(dateValue.toDate().toString())

                    val locationMap = task.data?.get("location") as Map<String, String>
                    setNavigationButton(locationMap)
                    setDownloadableContent(task.data?.get("evidenceList") as ArrayList<*>)

                    disableEdit()
                }
            }
    }

    private fun setNavigationButton(locationMap: Map<String, String>) {
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
    private fun setDownloadableContent(imageUrls: ArrayList<*>) {
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

            if (imageUrls.size != 0) {
                var totalDownloads = 0
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
                                Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show()
                                Log.e("ViewFIR Download", exception.toString())
                            }
                        }
                }
            }
        }
    }

    private fun disableEdit(){
        incidentTypeValue.isFocusable = false
        incidentPlaceValue.isFocusable = false
        incidentPlaceTypeValue.isFocusable = false
        incidentDateValue.isFocusable = false
        firNameValue.isFocusable = false
        phoneNumberValue.isFocusable = false
        suspectNameValue.isFocusable = false
        suspectAddressValue.isFocusable = false
        suspectAgeValue.isFocusable = false

        navButton.visibility = View.VISIBLE
        downladbleContent.visibility = View.VISIBLE

        chooseImageUpdate.visibility = View.GONE
        uploadImageUpdate.visibility = View.GONE
        chooseVideoUpdate.visibility = View.GONE
        uploadVideoUpdate.visibility = View.GONE
    }

    private fun enableEdit(){
        incidentTypeValue.isFocusableInTouchMode = true
        incidentPlaceValue.isFocusableInTouchMode = true
        incidentPlaceTypeValue.isFocusableInTouchMode = true
        incidentDateValue.isFocusableInTouchMode = true
        firNameValue.isFocusableInTouchMode = true
        phoneNumberValue.isFocusableInTouchMode = true
        suspectNameValue.isFocusableInTouchMode = true
        suspectAddressValue.isFocusableInTouchMode = true
        suspectAgeValue.isFocusableInTouchMode = true

        navButton.visibility = View.GONE
        downladbleContent.visibility = View.GONE

        chooseImageUpdate.visibility = View.VISIBLE
        uploadImageUpdate.visibility = View.VISIBLE
        chooseVideoUpdate.visibility = View.VISIBLE
        uploadVideoUpdate.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val selectedFile = data?.data
            //The uri with the location of the file
            filePath = data.data!!

            if (selectedFile != null) {
//                val fileutils = FileUtils(requireActivity().applicationContext)
//                val filePath = fileutils.getPath(selectedFile)
//                imagepath = filePath.toString()
            }
        }
    }

    private fun uploadEvidences(loading: LoadingClassCustomLoader){
        val filePathAndName = "fir/${CommonUtils.firdata["firID"].toString()}/evidence/evidence_${System.currentTimeMillis()}"
        val uploadEvidencePath =  FirebaseStorage.getInstance()
            .getReference(filePathAndName)
            .putFile(filePath)

        uploadEvidencePath.addOnCompleteListener { uploadSnapshot ->
            if (uploadSnapshot.isSuccessful) {
                uploadSnapshot.result.storage.downloadUrl.addOnSuccessListener { taskUri ->
                    val uuri: Uri = taskUri.normalizeScheme()
                    val uuriSting = uuri.toString()
                    addEvidencesToFIR(uuriSting)
                    loading.isDismiss()
                }
            } else {
                loading.isDismiss()
                Toast.makeText(this, "Error in Uploading", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun addEvidencesToFIR(downloadUri: String) {
        firestore.collection("FIR")
            .document(CommonUtils.firdata["firID"].toString())
            .update("evidenceList", FieldValue.arrayUnion(downloadUri))
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "File Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
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