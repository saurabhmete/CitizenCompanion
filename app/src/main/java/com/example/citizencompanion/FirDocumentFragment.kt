package com.example.citizencompanion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.Utils.LoadingClassCustomLoader
import com.example.citizencompanion.objects.FIRObject
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
private val database: DatabaseReference = Firebase.database.reference
private val fireStorage: FirebaseStorage = FirebaseStorage.getInstance()

var imagepath = "null"
lateinit var filePath: Uri
lateinit var firDocumentReferenceId: String
val REGISTER_FIR = true;

class FirDocumentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_fir_document, container, false)

        val loading = LoadingClassCustomLoader(requireActivity())

        if(REGISTER_FIR){
            loading.startLoading("Filing your FIR")
            createFir(loading)
        }

        val chooseFile = v.findViewById<Button>(R.id.choosefilefir)
        chooseFile.setOnClickListener {
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
        val chooseVideoFile = v.findViewById<Button>(R.id.chooseVideoFir)
        chooseVideoFile.setOnClickListener {
            val intent = Intent()
                .setType("video/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a video"), 111)
        }

        val submitFir = v.findViewById<Button>(R.id.submitfir)

        val latitude = CommonUtils.firdata["latitude"].toString()
        val longitude = CommonUtils.firdata["longitude"].toString()

        submitFir.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, DashboardMenu::class.java))
                finish()
            }
        }

        val uploadImage = v.findViewById<Button>(R.id.uploadImage)
        uploadImage.setOnClickListener {
            if(::filePath.isInitialized) {
                loading.startLoading("Uploading")
                uploadEvidences(loading)
            } else{
                Toast.makeText(requireContext(), "Please select image for upload", Toast.LENGTH_SHORT).show()
            }
        }

        val uploadVideo = v.findViewById<Button>(R.id.uploadVideo)
        uploadVideo.setOnClickListener {
            if(::filePath.isInitialized) {
                loading.startLoading("Uploading")
                uploadEvidences(loading)
            } else{
                Toast.makeText(requireContext(), "Please select video for upload", Toast.LENGTH_SHORT).show()
            }
        }
        return v
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

    // Create Object For FIR
    private fun createFir(loading: LoadingClassCustomLoader) {
        val latitude = CommonUtils.firdata["latitude"].toString()
        val longitude = CommonUtils.firdata["longitude"].toString()

        val locationMap = HashMap<String, String>()
        locationMap["longitude"] = longitude
        locationMap["latitude"] = latitude

        val firOb = FIRObject(
            CommonUtils.firdata["uid"].toString(),
            CommonUtils.firdata["name"].toString(),
            CommonUtils.firdata["gender"].toString(),
            Timestamp(Date()),
            CommonUtils.firdata["address"].toString(),
            CommonUtils.firdata["phone"].toString(),
            CommonUtils.firdata["aadhar"].toString(),
            CommonUtils.firdata["incidenttype"].toString(),
            CommonUtils.firdata["incidentplacetype"].toString(),
            CommonUtils.firdata["incidentplacename"].toString(),
            CommonUtils.firdata["incidentdate"].toString(),
            CommonUtils.firdata["seensuspect"].toString(),
            CommonUtils.firdata["suspectname"].toString(),
            CommonUtils.firdata["suspectage"].toString(),
            CommonUtils.firdata["suspectaddress"].toString(),
            CommonUtils.firdata["suspectidentify"].toString(),
            locationMap,
            listOf()
        )

        registerFir(firOb, loading)
    }

    private fun registerFir(firOb: FIRObject, loading: LoadingClassCustomLoader) {
        fireStore.collection("FIR")
            .add(firOb)
            .addOnSuccessListener { documentReference ->
                Log.i("INFO", "Successful Insertion with doc -> $documentReference")
                firDocumentReferenceId = documentReference.id
                        database
                            .child("FIRs")
                            .child(CommonUtils.firdata["pinCode"].toString())
                            .child(documentReference.id)
                            .setValue("registered")
                            .addOnSuccessListener {
                                Log.d(
                                    "INFO",
                                    "Successful FIR document-key insertion in realtime database"
                                )
                                addFIRToUser(documentReference.id, loading)
                            }
            }
            .addOnFailureListener { e ->
                Log.d("ERROR", "Error Occurred when filing FIR", e)
            }
    }

    private fun addFIRToUser(id: String, loading: LoadingClassCustomLoader) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            fireStore.collection("citizen")
                .document(currentUser.uid)
                .update("FIRs", FieldValue.arrayUnion(id))
                .addOnSuccessListener {
                    Log.i("addFIRToUser", "Successful addition of FIR in citizen dataStore")
                    loading.isDismiss()
                    Toast.makeText(
                        requireActivity().applicationContext,
                        "FIR Filed Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { exception ->
                    Log.e("addFIRToUser", "Failed to add FIR in citizen dataStore $exception")
                }
        }
    }

    private fun uploadEvidences(loading: LoadingClassCustomLoader){
        val filePathAndName = "fir/${firDocumentReferenceId}/evidence/evidence_${System.currentTimeMillis()}"
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
                    Toast.makeText(requireActivity(), "Error in Uploading", Toast.LENGTH_SHORT)
                }
            }
    }

    private fun addEvidencesToFIR(downloadUri: String) {
        fireStore.collection("FIR")
            .document(firDocumentReferenceId)
            .update("evidenceList", FieldValue.arrayUnion(downloadUri))
            .addOnSuccessListener {
                Toast.makeText(
                    requireActivity(),
                    "File Uploaded Successfully",
                    Toast.LENGTH_SHORT
                )
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirDocumentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}