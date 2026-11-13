package com.example.tp_listview

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private val contacts = mutableListOf(
        Contact(
            nom = "Dupont",
            prenom = "Claire",
            telephone = "06 12 34 56 78",
            email = "claire.dupont@example.com",
            photoDrawableRes = R.drawable.avatar_claire
        ),
        Contact(
            nom = "Martin",
            prenom = "Thomas",
            telephone = "07 23 45 67 89",
            email = "thomas.martin@example.com",
            photoDrawableRes = R.drawable.avatar_thomas
        ),
        Contact(
            nom = "Bernard",
            prenom = "Sophie",
            telephone = "06 98 76 54 32",
            email = "sophie.bernard@example.com",
            photoDrawableRes = R.drawable.avatar_sophie
        ),
        Contact(
            nom = "Petit",
            prenom = "Lucas",
            telephone = "07 11 22 33 44",
            email = "lucas.petit@example.com",
            photoDrawableRes = R.drawable.avatar_lucas
        ),
        Contact(
            nom = "Moreau",
            prenom = "Julien",
            telephone = "06 55 44 33 22",
            email = "julien.moreau@example.com",
            photoDrawableRes = R.drawable.avatar_julien
        )
    )

    private lateinit var adapter: ContactAdapter
    private lateinit var lvContacts: ListView
    private lateinit var toolbarMain: MaterialToolbar

    // Réception du retour d'activité (DetailActivity)
    private val detailActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                val data = result.data ?: return@registerForActivityResult
                @Suppress("DEPRECATION")
                val updatedContact = data.getSerializableExtra(DetailActivity.EXTRA_CONTACT) as? Contact ?: return@registerForActivityResult
                val position = data.getIntExtra(DetailActivity.EXTRA_POSITION, -1)

                if (position in contacts.indices) {
                    // 10. Modification d'un contact existant
                    contacts[position] = updatedContact
                    adapter.notifyDataSetChanged()
                    Toast.makeText(
                        this,
                        getString(R.string.msg_contact_modifie, updatedContact.nomComplet),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (position == -1) {
                    // Ajout d'un nouveau contact
                    contacts.add(updatedContact)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(
                        this,
                        getString(R.string.msg_contact_ajoute, updatedContact.nomComplet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            DetailActivity.RESULT_DELETE -> {
                val data = result.data ?: return@registerForActivityResult
                val position = data.getIntExtra(DetailActivity.EXTRA_POSITION, -1)
                if (position in contacts.indices) {
                    contacts.removeAt(position)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, R.string.msg_contact_supprime, Toast.LENGTH_SHORT).show()
                }
            }
            RESULT_CANCELED -> {
                // 11. Annulation : la liste reste inchangée
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Gestion des marges système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        toolbarMain = findViewById(R.id.toolbarMain)
        lvContacts = findViewById(R.id.lvContacts)

        // 3. ListView personnalisée avec son Custom Adapter
        adapter = ContactAdapter(this, contacts)
        lvContacts.adapter = adapter

        // 4. Clic sur un contact -> Intent explicite vers DetailActivity
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = contacts[position]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_CONTACT, selectedContact)
                putExtra(DetailActivity.EXTRA_POSITION, position)
            }
            detailActivityLauncher.launch(intent)
        }

        // Bouton "+" pour ajouter un nouveau contact
        toolbarMain.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add -> {
                    val intent = Intent(this, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_POSITION, -1)
                    }
                    detailActivityLauncher.launch(intent)
                    true
                }
                else -> false
            }
        }
    }
}
