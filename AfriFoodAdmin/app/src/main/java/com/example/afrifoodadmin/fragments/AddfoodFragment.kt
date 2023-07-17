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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.afrifoodadmin.R
import com.example.afrifoodadmin.adapter.AddFoodImageAdapter
import com.example.afrifoodadmin.databinding.FragmentAddfoodBinding
import com.example.afrifoodadmin.model.AddFoodModel
import com.example.afrifoodadmin.model.CountryModel
import com.example.afrifoodadmin.model.StoreModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class AddfoodFragment : Fragment() {

    private lateinit var binding : FragmentAddfoodBinding
    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddFoodImageAdapter
    private var coverImage: Uri? = null
    private lateinit var dialog: Dialog
    private var coverImgUrl: String ? = ""
    private lateinit var countryList: ArrayList<String>

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            coverImage = it.data!!.data
            binding.foodCoverImg.setImageURI(coverImage)
            binding.foodCoverImg.visibility = View.VISIBLE

        }
    }

    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddfoodBinding.inflate(layoutInflater)

        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener{
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.foodImgBtn.setOnClickListener{
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)
        }

        setFoodStore()

        adapter = AddFoodImageAdapter(list)
        binding.foodImgRecyclerView.adapter = adapter

        binding.submitFoodBtn.setOnClickListener{
            validateData()
        }

        return binding.root
    }


    private fun validateData() {
        if (binding.foodNameEdt.text.toString().isEmpty()){
            binding.foodNameEdt.requestFocus()
            binding.foodNameEdt.error = "Empty"
        }else if(binding.foodSpEdt.text.toString().isEmpty()){
            binding.foodSpEdt.requestFocus()
            binding.foodSpEdt.error = "Empty"
        }else if (coverImage == null){
            Toast.makeText(requireContext(), "Please select cover image", Toast.LENGTH_SHORT).show()
        }else if (list.size < 1){
            Toast.makeText(requireContext(), "Please select product images", Toast.LENGTH_SHORT).show()
        }else{
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("food/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    coverImgUrl = image.toString()

                    uploadProductImage()

                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()

            }
    }

    private var i = 0
    private fun uploadProductImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("food/$fileName")
        refStorage.putFile(list[i])
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    listImages.add(image!!.toString())
                    if (list.size ==listImages.size) {
                        storeData()
                    }else{
                        i +=1
                        uploadProductImage()
                    }
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()

            }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("food")
        val key = db.document().id

        val data = AddFoodModel(
            binding.foodNameEdt.text.toString(),
            binding.foodDescriptionEdt.text.toString(),
            coverImgUrl.toString(),
            countryList[binding.foodCountryDropdown.selectedItemPosition],
            key,
            binding.foodSpEdt.text.toString(),
            listImages
        )

        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Food Added", Toast.LENGTH_SHORT).show()
            binding.foodNameEdt.text = null
        }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setFoodStore() {
        countryList = ArrayList()
        Firebase.firestore.collection("countries").get().addOnSuccessListener {
            countryList.clear()
            for (doc in it.documents){
                val data = doc.toObject(CountryModel::class.java)
                countryList.add(data!!.ctry!!)
            }
            countryList.add(0, "Select Element of country")
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_layout,countryList)
            binding.foodCountryDropdown.adapter = arrayAdapter
        }
    }

}