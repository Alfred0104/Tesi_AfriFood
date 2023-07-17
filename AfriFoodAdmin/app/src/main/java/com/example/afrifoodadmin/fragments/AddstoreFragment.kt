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
import com.example.afrifoodadmin.adapter.StoreAdapter
import com.example.afrifoodadmin.databinding.FragmentAddstoreBinding
import com.example.afrifoodadmin.model.StoreModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
//import java.util.Locale.Category
import kotlin.collections.ArrayList

class AddstoreFragment : Fragment() {


    private lateinit var binding: FragmentAddstoreBinding
    private var imageUrl : Uri? = null
    private lateinit var dialog : Dialog

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            imageUrl = it.data!!.data
            binding.imageView.setImageURI(imageUrl)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddstoreBinding.inflate(layoutInflater)

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
            button10.setOnClickListener {
                validateData(binding.storeName.text.toString())
            }
        }


        return binding.root
    }

    private fun getData() {
        var list = ArrayList<StoreModel>()
        Firebase.firestore.collection("storeElements")
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents){
                    val data = doc.toObject(StoreModel::class.java)
                    list.add(data!!)
                }
                binding.storeRecycler.adapter = StoreAdapter(requireContext(),list)
            }
    }

    private fun validateData(storeName: String) {
        if(storeName.isEmpty()){
            Toast.makeText(requireContext(),"Please provide element of store name", Toast.LENGTH_SHORT).show()
        }else if(imageUrl == null){
            Toast.makeText(requireContext(),"Please select image", Toast.LENGTH_SHORT).show()
        }else {
            uploadImage(storeName)
        }
    }

    private fun uploadImage(storeName: String) {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("stores/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeData(storeName, image.toString())
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()

            }
    }

    private fun storeData(storeName: String, url: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String, Any>(
            "store" to storeName,
            "img" to url
        )

        db.collection("storeElements").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.preview))
                binding.storeName.text = null
                getData()
                Toast.makeText(requireContext(), "Store Element added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()


            }
    }


}