package com.example.cs330_pz.activity

import android.content.Intent
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cs330_pz.R
import com.example.cs330_pz.model.Film
import com.example.cs330_pz.model.SliderItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private var database: FirebaseDatabase? = null

    private var bannerItems by mutableStateOf<List<SliderItems>>(emptyList())
    private var topMovies by mutableStateOf<List<Film>>(emptyList())
    private var upcomingMovies by mutableStateOf<List<Film>>(emptyList())

    private var bannerLoading by mutableStateOf(true)
    private var topMoviesLoading by mutableStateOf(true)
    private var upcomingMoviesLoading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            database =
                FirebaseDatabase.getInstance("https://cs330-pz1-default-rtdb.europe-west1.firebasedatabase.app")
            initBanner()
            initTopMovies()
            initUpcoming()
        } catch (_: Exception) {
            bannerLoading = false
            topMoviesLoading = false
            upcomingMoviesLoading = false
        }

        setContent {
            MainScreen(
                banners = bannerItems,
                topMovies = topMovies,
                upcomingMovies = upcomingMovies,
                bannerLoading = bannerLoading,
                topMoviesLoading = topMoviesLoading,
                upcomingMoviesLoading = upcomingMoviesLoading,
                onMovieClick = { film ->
                    val intent = Intent(this, DetailFilmActivity::class.java)
                    intent.putExtra("object", film)
                    startActivity(intent)
                }
            )
        }
    }

    private fun initTopMovies() {
        val myRef: DatabaseReference = database?.getReference("Items") ?: run {
            topMoviesLoading = false
            return
        }
        topMoviesLoading = true

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = ArrayList<Film>()
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        val item = i.getValue(Film::class.java)
                        if (item != null) {
                            items.add(item)
                        }
                    }
                }
                topMovies = items
                topMoviesLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                topMoviesLoading = false
            }
        })
    }

    private fun initBanner() {
        val myRef = database?.getReference("Banners") ?: run {
            bannerLoading = false
            return
        }
        bannerLoading = true

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderItems>()
                for (i in snapshot.children) {
                    val list = i.getValue(SliderItems::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                bannerItems = lists
                bannerLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                bannerLoading = false
            }
        })
    }

    private fun initUpcoming() {
        val myRef: DatabaseReference = database?.getReference("Items") ?: run {
            upcomingMoviesLoading = false
            return
        }
        upcomingMoviesLoading = true

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = ArrayList<Film>()
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        val item = i.getValue(Film::class.java)
                        if (item != null) {
                            items.add(item)
                        }
                    }
                }
                upcomingMovies = items
                upcomingMoviesLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                upcomingMoviesLoading = false
            }
        })
    }
}

@Composable
private fun MainScreen(
    banners: List<SliderItems>,
    topMovies: List<Film>,
    upcomingMovies: List<Film>,
    bannerLoading: Boolean,
    topMoviesLoading: Boolean,
    upcomingMoviesLoading: Boolean,
    onMovieClick: (Film) -> Unit
) {
    val black = colorResource(R.color.black)
    val white = colorResource(R.color.white)
    val yellow = colorResource(R.color.yellow)
    val green = colorResource(R.color.green)
    val black2 = colorResource(R.color.black2)
    val scrollState = rememberScrollState()
    val navItems = listOf(
        NavItem(R.drawable.btn_1, "Explorer"),
        NavItem(R.drawable.btn_2, "Favorite"),
        NavItem(R.drawable.btn_3, "Cart"),
        NavItem(R.drawable.btn_4, "Profile")
    )
    var selectedNavIndex by remember { mutableStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredTopMovies = remember(topMovies, searchQuery) {
        filterMovies(topMovies, searchQuery)
    }
    val filteredUpcomingMovies = remember(upcomingMovies, searchQuery) {
        filterMovies(upcomingMovies, searchQuery)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            MainHeader()
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            BannerSection(banners = banners, loading = bannerLoading)

            SectionTitle(title = "Top Movies", action = stringResource(R.string.see_all), titleColor = yellow, actionColor = white)
            MovieList(
                items = filteredTopMovies,
                loading = topMoviesLoading,
                emptyText = if (searchQuery.isBlank()) "No movies available" else "No movies found",
                onMovieClick = onMovieClick
            )

            SectionTitle(
                title = stringResource(R.string.upcoming_movies),
                action = stringResource(R.string.see_all),
                titleColor = yellow,
                actionColor = white
            )
            MovieList(
                items = filteredUpcomingMovies,
                loading = upcomingMoviesLoading,
                emptyText = if (searchQuery.isBlank()) "No movies available" else "No movies found",
                onMovieClick = onMovieClick
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = navItems[selectedNavIndex].title,
                color = white,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            BottomNavBar(
                modifier = Modifier,
                containerColor = black2,
                selectedColor = green,
                items = navItems,
                selectedIndex = selectedNavIndex,
                onItemClick = { index -> selectedNavIndex = index }
            )
        }
    }
}

@Composable
private fun MainHeader() {
    val white = colorResource(R.color.white)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.profile),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "Hello Pitter Jackson",
                color = white,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "pitterjackson",
                color = white,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val white = colorResource(R.color.white)
    val black2 = colorResource(R.color.black2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(black2)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.search),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            cursorBrush = SolidColor(white),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = white,
                fontSize = 16.sp
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (query.isBlank()) {
                    Text(
                        text = "Search Movies",
                        color = white.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )
        Image(
            painter = painterResource(R.drawable.microphone),
            contentDescription = null
        )
    }
}

@Composable
private fun BannerSection(banners: List<SliderItems>, loading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator()
            return
        }

        if (banners.isEmpty()) {
            return
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 60.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(banners) { banner ->
                AsyncImage(
                    model = banner.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(280.dp)
                        .height(300.dp)
                        .clip(RoundedCornerShape(30.dp))
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    action: String,
    titleColor: Color,
    actionColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = action,
            color = actionColor,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MovieList(
    items: List<Film>,
    loading: Boolean,
    emptyText: String,
    onMovieClick: (Film) -> Unit
) {
    val white = colorResource(R.color.white)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator()
            return
        }

        if (items.isEmpty()) {
            Text(
                text = emptyText,
                color = white
            )
            return
        }

        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
            items(items) { movie ->
                MovieCard(item = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}

@Composable
private fun MovieCard(item: Film, onClick: () -> Unit) {
    val white = colorResource(R.color.white)

    Column(
        modifier = Modifier
            .padding(6.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = item.Poster,
            contentDescription = item.Title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(30.dp))
        )

        Text(
            text = item.Title.orEmpty(),
            color = white,
            maxLines = 2,
            modifier = Modifier
                .width(140.dp)
                .padding(start = 4.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    containerColor: Color,
    selectedColor: Color,
    items: List<NavItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (index == selectedIndex) selectedColor else Color.Transparent)
                    .clickable { onItemClick(index) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(item.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private data class NavItem(
    val iconRes: Int,
    val title: String
)

private fun filterMovies(items: List<Film>, query: String): List<Film> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) {
        return items
    }

    return items.filter { film ->
        film.Title?.contains(normalizedQuery, ignoreCase = true) == true
    }
}
