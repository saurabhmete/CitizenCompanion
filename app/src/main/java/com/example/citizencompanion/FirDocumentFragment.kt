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
import com.example.citizencompanion.Utils.FileUtils
import com.example.citizencompanion.objects.FIRObject
import com.google.firebase.Timestamp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
private val database: DatabaseReference = Firebase.database.reference
var imagepath = "null"

/**
 * A simple [Fragment] subclass.
 * Use the [FirDocumentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        val chooseFile = v.findViewById<Button>(R.id.choosefilefir)
        chooseFile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
            }
        })

        val submitFir = v.findViewById<Button>(R.id.submitfir)
        val firback4 = v.findViewById<Button>(R.id.documentback)

        val latitude = CommonUtils.firdata["latitude"].toString()
        val longitude = CommonUtils.firdata["longitude"].toString()

        submitFir.setOnClickListener {
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
                GeoPoint(latitude.toDouble(), longitude.toDouble())
            )
            registerFir(firOb)

            Toast.makeText(
                requireActivity().applicationContext,
                "Filed Successfully",
                Toast.LENGTH_SHORT
            ).show()

            val fragment = FirFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }
        firback4.setOnClickListener{
            val fragment = FirSuspectFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        return v
    }

    private fun uploadimagefir(imagepath: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val file = Uri.fromFile(File(imagepath))
        val riversRef = storageRef.child("firimage/${file.lastPathSegment}")
        val uploadTask = riversRef.putFile(file)

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            /*Toast.makeText(
                requireActivity().applicationContext, "Aadhar Upload Failed",
                Toast.LENGTH_SHORT
            ).show()*/

            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...


            /*Toast.makeText(
                requireActivity().applicationContext, "Aadhar Success",
                Toast.LENGTH_LONG
            ).show()*/
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedFile = data?.data
            //The uri with the location of the file

            if (selectedFile != null) {
                val fileutils = FileUtils(requireActivity().applicationContext)
                val filePath = fileutils.getPath(selectedFile)
                imagepath = filePath.toString()
            }
        }
    }

    private fun registerFir(firOb: FIRObject) {
        fireStore.collection("FIR")
            .add(firOb)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
//                    database
//                        .child("FIR")
//                        .child(CommonUtils.firdata["pinCode"].toString())
//                        .child("submitted")


                } else {

                }
            }
            .addOnSuccessListener { doc ->
                Log.d("DEBUG", "Successful Insertion with doc -> $doc")
            }
            .addOnFailureListener { e ->
                Log.d("ERROR", "Error Occured when filing FIR", e)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirDocumentFragment.
         */
        // TODO: Rename and change types and number of parameters
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