package br.com.fiap.inpulse.features.newidea.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.utils.ImageSelectionListener
import coil.load

class IdeaFragmentImg : Fragment(), IdeaInfoProvider {

    private lateinit var btnSelectImage: AppCompatButton
    private lateinit var imagePreview: ImageView
    private lateinit var btnEnviarImg: AppCompatButton
    private lateinit var cardViewImagePreview: CardView
    private var imageSelectionListener: ImageSelectionListener? = null

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
        }
    }

    fun setImageSelectionListener(listener: ImageSelectionListener) {
        this.imageSelectionListener = listener
    }

    fun updateImagePreviewFromUri(imageUri: Uri?) {
        if (imageUri != null) {
            imagePreview.load(imageUri) {
                crossfade(true)
                listener { _, _ ->
                    imagePreview.visibility = View.VISIBLE
                    cardViewImagePreview.visibility = View.VISIBLE
                    btnEnviarImg.text = "Alterar Imagem"
                }
            }
        } else {
            imagePreview.setImageDrawable(null)
            imagePreview.visibility = View.GONE
            cardViewImagePreview.visibility = View.GONE
            btnEnviarImg.text = "Enviar Imagem"
        }
    }

    override fun enviarDados(): Bundle {
        val bundle = Bundle()
        return bundle
    }
}