package com.example.citizencompanion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.citizencompanion.Utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_fir.view.*

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


       val v = inflater.inflate(R.layout.fragment_fir,container,false)
        val firnext1 = v.findViewById<Button>(R.id.firnext1)


        firnext1.setOnClickListener{
        val fragment = FirIncidentFragment()


            val firname = v.findViewById<EditText>(R.id.firname).text.toString()
            val radioGender = v.findViewById<RadioGroup>(R.id.radiogenderfir)
            val selectedGender = radioGender.checkedRadioButtonId
            val xRadioGender = v.findViewById<RadioButton>(selectedGender)
            val firgender = xRadioGender.text.toString()
            val firdate = v.findViewById<EditText>(R.id.DOBfir).text.toString()
            val firaddress = v.findViewById<EditText>(R.id.postalfir).text.toString()
            val firphone = v.findViewById<EditText>(R.id.phonenofir).text.toString()
            val firaadhar = v.findViewById<EditText>(R.id.aadharfir).text.toString()

            CommonUtils.firdata.put("firname",firname)
            CommonUtils.firdata.put("firgender",firgender)
            CommonUtils.firdata.put("firdate",firdate)
            CommonUtils.firdata.put("firaddress",firaddress)
            CommonUtils.firdata.put("firphone",firphone)
            CommonUtils.firdata.put("firaadhar",firaadhar)



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







}