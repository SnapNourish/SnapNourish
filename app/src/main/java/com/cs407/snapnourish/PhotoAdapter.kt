package com.cs407.snapnourish

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cs407.snapnourish.model.Photo

class PhotoAdapter(private var photos: List<Photo>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        Glide.with(holder.imageView.context).load(photo.imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int = photos.size

    fun updatePhotos(newPhotos: List<ContactsContract.CommonDataKinds.Photo>) {
        notifyDataSetChanged()
    }
}
