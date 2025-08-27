package br.com.fiap.inpulse.features.newidea.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // Importar ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.utils.ImageSelectionListener // Importar a interface

class IdeaFragmentImg : Fragment(), IdeaInfoProvider {

    private lateinit var btnSelectImage: AppCompatButton
    private lateinit var imagePreview: ImageView
    private lateinit var btnEnviarImg: AppCompatButton
    private lateinit var cardViewImagePreview: CardView
    private var imageSelectionListener: ImageSelectionListener? = null
    private var currentImageBase64: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideia_img, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSelectImage = view.findViewById(R.id.sim_enviar_imagem)
        imagePreview = view.findViewById(R.id.imagePreview)
        cardViewImagePreview = view.findViewById(R.id.cardViewImagePreview)
        btnEnviarImg = view.findViewById(R.id.sim_enviar_imagem)

        btnSelectImage.setOnClickListener {
            imageSelectionListener?.openImagePicker()
        }

        view.findViewById<TextView>(R.id.txt_sem_imagem).setOnClickListener {
            imageSelectionListener?.onImageSelected(null)
            imagePreview.visibility = View.GONE
            imagePreview.setImageDrawable(null)
            currentImageBase64 = null
        }

        if (!currentImageBase64.isNullOrEmpty()) {
            updateImagePreview(currentImageBase64)
        }
    }

    fun setImageSelectionListener(listener: ImageSelectionListener) {
        this.imageSelectionListener = listener
    }

    fun updateImagePreview(base64Image: String?) {
        this.currentImageBase64 = base64Image
        if (!base64Image.isNullOrEmpty()) {
            try {
                val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                imagePreview.setImageBitmap(decodedByte)
                imagePreview.visibility = View.VISIBLE
                cardViewImagePreview.visibility = View.VISIBLE
                btnEnviarImg.text = "Imagem enviada"
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                imagePreview.visibility = View.GONE
                cardViewImagePreview.visibility = View.GONE
            }
        } else {
            imagePreview.visibility = View.GONE
            cardViewImagePreview.visibility = View.GONE
            imagePreview.setImageDrawable(null)
        }
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        return bundle
    }
}