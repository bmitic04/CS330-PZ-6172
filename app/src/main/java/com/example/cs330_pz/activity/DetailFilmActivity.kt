package com.example.cs330_pz.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cs330_pz.R
import com.example.cs330_pz.model.Cast
import com.example.cs330_pz.model.Film

class DetailFilmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val film = intent.getSerializableExtra("object") as Film

        setContent {
            DetailFilmScreen(
                film = film,
                onBackClick = { finish() },
                onBuyTicketClick = {
                    val seatIntent = Intent(this, SeatListActivity::class.java)
                    seatIntent.putExtra("film", film)
                    startActivity(seatIntent)
                }
            )
        }
    }
}

@Composable
private fun DetailFilmScreen(
    film: Film,
    onBackClick: () -> Unit,
    onBuyTicketClick: () -> Unit
) {
    val black = colorResource(R.color.black)
    val white = colorResource(R.color.white)
    val green = colorResource(R.color.green)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = film.Poster,
                contentDescription = film.Title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            )

            Image(
                painter = painterResource(R.drawable.back_dark),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, top = 48.dp)
                    .clickable(onClick = onBackClick)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = film.Title.orEmpty(),
                    color = white,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(R.drawable.bookmark),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Image(
                    painter = painterResource(R.drawable.share),
                    contentDescription = null
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(film.Genre) { genre ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorResource(R.color.blue))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = genre,
                            color = white,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.56f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${film.Year} - ${film.Time}",
                    color = white,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "IMDB ${film.Imdb}",
                    color = white
                )
            }

            Text(
                text = stringResource(R.string.summery),
                color = white,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = film.Description.orEmpty(),
                color = white,
                fontSize = 14.sp
            )

            Text(
                text = stringResource(R.string.cast),
                color = white,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            CastRow(casts = film.Casts)
        }

        Button(
            onClick = onBuyTicketClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = green,
                contentColor = white
            )
        ) {
            Text(
                text = stringResource(R.string.buy_ticket),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun CastRow(casts: List<Cast>) {
    val white = colorResource(R.color.white)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(casts) { cast ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = cast.PicUrl,
                    contentDescription = cast.Actor,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = cast.Actor.orEmpty(),
                    color = white,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
