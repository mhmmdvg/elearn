package com.elearn.presentation.ui.model

import android.net.Uri

data class MaterialFormState (
    val materialName: String = "",
    val description: String = "",
    val selectedClass: String? = null,
    val selectedClassId: String? = null,
    val selectedFileUri: Uri? = null,
    val selectedFileName: String = "No file selected"
)