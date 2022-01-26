package com.example.bcsd_android_login.model

import android.net.Uri
import java.util.*

data class ImageData(
    val id: Long,
    val dateTaken: Date,
    val displayName: String,
    val contentUri: Uri
)