package com.example.citizencompanion.Utils

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.example.citizencompanion.R
import kotlinx.android.synthetic.main.custom_loading_dialog.view.*
import org.jetbrains.anko.find

class LoadingClassCustomLoader(val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog

    fun startLoading(incomingText: String){
        // Set View
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_loading_dialog, null)
        dialogView.loadingProgressBarText.text = incomingText
        // Set Dialog
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}