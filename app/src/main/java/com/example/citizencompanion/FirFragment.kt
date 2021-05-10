package com.example.citizencompanion

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.citizencompanion.Utils.CommonUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_fir.*
import kotlinx.android.synthetic.main.fragment_fir_incident.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var day = 0
    private var month = 0
    private var year = 0

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
        val v = inflater.inflate(R.layout.fragment_fir, container, false)
        val firnext1 = v.findViewById<Button>(R.id.firnext1)

        firnext1.setOnClickListener {
            val fragment = FirIncidentFragment()

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val name = v.findViewById<EditText>(R.id.firname).text.toString()
            val radioGender = v.findViewById<RadioGroup>(R.id.radiogenderfir)
            val selectedGender = radioGender.checkedRadioButtonId
            val xRadioGender = v.findViewById<RadioButton>(selectedGender)
            val gender = xRadioGender.text.toString()
            val address = v.findViewById<EditText>(R.id.postalfir).text.toString()
            val phone = v.findViewById<EditText>(R.id.phonenofir).text.toString()
            val aadhar = v.findViewById<EditText>(R.id.aadharfir).text.toString()

            if(uid != null){
                CommonUtils.firdata["uid"] = uid
            }
            CommonUtils.firdata["name"] = name
            CommonUtils.firdata["gender"] = gender
            CommonUtils.firdata["address"] = address
            CommonUtils.firdata["phone"] = phone
            CommonUtils.firdata["aadhar"] = aadhar

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
         }

        pickDate(v)
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val dateOfIncident = "$dayOfMonth / $month / $year"
            DOBfir.setText(dateOfIncident)
        }

        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getCurrentDate(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    private fun pickDate(v: View) {
        val dobFir = v.findViewById<EditText>(R.id.DOBfir)
        dobFir.setOnClickListener {
            getCurrentDate()
            DatePickerDialog(requireActivity(), dateSetListener, year, month, day).show()
        }
    }
}