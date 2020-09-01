package com.example.citizencompanion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.Utils.FileUtils
import com.example.citizencompanion.objects.FIRObject
import com.example.citizencompanion.services.FirService
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
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


        val v= inflater.inflate(R.layout.fragment_fir_document, container, false)
        val choosefile = v.findViewById<Button>(R.id.choosefilefir)
        choosefile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)

            }
        })

        val submitfir = v.findViewById<Button>(R.id.submitfir)
        val firback4 = v.findViewById<Button>(R.id.documentback)
        submitfir.setOnClickListener{





            var firOb = FIRObject(
                CommonUtils.firdata["uid"].toString(),
                CommonUtils.firdata["firname"].toString(),
                CommonUtils.firdata["firgender"].toString(),
                CommonUtils.firdata["firdate"].toString(),
                CommonUtils.firdata["firaddress"].toString(),
                CommonUtils.firdata["firphone"].toString(),
                CommonUtils.firdata["firaadhar"].toString(),
                CommonUtils.firdata["incidenttype"].toString(),
                CommonUtils.firdata["incidentplacetype"].toString(),
                CommonUtils.firdata["incidentplacename"].toString(),
                CommonUtils.firdata["incidentdate"].toString(),
                CommonUtils.firdata["seensuspect"].toString(),
                CommonUtils.firdata["suspectname"].toString(),
                CommonUtils.firdata["suspectage"].toString(),
                CommonUtils.firdata["suspectaddress"].toString(),
                CommonUtils.firdata["suspectidentify"].toString(),
                CommonUtils.firdata["Location"].toString()
            )
            FirService.registerFir(firOb)
            Toast.makeText(requireActivity().applicationContext, "Filed Successfully", Toast.LENGTH_SHORT).show()





            val fragment = FirFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmenttransaction = fragmentManager.beginTransaction()
            fragmenttransaction.replace(R.id.container,fragment)
            fragmenttransaction.addToBackStack(null)
            fragmenttransaction.commit()




        }
        firback4.setOnClickListener{
            val fragment = FirSuspectFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmenttransaction = fragmentManager.beginTransaction()
            fragmenttransaction.replace(R.id.container,fragment)
            fragmenttransaction.addToBackStack(null)
            fragmenttransaction.commit()




        }

        return v
    }
    private fun uploadimagefir(imagepath: String) {

        val storage = Firebase.storage
        val storageRef = storage.reference


        var file = Uri.fromFile(File(imagepath))
        val riversRef = storageRef.child("firimage/${file.lastPathSegment}")
        var uploadTask = riversRef.putFile(file)

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
                imagepath=filePath.toString()

            }

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