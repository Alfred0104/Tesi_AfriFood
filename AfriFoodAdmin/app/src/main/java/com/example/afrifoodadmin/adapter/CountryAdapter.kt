package com.example.afrifoodadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afrifoodadmin.databinding.ItemCountryLayoutBinding
import com.example.afrifoodadmin.model.CountryModel
import com.example.afrifoodadmin.R
//import java.util.Collections.list

class CountryAdapter(var context: Context, val list : ArrayList<CountryModel>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    inner class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var binding = ItemCountryLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
     return CountryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_country_layout, parent, false))
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.binding.textView2.text= list[position].ctry
        Glide.with(context).load(list[position].img).into(holder.binding.imageView2)
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}