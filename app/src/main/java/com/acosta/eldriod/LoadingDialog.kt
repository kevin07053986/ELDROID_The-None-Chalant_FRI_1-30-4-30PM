package com.acosta.eldriod

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater

class LoadingDialog(private val activity: Activity) {

    private lateinit var dialog: AlertDialog

    fun loginLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        builder.setView(inflater.inflate(R.layout.activity_loading_screen, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}
