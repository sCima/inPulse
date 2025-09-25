package br.com.fiap.inpulse.utils

import com.azure.storage.blob.BlobClientBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.Uri

suspend fun uploadImageToAzure(context: Context, imageUri: Uri, blobName: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val blobEndpoint = "https://${AzureConstants.STORAGE_ACCOUNT_NAME}.blob.core.windows.net"
            val blobUrlWithSas = "$blobEndpoint/${AzureConstants.CONTAINER_NAME}/$blobName${AzureConstants.SAS_TOKEN}"

            val blobClient = BlobClientBuilder()
                .endpoint(blobUrlWithSas)
                .buildClient()

            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->

                blobClient.upload(inputStream, inputStream.available().toLong(), true)

                return@withContext "$blobEndpoint/${AzureConstants.CONTAINER_NAME}/$blobName"
            }

            return@withContext null

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}