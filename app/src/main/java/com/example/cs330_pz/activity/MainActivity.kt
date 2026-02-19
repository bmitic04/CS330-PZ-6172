package com.example.cs330_pz.activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.cs330_pz.adapter.FilmListAdapter
import com.example.cs330_pz.adapter.SliderAdapter
import com.example.cs330_pz.databinding.ActivityMainBinding
import com.example.cs330_pz.model.Film
import com.example.cs330_pz.model.SliderItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private val sliderHandle = Handler()
    private val sliderRunnable= Runnable{
        binding.viewPager2.currentItem=binding.viewPager2.currentItem + 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance("https://cs330-pz1-default-rtdb.europe-west1.firebasedatabase.app")

        initBanner()
        initTopMovies()
        initUpcoming()
    }

    private fun initTopMovies() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBarTopMovies.visibility = View.VISIBLE
        val items = ArrayList<Film>()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(i in snapshot.children){
                        val item = i.getValue(Film::class.java)
                        if(item!=null){
                            items.add(item)
                        }
                    }
                    if(items.isNotEmpty()){
                        binding.recyclerViewTopMovies.layoutManager =
                            LinearLayoutManager(this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recyclerViewTopMovies.adapter = FilmListAdapter(items)
                    }
                    binding.progressBarTopMovies.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initBanner(){
        val myRef = database.getReference("Banners")
        binding.progressBarSlider.visibility = View.VISIBLE

        myRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderItems>()
                for(i in snapshot.children){
                    val list = i.getValue(SliderItems::class.java)
                    if(list != null){
                        lists.add(list)
                    }
                }
                binding.progressBarSlider.visibility = View.GONE
                banners(lists)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
    }
    private fun banners(lists: MutableList<SliderItems>){
        binding.viewPager2.adapter = SliderAdapter(lists, binding.viewPager2)
        binding.viewPager2.clipToPadding=false
        binding.viewPager2.clipChildren=false
        binding.viewPager2.offscreenPageLimit=3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer= CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer {page, position ->
                var r = 1 - abs(position)
                page.scaleY=0.85f + r * 0.15f

            }
        }
        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.currentItem = 1
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandle.removeCallbacks(sliderRunnable)
            }
        })

    }


    private fun initUpcoming() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBarTopUpcoming.visibility = View.VISIBLE
        val items = ArrayList<Film>()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(i in snapshot.children){
                        val item = i.getValue(Film::class.java)
                        if(item!=null){
                            items.add(item)
                        }
                    }
                    if(items.isNotEmpty()){
                        binding.recyclerViewUpcoming.layoutManager =
                            LinearLayoutManager(this@MainActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        binding.recyclerViewUpcoming.adapter = FilmListAdapter(items)
                    }
                    binding.progressBarTopUpcoming.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}