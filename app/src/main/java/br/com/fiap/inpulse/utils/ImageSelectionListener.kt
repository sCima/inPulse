package br.com.fiap.inpulse.utils

interface ImageSelectionListener {
    fun openImagePicker()
    fun onImageSelected(base64Image: String?)
}