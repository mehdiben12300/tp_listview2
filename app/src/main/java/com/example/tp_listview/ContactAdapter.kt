package com.example.tp_listview

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val contacts: List<Contact>
) : ArrayAdapter<Contact>(context, 0, contacts) {

    private class ViewHolder(view: View) {
        val ivPhoto: ImageView = view.findViewById(R.id.ivItemPhoto)
        val tvNomPrenom: TextView = view.findViewById(R.id.tvItemNomPrenom)
        val tvTelephone: TextView = view.findViewById(R.id.tvItemTelephone)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val contact = getItem(position)
        if (contact != null) {
            holder.tvNomPrenom.text = contact.nomComplet
            holder.tvTelephone.text = contact.telephone

            // Gestion de l'affichage de la photo
            when {
                !contact.photoUri.isNullOrEmpty() -> {
                    try {
                        holder.ivPhoto.setImageURI(Uri.parse(contact.photoUri))
                    } catch (e: Exception) {
                        holder.ivPhoto.setImageResource(contact.photoDrawableRes ?: R.drawable.ic_default_avatar)
                    }
                }
                contact.photoDrawableRes != null -> {
                    holder.ivPhoto.setImageResource(contact.photoDrawableRes!!)
                }
                else -> {
                    holder.ivPhoto.setImageResource(R.drawable.ic_default_avatar)
                }
            }
        }

        return view
    }
}
