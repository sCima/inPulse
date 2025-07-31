package br.com.fiap.inpulse.features.newidea

interface ImageSelectionListener {
    fun openImagePicker()
    fun onImageSelected(base64Image: String?)
}