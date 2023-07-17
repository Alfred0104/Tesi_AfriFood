package com.example.afrifoodadmin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.afrifoodadmin.R
import com.example.afrifoodadmin.adapter.CountryAdapter
import com.example.afrifoodadmin.databinding.FragmentAddcountryBinding
import com.example.afrifoodadmin.model.CountryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class AddcountryFragment : Fragment() {

    private lateinit var binding: FragmentAddcountryBinding
    private var imageUrl: Uri? = null
    private lateinit var dialog: Dialog

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageUrl = it.data!!.data
            binding.imageView.setImageURI(imageUrl)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddcountryBinding.inflate(layoutInflater)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        getData()


        binding.apply {
            imageView.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)

            }
            button9.setOnClickListener {
                validateData(binding.countryName.text.toString())
            }
        }
        return binding.root
    }

    private fun getData() {
        var list = ArrayList<CountryModel>()
        Firebase.firestore.collection("countries")
            .get().addOnSuccessListener {
               list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(CountryModel::class.java)
                    list.add(data!!)

                }
                binding.countryRecycler.adapter= CountryAdapter(requireContext(), list)
            }
    }

    private fun validateData(countryName: String) {
        if(countryName.isEmpty()){
            Toast.makeText(requireContext(),"Please provide country name", Toast.LENGTH_SHORT).show()
        }else if(imageUrl == null){
            Toast.makeText(requireContext(),"Please select image", Toast.LENGTH_SHORT).show()

        }else{
            uploadImage(countryName)
        }
    }

    private fun uploadImage(countryName: String) {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("country/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeData(countryName, image.toString())
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()

            }
    }

    private fun storeData(countryName: String, url: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String, Any>(
            "ctry" to countryName,
            "img" to url
        )

        db.collection("countries").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.preview))
                binding.countryName.text = null
                getData()
                Toast.makeText(requireContext(), "Country added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()


            }
    }


}

