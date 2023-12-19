package com.sumeyra.instagramkotlin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.sumeyra.instagramkotlin.R
import com.sumeyra.instagramkotlin.adapter.PostAdapter
import com.sumeyra.instagramkotlin.databinding.ActivityFeedBinding
import com.sumeyra.instagramkotlin.model.Post

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postList :ArrayList<Post>
    private lateinit var postAdapter : PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth= Firebase.auth
        db = Firebase.firestore

        postList= ArrayList<Post>()
        getData()

        //recycler view işlemleri
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter= PostAdapter(postList)
        binding.recyclerView.adapter = postAdapter
    }

    private fun getData() {
        db.collection(  "Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null){
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{

                if (!value!!.isEmpty){
                    //empty değilse
                    val documents = value.documents

                    postList.clear()
                    for (document in documents){
                        val comment = document.get("comment") as String
                        val useremail = document.get("User E-mail") as String
                        val downloadURL = document.get("download URL") as String

                        val post = Post(useremail,comment,downloadURL)
                        postList.add(post)

                    }
                    postAdapter.notifyDataSetChanged()
                }
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.upload_photo,menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //intent
        if(item.itemId == R.id.upload_photo){
            val intent = Intent(this@FeedActivity, UploadActvity::class.java)
            startActivity(intent)
        }else if (item.itemId== R.id.signout){
            auth.signOut()
            val intent = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}