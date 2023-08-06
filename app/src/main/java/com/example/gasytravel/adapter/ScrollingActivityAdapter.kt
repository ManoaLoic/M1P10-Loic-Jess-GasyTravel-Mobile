package com.example.gasytravel.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gasytravel.Detail
import com.example.gasytravel.FicheActivity
import com.example.gasytravel.R
import com.example.gasytravel.databinding.ShowListRawBinding
import com.example.gasytravel.model.Post

class ScrollingActivityAdapter(private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<ScrollingActivityAdapter.MainActivityAdapterHolder>() {
    private var tvShowList = ArrayList<Post>()
    private var i = 1;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivityAdapterHolder {
        return MainActivityAdapterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.show_list_raw, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainActivityAdapterHolder, position: Int) {
        val post : Post = tvShowList[position]
        holder.binding.titre.text = post.titre
        holder.binding.type.text = post.type
        holder.binding.prix.text = "${post.prix} ${post.unite}"
        holder.binding.description.text = HtmlCompat.fromHtml(post.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        Glide
            .with(holder.itemView)
            .load(post.brand)
            .centerCrop()
            .placeholder(R.drawable.loading)
            .into(holder.binding.brandingImage)

        holder.bind(post, object : OnItemClickListener {
            override fun onItemClick(context: Context, post: Post) {
                val intent = Intent(context, FicheActivity::class.java)
                intent.putExtra("id", post.id)
                context.startActivity(intent)
            }
        })

    }

    override fun getItemCount(): Int {
        return tvShowList.size
    }

        class MainActivityAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding = ShowListRawBinding.bind(itemView)

            fun bind(tvShow: Post, clickListener: OnItemClickListener) {
                Glide.with(itemView)
                    .load(tvShow.brand)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.brandingImage)

                itemView.setOnClickListener {
                    clickListener.onItemClick(itemView.context, tvShow)
                }
            }
        }


    fun updateList(tvShowList: ArrayList<Post>, oldCount: Int, tvShowListSize: Int, add : Boolean) {
        this.tvShowList = tvShowList
        notifyItemRangeInserted(oldCount, tvShowListSize)
        if(!add){
            notifyDataSetChanged()
        }
    }
}
interface OnItemClickListener {
    fun onItemClick(context: Context, tvShow: Post)
}