package com.cs407.snapnourish

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cs407.snapnourish.databinding.ActivityItemImageBinding
import java.net.URL

//class ItemImage : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_item_image)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}

data class ItemImage(
    //or val imageURL: URL
    val photoURL: String
)

class ItemImageAdapter(private var images: MutableList<ItemImage>) :
        RecyclerView.Adapter<ItemImageAdapter.ImageViewHolder>() {

        inner class ImageViewHolder(val binding: ActivityItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ActivityItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = images[position]

        // Load image with Glide
        Glide.with(holder.binding.root.context)
            .load(item.photoURL)
            .into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = images.size

    fun updateData(newImages: List<ItemImage>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

}