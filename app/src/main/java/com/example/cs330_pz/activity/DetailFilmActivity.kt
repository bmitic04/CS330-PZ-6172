package com.example.cs330_pz.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.cs330_pz.adapter.CastListAdapter
import com.example.cs330_pz.adapter.GenreEachFilmAdapter
import com.example.cs330_pz.databinding.ActivityDeatailFilmBinding
import com.example.cs330_pz.model.Film
import eightbitlab.com.blurview.RenderScriptBlur

class DetailFilmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeatailFilmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeatailFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setVariable()
    }

    private fun setVariable() {
        val film = intent.getSerializableExtra("object") as Film
        val requestOptions = RequestOptions().transform(CenterCrop(),
            GranularRoundedCorners(0f, 0f, 50f, 50f))

        Glide.with(this)
            .load(film.Poster)
            .apply(requestOptions)
            .into(binding.filmPic)

        binding.titleTxt.text = film.Title
        binding.imdbTxt.text = "IMDB ${film.Imdb}"
        binding.movieTimeTxt.text = "${film.Year} - ${film.Time}"
        binding.movieSummeryTxt.text = film.Description

        binding.backBtn.setOnClickListener {
            finish()
        }
        val radius = 10f
        val decorView = window.decorView
        var rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowsBackground = decorView.background

        binding.blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowsBackground)
            .setBlurRadius(radius)
        binding.blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        binding.blurView.clipToOutline = true

        film.Genre?.let{
            binding.genreView.adapter = GenreEachFilmAdapter(it)
            binding.genreView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        film.Casts?.let{
            binding.castListView.layoutManager=
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.castListView.adapter = CastListAdapter(it)
        }

        binding.buyTicketBtn.setOnClickListener {
            val intent= Intent(this, SeatListActivity::class.java)
            intent.putExtra("film", film)
            startActivity(intent)
        }
    }
}