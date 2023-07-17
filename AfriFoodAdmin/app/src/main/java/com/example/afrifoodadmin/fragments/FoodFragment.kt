package com.example.afrifoodadmin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.afrifoodadmin.R
import com.example.afrifoodadmin.databinding.FragmentFoodBinding
import com.example.afrifoodadmin.databinding.FragmentProductBinding


class FoodFragment : Fragment() {

    private lateinit var binding : FragmentFoodBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(layoutInflater)

        binding.floatingActionButton1.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_foodFragment_to_addfoodFragment)
        }
        return binding.root
    }


}