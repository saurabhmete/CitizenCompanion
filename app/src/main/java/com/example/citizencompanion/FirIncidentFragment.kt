package com.example.citizencompanion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.citizencompanion.Utils.CommonUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var firTypeString = "null"
var firPlaceString = "null"
var seensuspectString = "null"
/**
 * A simple [Fragment] subclass.
 * Use the [FirIncidentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirIncidentFragment : Fragment() {
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
        val v= inflater.inflate(R.layout.fragment_fir_incident, container, false)
        val firnext2 = v.findViewById<Button>(R.id.firnext2)
        val firback2 = v.findViewById<Button>(R.id.firback2)
        val firType: Spinner = v.findViewById(R.id.typeincident)

        ArrayAdapter.createFromResource(
            requireActivity().applicationContext,
            R.array.fir_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            firType.adapter = adapter
            firType.prompt = "Type of Incident"
        }
        firType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firTypeString = adapterView?.getItemAtPosition(position).toString().toLowerCase()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val firplace: Spinner = v.findViewById(R.id.placeincident)
        ArrayAdapter.createFromResource(
            requireActivity().applicationContext,
            R.array.fir_place_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            firplace.adapter = adapter
            firplace.prompt = "Place of Incident"
        }
        firplace.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firPlaceString = adapterView?.getItemAtPosition(position).toString().toLowerCase()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val seensuspect: Spinner = v.findViewById(R.id.suspect)
        ArrayAdapter.createFromResource(
            requireActivity().applicationContext,
            R.array.yesno,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            seensuspect.adapter = adapter
            seensuspect.prompt = "Have you seen the suspect"
        }
        seensuspect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                seensuspectString = adapterView?.getItemAtPosition(position).toString().toLowerCase()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        firnext2.setOnClickListener{

            val firplacename = v.findViewById<EditText>(R.id.incidentplacename).text.toString()
            val incidentdate = v.findViewById<EditText>(R.id.incidentdate).text.toString()

            CommonUtils.firdata.put("incidenttype",firTypeString)
            CommonUtils.firdata.put("incidentplacetype",firPlaceString)
            CommonUtils.firdata.put("incidentplacename",firplacename)
            CommonUtils.firdata.put("incidentdate",incidentdate)
            CommonUtils.firdata.put("seensuspect",seensuspectString)

            if(seensuspectString.equals("yes")){
                val fragment = FirSuspectFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmenttransaction = fragmentManager.beginTransaction()
                fragmenttransaction.replace(R.id.container,fragment)
                fragmenttransaction.addToBackStack(null)
                fragmenttransaction.commit()
            }else{
                val fragment = FirDocumentFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmenttransaction = fragmentManager.beginTransaction()
                fragmenttransaction.replace(R.id.container,fragment)
                fragmenttransaction.addToBackStack(null)
                fragmenttransaction.commit()
            }
        }
        firback2.setOnClickListener{
            val fragment = FirFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmenttransaction = fragmentManager.beginTransaction()
            fragmenttransaction.replace(R.id.container,fragment)
            fragmenttransaction.addToBackStack(null)
            fragmenttransaction.commit()
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
         * @return A new instance of fragment FirIncidentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirIncidentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}