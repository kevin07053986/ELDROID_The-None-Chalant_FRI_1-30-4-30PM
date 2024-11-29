package com.acosta.eldriod

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.acosta.eldriod.calendar.ui.CalendarFragment
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.InputStream
import java.io.OutputStream

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1) // Default to -1 if not found
        val userName = sharedPreferences.getString("user_name", getString(R.string.guest))
        val userEmail = sharedPreferences.getString("user_email", "")
        val userDob = sharedPreferences.getString("user_dob", "")
        val userAccountType = sharedPreferences.getString("user_accountType", "")
        //Log.d("HomeActivity", "User data: id=$userId, name=$userName, email=$userEmail, dob=$userDob, accountType=$userAccountType")
        val usernameTextView = findViewById<TextView>(R.id.username)
        usernameTextView.text = userName
        setupIconListeners()

        setupDocumentScanner()

        if (savedInstanceState == null) {
            loadHomeFragment()
        }
    }

    private fun setupIconListeners() {
        findViewById<ImageView>(R.id.calendarIcon).setOnClickListener {
            toCalendar()
        }

        findViewById<ImageView>(R.id.guideIcon).setOnClickListener {
            toGuide()
        }

        findViewById<ImageView>(R.id.calcIcon).setOnClickListener {
            toCalc()
        }
    }

    private fun setupDocumentScanner() {
        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)
        val scannerLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                if (scanningResult != null) {
                    savePdfLocally(scanningResult)
                }
            }
        }

        findViewById<ImageView>(R.id.cameraIcon).setOnClickListener {
            scanner.getStartScanIntent(this@HomeActivity)
                .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener { exception ->
                    //Log.e("HomeActivity", "Error starting scanner", exception)
                    Toast.makeText(this, getString(R.string.scanFail), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .commit()
    }

    private fun toCalendar() {
        //Log.d("HomeActivity", "Navigating to CalendarFragment")
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, CalendarFragment())
            .addToBackStack(null) // Allows back navigation
            .commit()
    }

    private fun toGuide() {
        //Log.d("HomeActivity", "Navigating to GuideActivity")
        val intent = Intent(this, GuideActivity::class.java)
        startActivity(intent)
    }

    private fun toCalc() {
        //Log.d("HomeActivity", "Navigating to CalcActivity")
        val intent = Intent(this, CalcActivity::class.java)
        startActivity(intent)
    }

    private fun savePdfLocally(scanningResult: GmsDocumentScanningResult) {
        scanningResult.pdf?.let { pdf ->
            val pdfUri = pdf.uri
            val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "scanned_document.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/Scanned Documents")
                }
            }
            val newPdfUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            val outputStream: OutputStream? = newPdfUri?.let { contentResolver.openOutputStream(it) }

            if (inputStream != null && outputStream != null) {
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                Toast.makeText(this, getString(R.string.pdfSaved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.pdfFail), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
