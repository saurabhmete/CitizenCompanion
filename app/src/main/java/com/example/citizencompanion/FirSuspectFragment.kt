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
var identifysuspectString = "null"

/**
 * A simple [Fragment] subclass.
 * Use the [FirSuspectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirSuspectFragment : Fragment() {
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
        val v= inflater.inflate(R.layout.fragment_fir_suspect, container, false)

        val firnext3 = v.findViewById<Button>(R.id.suspectnext)
        val firback3 = v.findViewById<Button>(R.id.suspectback)
        val identifysuspect: Spinner = v.findViewById(R.id.identifysuspect)
        ArrayAdapter.createFromResource(
            requireActivity().applicationContext,
            R.array.yesno,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            identifysuspect.adapter = adapter
            identifysuspect.prompt = "Can you identify suspect?"
        }
        identifysuspect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                identifysuspectString = adapterView?.getItemAtPosition(position).toString().toLowerCase()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        firnext3.setOnClickListener{


            val suspectname = v.findViewById<EditText>(R.id.suspectname).text.toString()
            val suspectage = v.findViewById<EditText>(R.id.suspectage).text.toString()
            val suspectadd = v.findViewById<EditText>(R.id.suspectaddress).text.toString()

            CommonUtils.firdata.put("suspectname",suspectname)
            CommonUtils.firdata.put("suspectage",suspectage)
            CommonUtils.firdata.put("suspectaddress",suspectadd)
            CommonUtils.firdata.put("suspectidentify",identifysuspectString)


            val fragment = FirDocumentFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmenttransaction = fragmentManager.beginTransaction()
            fragmenttransaction.replace(R.id.container,fragment)
            fragmenttransaction.addToBackStack(null)
            fragmenttransaction.commit()




        }
        firback3.setOnClickListener{
            val fragment = FirIncidentFragment()
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
         * @return A new instance of fragment FirSuspectFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirSuspectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}