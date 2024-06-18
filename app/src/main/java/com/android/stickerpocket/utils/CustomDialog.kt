package com.android.stickerpocket.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.android.stickerpocket.R
import com.android.stickerpocket.presentation.sticker.StickerFragmentInteractor


object CustomDialog {
    private lateinit var alertDialog: AlertDialog

    @SuppressLint("MissingInflatedId")
    fun showCustomDialog(
        context: Context,
        message: String,
        buttonText:String
    ) {

        val dialogView: View = View.inflate(context,R.layout.cv_no_stickers_dialog, null)



        val msg_txt = dialogView.findViewById<TextView>(R.id.tv_message)
        val custom_button: Button = dialogView.findViewById(R.id.btn_ok)

        msg_txt.text=message
        custom_button.text=buttonText


        custom_button.setOnClickListener {
            alertDialog.dismiss()
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
        alertDialog.show()
    }
}