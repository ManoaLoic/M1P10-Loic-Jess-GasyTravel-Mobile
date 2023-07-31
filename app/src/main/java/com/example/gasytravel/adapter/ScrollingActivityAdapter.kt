package com.example.gasytravel.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gasytravel.Detail
import com.example.gasytravel.R
import com.example.gasytravel.SettingsActivity
import com.example.gasytravel.databinding.ShowListRawBinding
import com.example.gasytravel.model.TvShow

class ScrollingActivityAdapter(private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<ScrollingActivityAdapter.MainActivityAdapterHolder>() {
    private var tvShowList = ArrayList<TvShow>()
    private var i = 1;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityAdapterHolder {
        return MainActivityAdapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.show_list_raw, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainActivityAdapterHolder, position: Int) {
//        holder.binding.tvTitle.text = "${tvShowList[position].name}"
        Glide
            .with(holder.itemView)
            .load(tvShowList[position].imageThumbnailPath)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.binding.brandingImage)

        holder.bind(tvShowList[position], object : OnItemClickListener {
            override fun onItemClick(context: Context, tvShow: TvShow) {
                // Handle the click event here, e.g., start a new activity
                val intent = Intent(context, Detail::class.java)
                // Pass any data to the new activity if needed
                // intent.putExtra("KEY", value)
                context.startActivity(intent)
            }
        })

    }

    override fun getItemCount(): Int {
        return tvShowList.size
    }

        class MainActivityAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding = ShowListRawBinding.bind(itemView)

            fun bind(tvShow: TvShow, clickListener: OnItemClickListener) {
                Glide.with(itemView)
                    .load(tvShow.imageThumbnailPath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.brandingImage)

                itemView.setOnClickListener {
                    clickListener.onItemClick(itemView.context, tvShow)
                }
            }
        }


    fun updateList(tvShowList: ArrayList<TvShow>, oldCount: Int, tvShowListSize: Int) {
        this.tvShowList = tvShowList
        notifyItemRangeInserted(oldCount, tvShowListSize)
    }
}
interface OnItemClickListener {
    fun onItemClick(context: Context, tvShow: TvShow)
}