package com.example.cs330_pz.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cs330_pz.R
import com.example.cs330_pz.adapter.DateAdapter
import com.example.cs330_pz.adapter.SeatListAdapter
import com.example.cs330_pz.adapter.TimeAdapter
import com.example.cs330_pz.databinding.ActivitySeatListBinding
import com.example.cs330_pz.model.Film
import com.example.cs330_pz.model.Seat
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var film: Film
    private var price: Double = 0.0
    private var number: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setVariable()
        initTimeDateList()
        initSeatsList()
    }


    private fun initSeatsList() {
        val gridLayoutManager = GridLayoutManager(this, 7)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position % 7 == 3) 1 else 1
            }
        }
        binding.apply{
            seatRecyclerview.layoutManager = gridLayoutManager

            val seatList = mutableListOf<Seat>()
            val numberSeats = 81
            for(i in 0 until numberSeats){
                val seatName = ""
                val seatStatus =
                    if(i == 2 || i == 20 || i == 33 || i == 41 || i == 50 || i == 72 || i == 73) Seat.SeatStatus.UNAVAILABLE else Seat.SeatStatus.AVAILABLE
                seatList.add(Seat(seatStatus, seatName))
            }
            val seatAdapter = SeatListAdapter(seatList, this@SeatListActivity, object: SeatListAdapter.SelectedSeat{
                override fun Return(selectedName: String, num: Int) {
                    numberSelectedTxt.text = "$num Seat Selected"
                    val df = DecimalFormat("#.##")
                    price = df.format(num * film.price).toDouble()
                    number = num
                    priceTxt.text = "$$price"
                }

            })
            seatRecyclerview.adapter = seatAdapter
            seatRecyclerview.isNestedScrollingEnabled = false
        }
    }

    private fun initTimeDateList() {
        binding.apply{
            dateRecyclerview.layoutManager =
                LinearLayoutManager(this@SeatListActivity, LinearLayoutManager.HORIZONTAL, false)
            dateRecyclerview.adapter = DateAdapter(generateDates())

            timeRecyclerview.layoutManager =
                LinearLayoutManager(this@SeatListActivity, LinearLayoutManager.HORIZONTAL, false)
            timeRecyclerview.adapter = TimeAdapter(generateTimeSlots())
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun getIntentExtra() {
        film = intent.getSerializableExtra("film") as Film
    }

    private fun generateDates(): List<String>{
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM")

        for(i in 0 until 7){
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }

    private fun generateTimeSlots(): List<String>{
        val timeSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")

        for(i in 0 until 24 step 2){
            val time = LocalDate.now().atTime(i, 0)
            timeSlots.add(time.format(formatter))
        }
        return timeSlots
    }
}