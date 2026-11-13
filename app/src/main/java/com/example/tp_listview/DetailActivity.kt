package com.example.tp_listview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.io.File

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_POSITION = "extra_position"
        const val RESULT_DELETE = 100
    }

    private lateinit var toolbarDetail: MaterialToolbar
    private lateinit var ivDetailPhoto: ImageView
    private lateinit var ivCameraBadge: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTelephone: EditText
    private lateinit var btnAppeler: ImageButton
    private lateinit var etEmail: EditText
    private lateinit var btnEmail: ImageButton
    private lateinit var btnChoisirPhoto: MaterialButton
    private lateinit var btnPrendrePhoto: MaterialButton
    private lateinit var btnEnregistrer: MaterialButton
    private lateinit var btnAnnuler: MaterialButton

    private var contact: Contact? = null
    private var position: Int = -1

    private var currentPhotoUri: String? = null
    private var currentDrawableRes: Int? = null

    // Launcher pour la sélection depuis la galerie
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val savedUri = copyUriToInternal(uri)
                currentPhotoUri = savedUri
                currentDrawableRes = null
                ivDetailPhoto.setImageURI(Uri.parse(savedUri))
            } catch (e: Exception) {
                Toast.makeText(this, "Impossible de charger l'image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.msg_photo_annulee, Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher pour la capture avec l'appareil photo
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val savedUri = saveBitmapToInternal(bitmap)
                currentPhotoUri = savedUri
                currentDrawableRes = null
                ivDetailPhoto.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Impossible d'enregistrer la photo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.msg_camera_annulee, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Liaison des vues
        toolbarDetail = findViewById(R.id.toolbarDetail)
        ivDetailPhoto = findViewById(R.id.ivDetailPhoto)
        ivCameraBadge = findViewById(R.id.ivCameraBadge)
        etNom = findViewById(R.id.etNom)
        etPrenom = findViewById(R.id.etPrenom)
        etTelephone = findViewById(R.id.etTelephone)
        btnAppeler = findViewById(R.id.btnAppeler)
        etEmail = findViewById(R.id.etEmail)
        btnEmail = findViewById(R.id.btnEmail)
        btnChoisirPhoto = findViewById(R.id.btnChoisirPhoto)
        btnPrendrePhoto = findViewById(R.id.btnPrendrePhoto)
        btnEnregistrer = findViewById(R.id.btnEnregistrer)
        btnAnnuler = findViewById(R.id.btnAnnuler)

        // Récupération des données transmises par Intent explicite
        position = intent.getIntExtra(EXTRA_POSITION, -1)
        @Suppress("DEPRECATION")
        contact = intent.getSerializableExtra(EXTRA_CONTACT) as? Contact

        setupToolbar()
        populateFields()
        setupListeners()
    }

    private fun setupToolbar() {
        // Flèche retour pour annuler et revenir sans enregistrer
        toolbarDetail.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        // Si nous sommes en mode création (position == -1), masquer l'icône de suppression
        val deleteItem = toolbarDetail.menu.findItem(R.id.action_delete)
        if (position == -1) {
            deleteItem?.isVisible = false
            toolbarDetail.title = "Nouveau contact"
        }

        toolbarDetail.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    confirmerSuppression()
                    true
                }
                else -> false
            }
        }
    }

    private fun populateFields() {
        contact?.let { c ->
            etNom.setText(c.nom)
            etPrenom.setText(c.prenom)
            etTelephone.setText(c.telephone)
            etEmail.setText(c.email)

            currentPhotoUri = c.photoUri
            currentDrawableRes = c.photoDrawableRes

            when {
                !c.photoUri.isNullOrEmpty() -> {
                    try {
                        ivDetailPhoto.setImageURI(Uri.parse(c.photoUri))
                    } catch (e: Exception) {
                        ivDetailPhoto.setImageResource(c.photoDrawableRes ?: R.drawable.ic_default_avatar)
                    }
                }
                c.photoDrawableRes != null -> {
                    ivDetailPhoto.setImageResource(c.photoDrawableRes!!)
                }
                else -> {
                    ivDetailPhoto.setImageResource(R.drawable.ic_default_avatar)
                }
            }
        } ?: run {
            // Mode nouveau contact
            ivDetailPhoto.setImageResource(R.drawable.ic_default_avatar)
        }
    }

    private fun setupListeners() {
        // 6. Intent implicite : composer téléphonique
        btnAppeler.setOnClickListener {
            val tel = etTelephone.text.toString().trim()
            if (tel.isEmpty()) {
                etTelephone.error = getString(R.string.err_telephone_vide)
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$tel")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, R.string.msg_aucune_app_tel, Toast.LENGTH_SHORT).show()
            }
        }

        // 7. Intent implicite : envoyer un e-mail
        btnEmail.setOnClickListener {
            val mail = etEmail.text.toString().trim()
            if (mail.isEmpty()) {
                etEmail.error = "Veuillez saisir une adresse e-mail"
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$mail")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, R.string.msg_aucune_app_mail, Toast.LENGTH_SHORT).show()
            }
        }

        // 8. Galerie photo
        btnChoisirPhoto.setOnClickListener {
            try {
                pickImageLauncher.launch("image/*")
            } catch (e: Exception) {
                Toast.makeText(this, "Impossible d'ouvrir la galerie", Toast.LENGTH_SHORT).show()
            }
        }

        // 9. Appareil photo
        val launchCamera = {
            try {
                takePhotoLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(this, "Appareil photo indisponible", Toast.LENGTH_SHORT).show()
            }
        }
        btnPrendrePhoto.setOnClickListener { launchCamera() }
        ivCameraBadge.setOnClickListener { launchCamera() }

        // 10. Bouton Enregistrer
        btnEnregistrer.setOnClickListener { enregistrerContact() }

        // 11. Bouton Annuler
        btnAnnuler.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun enregistrerContact() {
        val nom = etNom.text.toString().trim()
        val prenom = etPrenom.text.toString().trim()
        val telephone = etTelephone.text.toString().trim()
        val email = etEmail.text.toString().trim()

        var hasError = false
        if (nom.isEmpty()) {
            etNom.error = getString(R.string.err_nom_vide)
            hasError = true
        }
        if (prenom.isEmpty()) {
            etPrenom.error = getString(R.string.err_prenom_vide)
            hasError = true
        }
        if (telephone.isEmpty()) {
            etTelephone.error = getString(R.string.err_telephone_vide)
            hasError = true
        }

        if (hasError) return

        val updatedContact = Contact(
            id = contact?.id ?: System.currentTimeMillis(),
            nom = nom,
            prenom = prenom,
            telephone = telephone,
            email = email,
            photoUri = currentPhotoUri,
            photoDrawableRes = currentDrawableRes
        )

        val resultIntent = Intent().apply {
            putExtra(EXTRA_CONTACT, updatedContact)
            putExtra(EXTRA_POSITION, position)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun confirmerSuppression() {
        val nomAffichage = contact?.nomComplet ?: "ce contact"
        AlertDialog.Builder(this)
            .setTitle(R.string.dlg_supprimer_titre)
            .setMessage(getString(R.string.dlg_supprimer_msg, nomAffichage))
            .setPositiveButton(R.string.dlg_oui) { _, _ ->
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_POSITION, position)
                }
                setResult(RESULT_DELETE, resultIntent)
                finish()
            }
            .setNegativeButton(R.string.dlg_non, null)
            .show()
    }

    private fun copyUriToInternal(sourceUri: Uri): String {
        val file = File(filesDir, "contact_photo_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(sourceUri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return Uri.fromFile(file).toString()
    }

    private fun saveBitmapToInternal(bitmap: Bitmap): String {
        val file = File(filesDir, "contact_photo_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        return Uri.fromFile(file).toString()
    }
}
