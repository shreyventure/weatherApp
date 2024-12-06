package com.example.csci_571_a4

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csci_571_a4.ui.theme.CSCI_571_A4Theme
import com.example.csci_571_a4.ui.theme.*

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.*

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.io.Serializable

class SearchScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val city = intent.getStringExtra("city") ?: "Unknown"
        val state = intent.getStringExtra("state") ?: "N/A"
        val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        setContent {
            CSCI_571_A4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainSearchResultLayout(this, innerPadding, city, state, this, sharedViewModel, owner = this)
                }
            }
        }
    }

    @Composable
    fun MainSearchResultLayout(
        thisContext: Context,
        innerPadding: PaddingValues,
        city: String,
        state: String,
        activity: ComponentActivity,
        sharedViewModel: SharedViewModel,
        owner: ViewModelStoreOwner
    ) {
        var loading by remember { mutableStateOf(true) }
        var data by remember { mutableStateOf("Loading...") }
        var error by remember { mutableStateOf<String?>(null) }
        var weatherData: ApiResponse? = null
        val FAV_PLACES = stringPreferencesKey("fav_places")
        val stringArrayFlow = getStringArray(thisContext, FAV_PLACES) // Call the function
        val placesArray by stringArrayFlow.collectAsState(initial = emptyList())

        LaunchedEffect(Unit) {
            val result =
                fetchDataFromApi("https://csci-571-assignment-3-441420.uw.r.appspot.com/getWeatherInformationFor?street=&city=$city&state=$state")
            if (result != null) {
                data = result
                loading = false
                println("Response: $data")
            } else {
                error = "Failed to fetch data"
            }
        }

        if (error != null) {
            Toast.makeText(thisContext, "API fetch error: $error", Toast.LENGTH_SHORT).show()
            return
        } else {
            weatherData = parseJsonResponse(data)
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = card_bg_dark)
                .fillMaxSize()
        ) {
            HeaderSearchResult(onCitySelected = { selectedCity ->
                // Handle the selected city here
                println("Selected city: $selectedCity")
            }, activity, city, state)
            if (loading) {
                IndeterminateCircularIndicator("Fetching Weather")
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Search Result", style = TextStyle(fontSize = 20.sp))
                }
                Column(modifier = Modifier.background(color = card_wrapper_bg)) {
                    Row {
                        if (weatherData != null) {
                            val dayVal = weatherData.data.timelines[0].intervals[0].values
                            Card1(dayVal.temperature.toString(), dayVal.weatherCode, city, state) {
                                val intent = Intent(activity, DetailsInfo::class.java).apply {
                                    putExtra("data", weatherData.data as Serializable)
                                    putExtra("city", city)
                                    putExtra("state", state)
                                }
                                ContextCompat.startActivity(activity, intent, null)
                            }
                        }
                    }
                    Row {
                        if (weatherData != null) {
                            val dayVal = weatherData.data.timelines[0].intervals[0].values
                            Card2(
                                windSpeed = formatToTwoDecimalPlaces(dayVal.windSpeed),
                                humidity = formatToTwoDecimalPlaces(dayVal.humidity),
                                pressure = formatToTwoDecimalPlaces(dayVal.pressureSeaLevel),
                                visibility = formatToTwoDecimalPlaces(dayVal.visibility)
                            )
                        }
                    }
                    Row {
                        if (weatherData != null) {
                            Card3(weatherData.data.timelines[0].intervals)
                        }
                    }
                }
            }
        }
        if(!loading) FloatingButton(thisContext, FAV_PLACES, city, state, sharedViewModel, owner) { println("clicked") }
    }

    @Composable
    fun HeaderSearchResult(
        onCitySelected: (String) -> Unit,
        activity: ComponentActivity,
        city: String,
        state: String
    ) {
        Row(
            //horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
                .padding(vertical = 20.dp)
        ) {
            Column {
                IconButton(onClick = {
                    val intent = Intent(activity, MainActivity::class.java).apply {
//                        putExtra("city", city)
//                        putExtra("state", state)
                    }
                    ContextCompat.startActivity(activity, intent, null)
                    finish()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White // White icon
                    )
                }
            }
            Column {
                Text(
                    text = "$city, $state", style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }

    }

    @Composable
    fun FloatingButton(
        thisContext: Context,
        FAV_PLACES: Preferences.Key<String>,
        city: String,
        state: String,
        sharedViewModel: SharedViewModel,
        owner: ViewModelStoreOwner,
        onFabClick: () -> Unit
    ) {
        var isFav by remember { mutableStateOf(false) }
        val stringArrayFlow = getStringArray(thisContext, FAV_PLACES)
        val placesArray by stringArrayFlow.collectAsState(initial = emptyList())
        val tempPlaceArr = placesArray.toMutableList()

        Box(
            modifier = Modifier.fillMaxSize(), // Fill the entire screen
        ) {
            FloatingActionButton(
                onClick = {
                    if (!isFav) {
                        println("Adding to favorite!")
                        tempPlaceArr.add("${city}, $state")
                        saveStringArray(thisContext, tempPlaceArr, FAV_PLACES)
                        sharedViewModel.updateState(!sharedViewModel.sharedToggleState)
                        Toast.makeText(thisContext, "${city}, $state was added to favorites", Toast.LENGTH_LONG).show()
                    } else {
                        println("Removing from favorite!")
                        tempPlaceArr.remove("${city}, $state")
                        saveStringArray(thisContext, tempPlaceArr, FAV_PLACES)
                        sharedViewModel.updateState(!sharedViewModel.sharedToggleState)
                        Toast.makeText(thisContext, "${city}, $state was removed from favorites", Toast.LENGTH_LONG).show()
                    }
                    isFav = !isFav
                    onFabClick()
                }, // Handle click action
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Position at the bottom-right
                    .padding(end = 16.dp, bottom = 150.dp), // Add padding from the edges
                containerColor = Color.White, // Customize background color
            ) {
                Image(
                    painter = painterResource(id = if (!isFav) R.drawable.add_fav else R.drawable.rem_fav),
                    contentDescription = if (isFav) "Remove place from Favorites" else "Add place to Favorites"
                )
            }
        }
    }
}