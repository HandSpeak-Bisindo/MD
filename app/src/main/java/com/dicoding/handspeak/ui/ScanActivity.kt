package com.dicoding.handspeak.ui

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.handspeak.databinding.ActivityScanBinding
import com.dicoding.handspeak.responses.ScanResponse
import com.dicoding.handspeak.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.graphics.Bitmap
import android.view.View
import com.dicoding.handspeak.R
import java.util.Locale

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnCamera.setOnClickListener {
            requestCameraPermission()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.imagePhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@ScanActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@ScanActivity)
                getFile = myFile
                binding.imagePhoto.setImageURI(uri)
            }
        }
    }

    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission) {
            startTakePhoto()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(cameraPermission),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTakePhoto()
            } else {
                // Izin kamera ditolak, berikan tindakan alternatif atau beri tahu pengguna
                // tentang kebutuhan izin tersebut
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = getFile as File
            val reducedFile = reduceFileImage(file)

            val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "img",
                reducedFile.name,
                requestImageFile
            )

            // Menampilkan ProgressBar
            binding.progressBar.visibility = View.VISIBLE

            val apiService = ApiConfig.getApiService()
            val uploadImageRequest = apiService.uploadImage(imageMultipart)
            uploadImageRequest.enqueue(object : Callback<ScanResponse> {
                override fun onResponse(
                    call: Call<ScanResponse>,
                    response: Response<ScanResponse>
                ) {
                    // Menyembunyikan ProgressBar setelah mendapatkan respons
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val scanResponse = response.body()
                        if (scanResponse != null) {
                            val result = scanResponse.response

                            val intent = Intent(this@ScanActivity, ResultActivity::class.java)

                            // Menyisipkan data ke dalam Intent
                            intent.putExtra("response", result)
                            intent.putExtra("imagePath", reducedFile.path)
                            startActivity(intent)
                        }
                    } else {
                        // Penanganan kesalahan saat upload gambar
                    }
                }

                override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                    // Menyembunyikan ProgressBar saat terjadi kegagalan
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(this@ScanActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            Toast.makeText(
                this@ScanActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val FILENAME_FORMAT = "dd-MMM-yyyy"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val MAXIMAL_SIZE = 1000000
    }
}
