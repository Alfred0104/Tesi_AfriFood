package com.example.afrifoodadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afrifoodadmin.R
import com.example.afrifoodadmin.databinding.ItemStoreLayoutBinding
import com.example.afrifoodadmin.model.StoreModel
//import io.grpc.Context
import android.content.Context
import com.bumptech.glide.Glide


class StoreAdapter(var context : Context, val list : ArrayList<StoreModel>) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {
    inner class  StoreViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var binding = ItemStoreLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        return StoreViewHolder(LayoutInflater.from(context).inflate(R.layout.item_store_layout, parent, false))
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.binding.textView3.text = list[position].store
        Glide.with(context).load(list[position].img).into(holder.binding.imageView3)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}