package com.hahnemanntechnology.activities

import android.app.Dialog
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hahnemanntechnology.R

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog


    fun showErrorSnackBar (msg :String, errMsg :Boolean)
    {
        val snackbar= Snackbar.make(findViewById(android.R.id.content),msg, Snackbar.LENGTH_LONG)

        val snackbarView = snackbar.view

        if (errMsg)
            snackbarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.Red))
        else
            snackbarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.Green))

        snackbar.show()
    }

    fun showProgressDialog(text:String)
    {
        mProgressDialog= Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog()
    {
        mProgressDialog.dismiss()
    }



}