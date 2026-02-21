package com.example.cs330_pz.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs330_pz.R
import com.example.cs330_pz.model.Film
import com.example.cs330_pz.model.Seat
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity() {
    private lateinit var film: Film

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        film = intent.getSerializableExtra("film") as Film

        setContent {
            SeatListScreen(
                film = film,
                dates = generateDates(),
                timeSlots = generateTimeSlots(),
                onBackClick = { finish() }
            )
        }
    }

    private fun generateDates(): List<String> {
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM")
        for (i in 0 until 7) {
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }

    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        for (i in 0 until 24 step 2) {
            val time = LocalDate.now().atTime(i, 0)
            timeSlots.add(time.format(formatter))
        }
        return timeSlots
    }
}

@Composable
private fun SeatListScreen(
    film: Film,
    dates: List<String>,
    timeSlots: List<String>,
    onBackClick: () -> Unit
) {
    val black = colorResource(R.color.black)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)
    val green = colorResource(R.color.green)

    var selectedDate by remember { mutableIntStateOf(-1) }
    var selectedTime by remember { mutableIntStateOf(-1) }

    val unavailableSeats = remember { setOf(2, 20, 33, 41, 50, 72, 73) }
    val seatStates = remember {
        mutableStateListOf<Seat.SeatStatus>().apply {
            repeat(81) { index ->
                add(if (index in unavailableSeats) Seat.SeatStatus.UNAVAILABLE else Seat.SeatStatus.AVAILABLE)
            }
        }
    }

    val selectedCount by remember {
        derivedStateOf { seatStates.count { it == Seat.SeatStatus.SELECTED } }
    }
    val formattedPrice by remember(selectedCount, film.price) {
        derivedStateOf { DecimalFormat("#.##").format(selectedCount * film.price) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.back_2),
                contentDescription = null,
                modifier = Modifier.clickable(onClick = onBackClick)
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Selected Seats",
                    color = white,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        DateRow(
            dates = dates,
            selectedIndex = selectedDate,
            onSelected = { selectedDate = it }
        )

        TimeRow(
            times = timeSlots,
            selectedIndex = selectedTime,
            onSelected = { selectedTime = it }
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.cinema),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )

            SeatGrid(
                seatStates = seatStates,
                onSeatClick = { index ->
                    when (seatStates[index]) {
                        Seat.SeatStatus.AVAILABLE -> seatStates[index] = Seat.SeatStatus.SELECTED
                        Seat.SeatStatus.SELECTED -> seatStates[index] = Seat.SeatStatus.AVAILABLE
                        Seat.SeatStatus.UNAVAILABLE -> {}
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, end = 48.dp, top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(colorRes = R.color.black2, label = "Available")
            LegendItem(colorRes = R.color.blue, label = "Selected")
            LegendItem(color = androidx.compose.ui.graphics.Color(0xFFBEBEBE), label = "Unavailable")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$$formattedPrice",
                    color = white,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$selectedCount Seat Selected",
                    color = grey,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {},
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = white
                )
            ) {
                Text(text = "Download Ticket")
            }
        }
    }
}

@Composable
private fun DateRow(
    dates: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(dates) { index, date ->
            val parts = date.split("/")
            val isSelected = selectedIndex == index
            val bgColor = if (isSelected) colorResource(R.color.orange) else colorResource(R.color.black2)
            val textColor = if (isSelected) colorResource(R.color.black) else colorResource(R.color.white)

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(65.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .clickable { onSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = parts.getOrElse(0) { "-" },
                    color = textColor,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Image(
                    painter = painterResource(R.drawable.dash_line_black),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${parts.getOrElse(1) { "-" }} ${parts.getOrElse(2) { "-" }}",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TimeRow(
    times: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(times) { index, time ->
            val isSelected = selectedIndex == index
            val bgColor = if (isSelected) colorResource(R.color.yellow) else colorResource(R.color.black2)
            val textColor = if (isSelected) colorResource(R.color.black) else colorResource(R.color.white)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .height(33.dp)
                    .clickable { onSelected(index) }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun SeatGrid(
    seatStates: List<Seat.SeatStatus>,
    onSeatClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 48.dp, top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        seatStates.chunked(7).forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { columnIndex, status ->
                    val index = rowIndex * 7 + columnIndex
                    SeatCell(
                        status = status,
                        onClick = { onSeatClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SeatCell(status: Seat.SeatStatus, onClick: () -> Unit) {
    val color = when (status) {
        Seat.SeatStatus.AVAILABLE -> colorResource(R.color.black2)
        Seat.SeatStatus.SELECTED -> colorResource(R.color.blue)
        Seat.SeatStatus.UNAVAILABLE -> androidx.compose.ui.graphics.Color(0xFFBEBEBE)
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(30.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color)
            .clickable(enabled = status != Seat.SeatStatus.UNAVAILABLE, onClick = onClick)
    )
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.LegendItem(
    colorRes: Int? = null,
    color: androidx.compose.ui.graphics.Color? = null,
    label: String
) {
    val white = colorResource(R.color.white)
    val itemColor = color ?: colorResource(checkNotNull(colorRes))

    Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(itemColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = white,
            fontSize = 12.sp
        )
    }
}
