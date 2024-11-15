package com.mab.buwisbuddyph

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog(private val activity: Context) {

    private lateinit var dialog: AlertDialog

    fun loginLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        builder.setView(inflater.inflate(R.layout.loading_screen, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}
