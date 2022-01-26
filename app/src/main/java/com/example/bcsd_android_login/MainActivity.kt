package com.example.bcsd_android_login

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bcsd_android_login.adapter.ImageAdapter
import com.example.bcsd_android_login.databinding.ActivityMainBinding
import com.example.bcsd_android_login.model.ImageData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth // 파이어 베이스 인증 객체
    private val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
    private val adapter = ImageAdapter()

    companion object{
        private const val TAG = "cursorTest"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()

        // 구글 및 커스텀 로그인 후 화면 중 로그아웃
        binding.signOutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            firebaseAuth.signOut()
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {}
            startActivity(intent)
            finish()
        }

        // 구글 및 커스텀 로그인 후 화면 중 회원탈퇴
        binding.revokeButton.setOnClickListener {
            firebaseAuth.currentUser?.delete()
            AuthUI.getInstance()
                .delete(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        openMediaStore() // 이미지 불러오기
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImages()
                    Log.d("clickTest", "True")
                } else {
                    Log.d("clickTest", "False")
                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    if (!showRationale) {
                        goToSettings()
                    }
                }
                return
            }
        }
    }

    private fun goToSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    private fun showImages() {
        adapter.testList.addAll(getList())
        binding.recyclerViewFrame.also {
            it.layoutManager = GridLayoutManager(this, 3)
            it.adapter = adapter
        }

    }

    private fun openMediaStore() {
        if (haveStoragePermission()) {
            Log.d("openMediaStore", "True")
            showImages()
        } else {
            requestPermission()
            Log.d("openMediaStore", "False")
        }
    }

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    private fun haveStoragePermission() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


    private fun getList(): List<ImageData> {
        val testList = mutableListOf<ImageData>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs = arrayOf(
            dateToTimestamp(day = 1, month = 1, year = 1970).toString()
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                val image = ImageData(id, dateTaken, displayName, contentUri)
                testList += image
                Log.d(
                    TAG, "id: $id, display_name: $displayName, date_taken: " +
                            "$dateTaken, content_uri: $contentUri"
                )
            }
        }

        Log.d(
            TAG, "Found ${testList.size} testList"
        )
        return testList
    }

    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            formatter.parse("$day.$month.$year")?.time ?: 0
        }

}
