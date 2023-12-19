package com.sumeyra.instagramkotlin.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.sumeyra.instagramkotlin.databinding.ActivityUploadActvityBinding
import java.util.UUID

class UploadActvity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadActvityBinding

    private lateinit var goToGalleryLauncher : ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    var imageURI : Uri? = null

    //firebase işlemleri için
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var store: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadActvityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        store = Firebase.storage


    }
    fun uploadImage(view: View){

        //universal unique id
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = store.reference
        val imageReference = reference.child("images").child(imageName)
        if (imageURI != null){
            imageReference.putFile(imageURI!!).addOnSuccessListener{
                //download url -> firestore
                val uploadPictureReference = store.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadURL =it.toString()

                    //firestore a verileri ekleme
                    if(auth.currentUser!=null){
                        val postHashMap = hashMapOf<String,Any>()
                        postHashMap.put("download URL",downloadURL)
                        postHashMap.put("User E-mail",auth.currentUser!!.email!!)
                        postHashMap.put("comment", binding.commentText.text.toString())
                        postHashMap.put("date",Timestamp.now())

                        firestore.collection("Posts").add(postHashMap).addOnSuccessListener {
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActvity, it.localizedMessage,Toast.LENGTH_LONG)
                        }

                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this@UploadActvity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

    fun selectImage(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                Snackbar.make(view,"Permission needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                //request permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }).show()
        }else{
            //request permission
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }

    }else{
        //intent to gallery
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // start activty for result
            goToGalleryLauncher.launch(intent)
    }

    }else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                        //request permission
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                    }).show()
                }else{
                    //request permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }

            }else{
                //intent to gallery
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // start activty for result
                goToGalleryLauncher.launch(intent)
            }

        }

    }
    fun registerLauncher(){
        //go to gallery

        goToGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode== RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    imageURI = intentFromResult.data

                    imageURI?.let{
                        binding.imageView.setImageURI(imageURI)
                    }
                    }
                }
            }

        //request permission
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                goToGalleryLauncher.launch(intentToGallery)
            }else{
                //permission denied
                Toast.makeText(this@UploadActvity, "Permission Needed !",Toast.LENGTH_LONG).show()
            }

        }
    }


}






