package com.example.citizencompanion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.citizencompanion.Utils.CommonUtils

var identifysuspectString = "null"

class FirSuspectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_fir_suspect, container, false)

        val firnext3 = v.findViewById<Button>(R.id.suspectnext)
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

        return v
    }
}