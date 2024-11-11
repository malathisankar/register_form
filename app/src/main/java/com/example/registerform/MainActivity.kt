package com.example.registerform

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.CountryCodePicker
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var phoneNumberEditText: EditText
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var statusTextView: TextView
    private lateinit var profileImageView: ImageView

    private val REQUEST_CODE_PICK_IMAGE = 1
    private val REQUEST_CODE_CAPTURE_IMAGE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        countryCodePicker = findViewById(R.id.countryCodePicker)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText)
        addressEditText = findViewById(R.id.addressEditText)
        stateEditText = findViewById(R.id.stateEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        statusTextView = findViewById(R.id.statusTextView)
        profileImageView = findViewById(R.id.profileImageView)

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            showImageOptionDialog()
        }

        dateOfBirthEditText.setOnClickListener {
            showDatePicker()
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            registerUser()
        }
    }

    private fun showImageOptionDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Profile Picture")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> captureImageFromCamera()
                1 -> pickImageFromGallery()
            }
        }
        builder.show()
    }

    private fun captureImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                dateOfBirthEditText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun registerUser() {
        val fullName = fullNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val countryCode = countryCodePicker.selectedCountryCodeWithPlus
        val phoneNumber = phoneNumberEditText.text.toString()
        val dateOfBirth = dateOfBirthEditText.text.toString()
        val address = addressEditText.text.toString()
        val state = stateEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() ||
            dateOfBirth.isEmpty() || address.isEmpty() || state.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()
        ) {
            statusTextView.text = "All fields are required."
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            statusTextView.text = "Invalid email format."
            return
        }

        if (password != confirmPassword) {
            statusTextView.text = "Passwords do not match."
            return
        }

        statusTextView.text = "Registration successful!"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val bitmap: Bitmap? = when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                REQUEST_CODE_CAPTURE_IMAGE -> data.extras?.get("data") as? Bitmap
                else -> null
            }
            profileImageView.setImageBitmap(bitmap)
        }
    }
}
