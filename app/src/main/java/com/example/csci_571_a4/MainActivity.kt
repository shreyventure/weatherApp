package com.example.csci_571_a4

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.widget.Autocomplete
import androidx.compose.ui.unit.sp
import com.example.csci_571_a4.ui.theme.CSCI_571_A4Theme
import com.example.csci_571_a4.ui.theme.*

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.Serializable

import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.pager.*
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map

// Create a DataStore instance
val Context.dataStore by preferencesDataStore(name = "local_storage")

data class WeatherData(val date: String, val weatherSymbol: String, val minTemp: String, val maxTemp: String)
val weatherCodeToLabelMap = mapOf(
    1000 to "Clear",
    1100 to "Mostly Clear",
    1101 to "Partly Cloudy",
    1102 to "Mostly Cloudy",
    1001 to "Cloudy",
    2000 to "Fog",
    2100 to "Light Fog",
    8000 to "Thunderstorm",
    5001 to "Flurries",
    5100 to "Light Snow",
    5000 to "Snow",
    5101 to "Heavy Snow",
    7102 to "Light Ice Pellets",
    7000 to "Ice Pellets",
    7101 to "Heavy Ice Pellets",
    4000 to "Drizzle",
    6000 to "Freezing Drizzle",
    6200 to "Light Freezing Roin",
    6001 to "Freezing Rain",
    6201 to "Heavy Freezing Rain",
    4200 to "Light Rain",
    4001 to "Rain",
    4201 to "Heavy Rain",
);

val weatherCodeToSymbMap = mapOf(
    1000 to R.drawable.clear_day,
    1100 to R.drawable.mostly_clear_day,
    1101 to R.drawable.partly_cloudy_day,
    1102 to R.drawable.mostly_cloudy,
    1001 to R.drawable.cloudy,
    2000 to R.drawable.fog,
    2100 to R.drawable.fog_light,
    8000 to R.drawable.tstorm,
    5001 to R.drawable.flurries,
    5100 to R.drawable.snow_light,
    5000 to R.drawable.snow,
    5101 to R.drawable.snow_heavy,
    7102 to R.drawable.ice_pellets_light,
    7000 to R.drawable.ice_pellets,
    7101 to R.drawable.ice_pellets_heavy,
    4000 to R.drawable.drizzle,
    6000 to R.drawable.freezing_drizzle,
    6200 to R.drawable.freezing_rain_light,
    6001 to R.drawable.freezing_rain,
    6201 to R.drawable.freezing_rain_heavy,
    4200 to R.drawable.rain_light,
    4001 to R.drawable.rain,
    4201 to R.drawable.rain_heavy,
);
data class ApiResponse(
    val data: Data,
    val formatted_address: String
): Serializable

data class Data(
    val timelines: List<Timeline>
): Serializable

data class Timeline(
    val timestep: String,
    val startTime: String,
    val endTime: String,
    val intervals: List<Interval>
): Serializable

data class Interval(
    val startTime: String,
    val values: Values
): Serializable

data class Values(
    val cloudCover: Double,
    val humidity: Double,
    val moonPhase: Int,
    val precipitationProbability: Int,
    val precipitationType: Int,
    val pressureSeaLevel: Double,
    val sunriseTime: String,
    val sunsetTime: String,
    val temperature: Double,
    val temperatureApparent: Double,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val uvIndex: Int,
    val visibility: Double,
    val weatherCode: Int,
    val windDirection: Double,
    val windSpeed: Double
): Serializable
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var placesClient: PlacesClient

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private var selectedCityName = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places API
        Places.initialize(applicationContext, "AIzaSyBoN6Y3CYEL2BDyBNq5gHgYEiig36PBlJ4", Locale("en")) // Replace with your API key
        placesClient = Places.createClient(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
        val sharedViewModel: SharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            CSCI_571_A4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    MainLayout(this, innerPadding, "Los Angeles", "CA", this, onSearch = {launchAutocomplete()}, selectedCityName = selectedCityName.value, placesClient = placesClient, this, sharedViewModel)

                }
            }
        }
    }

    private fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        // Check and request permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
//                Toast.makeText(this, "Lat: $latitude, Lon: $longitude", Toast.LENGTH_LONG).show()
            } else {
//                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    selectedCityName.value = place.name ?: "Unknown Location"
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                Log.i(TAG, "User canceled autocomplete")
            }
        }

    // Launch Autocomplete Activity
    private fun launchAutocomplete() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this)
        startAutocomplete.launch(intent)
    }
}

class SharedViewModel : ViewModel() {
    var sharedToggleState by mutableStateOf(true) // Use mutableStateOf for Compose state
        private set

    fun updateState(newState: Boolean) {
        sharedToggleState = newState
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(onCitySelected: (String) -> Unit, activity: ComponentActivity, onSearch: () -> Unit, selectedCityName: String, placesClient: PlacesClient, context: Context) {
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    if (isSearching) {
        // Custom search bar when in search mode
        SearchBarWithDropdown(
            query = query,
            onQueryChanged = {
                query = it
                if (it.isNotEmpty()) {
                    fetchPredictions(it, placesClient) { result ->
                        predictions = result
                        isDropdownExpanded = predictions.isNotEmpty()
                    }
                } else {
                    isDropdownExpanded = false
                    predictions = emptyList()
                }
            },
            onBackClicked = {
                isSearching = false
                query = ""
                isDropdownExpanded = false
                predictions = emptyList()
            },
            onCloseClicked = {
                query = ""
                isDropdownExpanded = false
                predictions = emptyList()
            },
            isDropdownExpanded = isDropdownExpanded,
            predictions = predictions,
            onPredictionClicked = { prediction ->
                println("Selected: ${prediction.getPrimaryText(null)}")
                query = prediction.getFullText(null).toString()
                isDropdownExpanded = false

                val parts = query.split(", ")
                if (parts.size >= 2) {
                    city = parts[0]
                    state = parts[1]
                    println("$city, $state")
                } else {
                    city = query // Default to the whole text if it doesn't have a comma
                    state = ""
                    println("$city, $state")
                }
                val intent = Intent(activity, SearchScreen::class.java).apply {
                    putExtra("city", city)
                    putExtra("state", state)
                }
                startActivity(activity, intent, null)
            }
        )
    } else {
        // Default top bar
        DefaultTopBar(onSearchClicked = { isSearching = true })
    }
}

@Composable
fun MainLayout(thisContext: Context, innerPadding: PaddingValues, city: String, state: String, activity: ComponentActivity, onSearch: () -> Unit, selectedCityName: String, placesClient: PlacesClient, owner: ViewModelStoreOwner, sharedViewModel: SharedViewModel) {
    var loading by remember { mutableStateOf(true) }
    var data by remember { mutableStateOf("Loading...") }
    var error by remember { mutableStateOf<String?>(null) }
    var weatherData: ApiResponse? = null
    var weatherDataData: Data? = null
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var results = remember { mutableStateListOf<String>() }
    val FAV_PLACES = stringPreferencesKey("fav_places")
    val stringArrayFlow = getStringArray(thisContext, FAV_PLACES) // Call the function
    val placesArray by stringArrayFlow.collectAsState(initial = emptyList())
    var toggleFlag by remember { mutableStateOf(true) }

    LaunchedEffect(Unit, sharedViewModel.sharedToggleState) {
        loading = true
//        clearStringArray(thisContext, FAV_PLACES)
        val favResults = mutableListOf<String>()
        val result =
            fetchDataFromApi("https://csci-571-assignment-3-441420.uw.r.appspot.com/getWeatherInformationFor?street=&city=$city&state=$state")
        if (result != null) {
            data = result
            favResults.add(data)
            println("Response: $data")
        } else {
            error = "Failed to fetch data"
        }
        println("placesArray -> ${placesArray}")
        placesArray.forEach { item ->
            val parts = item.split(",").map { it.trim() } // Split by comma and trim spaces
            if (parts.size == 2) {
                val favCity = parts[0]
                val favState = parts[1]

                val favResult =
                    fetchDataFromApi("https://csci-571-assignment-3-441420.uw.r.appspot.com/getWeatherInformationFor?street=&city=$favCity&state=$favState")
                if (favResult != null) favResults.add(favResult)
            }
        }
        loading = false
        println("favResS -> ${favResults.size}")
        println("favRes -> $favResults")
        results.clear()
        results.addAll(favResults)
    }
    if (error != null) {
        Toast.makeText(thisContext, "API fetch error: $error", Toast.LENGTH_SHORT).show()
        return
    } else {
        //weatherData = parseJsonResponse(data)
    }

    Column(modifier = Modifier
//        .padding(innerPadding)
        .background(color = card_bg_dark)
//        .fillMaxSize()
    ) {
        Header(onCitySelected = { selectedCity ->
            // Handle the selected city here
            println("Selected city: $selectedCity")
        }, activity, onSearch = onSearch, selectedCityName, placesClient, thisContext)
        if (loading) {
            IndeterminateCircularIndicator("Fetching Weather")
        } else {
            println("res -> $results")
            println("size -> ${results.size}")
            if(results.size > 0) PagerWithDots(thisContext, innerPadding, "Los Angeles", "California", activity, results, FAV_PLACES, sharedViewModel, owner)
        }
    }
}

@Composable
fun Card1(temp: String, weatherCode: Int, city: String, state: String, modifier: Modifier = Modifier, onCardClick: () -> Unit) {
    Card(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, // Tracks interactions
                indication = rememberRipple(bounded = true), // Adds ripple effect
                onClick = onCardClick
            ),
        shape = RoundedCornerShape(0.dp), // Sharp corners (default rectangular shape)
        colors = CardDefaults.cardColors(
            containerColor = card_bg // Set background color
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main content: Image and temperature details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = weatherCodeToSymbMap[weatherCode] ?: R.drawable.clear_day),
                    contentDescription = "Weather image",
                    modifier = Modifier.size(70.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 10.dp) // Space between Text and Column
                ) {
                    Text(
                        text = "${temp.toFloat().toInt()}°F",
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                    Text(
                        text = weatherCodeToLabelMap[weatherCode] ?: "Clear",
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            }

            // Bottom Row: City/State text and Info Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Ensures space between Text and Icon
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$city, $state",
                    modifier = Modifier.weight(1f), // Makes Text occupy available space but not the entire width
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center // Centers the text within its allocated space
                    )
                )
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Info Icon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun Card2(humidity: String, windSpeed: String, visibility: String, pressure: String) {
    Box(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .background(card_bg),
        contentAlignment = Alignment.Center,
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.humidity),
                    contentDescription = "Humidity",
                    modifier = Modifier.size(60.dp)
                )
                Text(text =  "${humidity}%",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(text =  "Humidity")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between text
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wind_speed),
                    contentDescription = "Wind Speed",
                    modifier = Modifier.size(60.dp)
                )
                Text(text =  "${windSpeed}mph",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ))
                Text(text =  "Wind Speed")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between text
            ) {
                Image(
                    painter = painterResource(id = R.drawable.visibility),
                    contentDescription = "Visibility",
                    modifier = Modifier.size(60.dp)
                )
                Text(text =  "${visibility}mi",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ))
                Text(text =  "Visibility")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pressure),
                    contentDescription = "Pressure",
                    modifier = Modifier.size(60.dp)
                )
                Text(text =  "${pressure}inHg",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ))
                Text(text =  "Pressure")
            }
        }
    }
}

@Composable
fun Card3(weatherDataList: List<Interval>) {
    LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
        items(weatherDataList) { day ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp)
                .padding(bottom = 1.dp)
                .background(card_bg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTimeToYYYYMMDD(day.startTime),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 5.dp)
                        .padding(vertical = 5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Image(
                    painter = painterResource(
                        id = weatherCodeToSymbMap[day.values.weatherCode] ?: R.drawable.clear_day
                    ),
                    contentDescription = "Weather Status",
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f)
                        .padding(vertical = 5.dp)
                )
                Text(
                    text = day.values.temperatureMin.toInt().toString(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = day.values.temperatureMax.toInt().toString(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 5.dp)
                        .padding(end = 5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun InnerContainer(weatherData: ApiResponse, city: String, state: String, activity: ComponentActivity, page: Int, thisContext: Context, FAV_PLACES: Preferences.Key<String>, sharedViewModel: SharedViewModel, owner: ViewModelStoreOwner) {
    Column(modifier = Modifier.background(color = card_wrapper_bg)) {
//        Delete
//        Row {
//            Button(
//                onClick = {
//                    clearStringArray(thisContext, FAV_PLACES)
//                    sharedViewModel.updateState(!sharedViewModel.sharedToggleState)
//                }
//            ) {
//                Text("Clear state")
//            }
//        }
//        Delete
        Row {
            val dayVal = weatherData.data.timelines[0].intervals[0].values
            Card1(dayVal.temperature.toString(), dayVal.weatherCode, city, state) {
                val intent = Intent(activity, DetailsInfo::class.java).apply {
                    putExtra("data", weatherData.data as Serializable )
                    putExtra("city", city)
                    putExtra("state", state)
                }
                startActivity(activity, intent, null)
            }
        }
        Row {
            val dayVal = weatherData.data.timelines[0].intervals[0].values
            Card2(
                windSpeed = formatToTwoDecimalPlaces(dayVal.windSpeed),
                humidity = formatToTwoDecimalPlaces(dayVal.humidity),
                pressure = formatToTwoDecimalPlaces(dayVal.pressureSeaLevel),
                visibility = formatToTwoDecimalPlaces(dayVal.visibility)
            )
        }
        Row {
            Card3(weatherData.data.timelines[0].intervals)
        }
    }

    FloatingButton(thisContext, FAV_PLACES, city, state, page, sharedViewModel = sharedViewModel, owner) { }
}

fun fetchPredictions(
    query: String,
    placesClient: PlacesClient,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    if (query.isEmpty()) {
        onResult(emptyList())
        return
    }

    val token = AutocompleteSessionToken.newInstance()
    val request = FindAutocompletePredictionsRequest.builder()
        .setSessionToken(token)
        .setQuery(query)
        .setTypesFilter(listOf("locality"))
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            println("Predictions fetched: ${response.autocompletePredictions.size}")
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            println("Error fetching predictions: ${exception.message}")
            onResult(emptyList())
        }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerWithDots(thisContext: Context, innerPadding: PaddingValues, city: String, state: String, activity: ComponentActivity, pages:  MutableList<String>, FAV_PLACES: Preferences.Key<String>, sharedViewModel: SharedViewModel, owner: ViewModelStoreOwner) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dots Indicator Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .weight(1f) // Take up equal space for centering
                .background(card_bg_dark),
            verticalAlignment = Alignment.CenterVertically, // Center vertically within the row
            horizontalArrangement = Arrangement.Center // Center horizontally within the row
        ) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .background(card_bg_dark)
                    .padding(vertical = 20.dp),
                activeColor = white,
                inactiveColor = Color.DarkGray
            )
        }

        // Horizontal Pager
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f) // Take up more vertical space for pager
        ) { idx ->
            val weatherData = parseJsonResponse(pages[idx])
            if (weatherData != null) {
                val parts = weatherData.formatted_address.split(',')
                InnerContainer(weatherData, parts[0], parts[1], activity, idx, thisContext, FAV_PLACES, sharedViewModel = sharedViewModel, owner)
            }
        }
    }
}

@Composable
fun IndeterminateCircularIndicator(displayText: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.drawable.spinner, // Load the spinner drawable
            contentDescription = "Loading",
            modifier = Modifier
                .size(55.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
        Text(text = displayText, fontSize = 15.sp, color = white, modifier= Modifier.padding(top = 90.dp))
    }
}

suspend fun fetchDataFromApi(url: String): String? {
    return withContext(Dispatchers.IO) { // Perform on the IO thread
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.string() // Return the response
            } else {
                throw IOException("HTTP error code: ${response.code}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null // Return null if an error occurs
        }
    }
}

fun parseJsonResponse(jsonString: String): ApiResponse? {
    return try {
        val gson = Gson()
        gson.fromJson(jsonString, ApiResponse::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun formatDateTimeToYYYYMMDD(dateTime: String): String {
    // Input format
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    // Desired output format
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateTime) // Parse the input string
        outputFormat.format(date)             // Format the date to "YYYY-MM-DD"
    } catch (e: Exception) {
        e.printStackTrace()
        "" // Return an empty string if there's an error
    }
}

fun formatToTwoDecimalPlaces(number: Double): String {
    return String.format("%.2f", number)
}

@Composable
fun WeatherContent(modifier: Modifier = Modifier, selectedCityName: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weather information for:",
//            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = selectedCityName.ifEmpty { "Select a city to view weather" },
//            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            color = white
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(onSearchClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "WeatherApp",
                fontSize = 20.sp,
                color = white
            )
        },
        actions = {
            Image(
                painter = painterResource(id = R.drawable.map_search),
                contentDescription = "Search",
                modifier = Modifier
                    .size(30.dp)
                    .padding(vertical = 5.dp)
                    .clickable { onSearchClicked() }
            )
        }
    )
}

@Composable
fun SearchBarWithDropdown(
    query: String,
    onQueryChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onBackClicked: () -> Unit,
    isDropdownExpanded: Boolean,
    predictions: List<AutocompletePrediction>,
    onPredictionClicked: (AutocompletePrediction) -> Unit
) {
    var internalQuery by remember { mutableStateOf(query) }
    var debounceQuery by remember { mutableStateOf(query) }

    // Debounce effect
    LaunchedEffect(internalQuery) {
        kotlinx.coroutines.delay(1500L) // Debounce delay
        if (debounceQuery != internalQuery) {
            debounceQuery = internalQuery
            onQueryChanged(debounceQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
            .padding(bottom = 20.dp)
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            BasicTextField(
                value = internalQuery,
                onValueChange = { newValue ->
                    internalQuery = newValue
                },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Black)
                    .padding(horizontal = 8.dp),
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    onQueryChanged(internalQuery)
                }),
                cursorBrush = SolidColor(Color.White)
            )
            IconButton(onClick = onCloseClicked) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Search", tint = Color.White)
            }
        }

        // Dropdown for Predictions
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { },
            modifier = Modifier
                .fillMaxWidth()
                .background(card_bg_dark)
        ) {
            predictions.forEach { prediction ->
                DropdownMenuItem(
                    onClick = { onPredictionClicked(prediction) },
                    interactionSource = remember { MutableInteractionSource() },
                    text = {
                        Text(
                            text = prediction.getFullText(null).toString(),
                            style = TextStyle(color = Color.White, fontSize = 16.sp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun FloatingButton(
    thisContext: Context,
    FAV_PLACES: Preferences.Key<String>,
    city: String,
    state: String,
    page: Int,
    sharedViewModel: SharedViewModel,
    owner: ViewModelStoreOwner,
    onFabClick: () -> Unit
) {
    var isFav by remember { mutableStateOf(true) }
    val stringArrayFlow = getStringArray(thisContext, FAV_PLACES)
    val placesArray by stringArrayFlow.collectAsState(initial = emptyList())
    val tempPlaceArr = placesArray.toMutableList()

    Box(
        modifier = Modifier.fillMaxSize(), // Fill the entire screen
    ) {
        if(page > 0) FloatingActionButton(
            onClick = {
                if (!isFav) {
                    println("Adding to favorite!")
                    tempPlaceArr.add("${city.trim()}, ${state.trim()}")
                    saveStringArray(thisContext, tempPlaceArr, FAV_PLACES)
                    sharedViewModel.updateState(!sharedViewModel.sharedToggleState)
                    Toast.makeText(thisContext, "${city.trim()}, ${state.trim()} was added to favorites", Toast.LENGTH_LONG).show()
                } else {
                    println("Removing from favorite! - ${city.trim()}, ${state.trim()}")
                    println("tempA -> $tempPlaceArr")
                    tempPlaceArr.remove("${city.trim()}, ${state.trim()}")
                    println("tempA -> $tempPlaceArr")
                    saveStringArray(thisContext, tempPlaceArr, FAV_PLACES)
                    sharedViewModel.updateState(!sharedViewModel.sharedToggleState)
                    Toast.makeText(thisContext, "${city.trim()}, ${state.trim()} was removed from favorites", Toast.LENGTH_LONG).show()
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

fun getStringArray(context: Context, key:  Preferences.Key<String>): Flow<List<String>> {
    return context.dataStore.data.map { preferences ->
        val storedString = preferences[key] ?: "" // Get the stored string or empty
        if (storedString.isNotEmpty()) storedString.split(";") else emptyList() // Convert back to a list
    }
}

fun saveStringArray(context: Context, stringArray: List<String>, key: Preferences.Key<String>) {
    val arrayAsString = stringArray.joinToString(separator = ";") // Convert array to single string
    println("saving -> $stringArray")
    // Launch a coroutine for the suspendable work
    CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            preferences[key] = arrayAsString
        }
    }
}

fun clearStringArray(context: Context, key: Preferences.Key<String>) {
    // Launch a coroutine for the suspendable work
    CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            preferences[key] = "" // Clear the value by setting it to an empty string
        }
    }
}