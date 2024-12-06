package com.example.csci_571_a4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.*

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.jakewharton.threetenabp.AndroidThreeTen

data class CardData(
    val imageResId: Int,
    val valueText: String,
    val labelText: String
)

data class TabItem(
    val title: String,
    val icon: Int,
    val desc: String,
    val content: @Composable () -> Unit,
)

class DetailsInfo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getSerializableExtra("data") as? Data
        val city = intent.getStringExtra("city") ?: ""
        val state = intent.getStringExtra("state") ?: ""
        AndroidThreeTen.init(this)
        println("tiLi: ${data}")
        enableEdgeToEdge()
        setContent {
            CSCI_571_A4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (data != null) {
                        MainLayoutDetailsInfo2(
                            innerPadding,
                            this,
                            city,
                            state,
                            data,
                            context = this
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainLayoutDetailsInfo2(
    innerPadding: PaddingValues,
    activity: ComponentActivity,
    city: String,
    state: String,
    data: Data,
    context: Context
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabItems = listOf(
        TabItem("TODAY", R.drawable.today, desc = "Today Tab", content = { TodayTabContent(data = data) } ),
        TabItem("WEEKLY", R.drawable.weekly_tab, "Weekly tab", content = { WeeklyTabContent(context, data) }),
        TabItem("WEATHER DATA", R.drawable.weather_data_tab, "Weather Data tab", content = { WeatherDataTabContent(context, data) })
    )
    val pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) selectedTabIndex = pagerState.currentPage
    }
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(tabBgColor)

    ) {
        HeaderDetailsInfo(activity, city, state, context, data)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = tabBgColor,
            contentColor = white,
            indicator = {
                    tabPositions ->
                    if (selectedTabIndex < tabPositions. size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .background(white),
                            color = white
                        )
                    }
            },
            divider = {  },
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    modifier = Modifier
                        .padding(top = 20.dp),
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(text = item.title, color = white)
                    },
                    icon = {
                        Image(painter = painterResource(id = item.icon), contentDescription = item.desc)
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            index -> tabItems[index].content()
        }
    }
}

@Composable
fun HeaderDetailsInfo(activity: ComponentActivity, city: String, state: String, context: Context, data: Data) {
    val tweetText = "Check Out ${city}, ${state}’s Weather! It is ${formatToTwoDecimalPlaces(data.timelines[0].intervals[0].values.temperature)}°F! \n" + "#CSCI571WeatherSearch"
    val twitterIntentUrl = "https://twitter.com/intent/tweet?text=${Uri.encode(tweetText)}"

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .padding(vertical = 20.dp)
    ) {
        Column {
            IconButton(onClick = {
                println("finishing")
                activity.finish()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White, // White icon
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f, fill = true) // Pushes content to occupy available space
        ) {
            Text(
                text = "$city, $state", style = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(end = 8.dp)
        ) {
            // Twitter Icon
            Image(
                painter = painterResource(id = R.drawable.twitter), // Replace with your Twitter icon drawable
                contentDescription = "Share on Twitter",
                modifier = Modifier
                    .width(50.dp)
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterIntentUrl))
                        context.startActivity(intent)
                    }
            )
        }
    }

}

@Composable
fun TodayTabContent(data: Data) {
    val row1 = listOf(
        CardData(R.drawable.wind_speed, "${data.timelines[0].intervals[0].values.windSpeed}mph", "Wind Speed"),
        CardData(R.drawable.pressure, "${data.timelines[0].intervals[0].values.pressureSeaLevel}inHg", "Pressure"),
        CardData(R.drawable.pouring, "${data.timelines[0].intervals[0].values.precipitationProbability}%", "Precipitation")
    )
    val row2 = listOf(
        CardData(R.drawable.ic_thermometer, "${data.timelines[0].intervals[0].values.temperature}°F", "Temperature"),
        CardData(weatherCodeToSymbMap[data.timelines[0].intervals[0].values.weatherCode] ?: R.drawable.clear_day, "${weatherCodeToLabelMap[data.timelines[0].intervals[0].values.weatherCode]}", " "),
        CardData(R.drawable.humidity, "${data.timelines[0].intervals[0].values.humidity}%", "Humidity")
    )
    val row3 = listOf(
        CardData(R.drawable.visibility, "${data.timelines[0].intervals[0].values.visibility}mi", "Visibility"),
        CardData(R.drawable.cloud_cover, "${data.timelines[0].intervals[0].values.cloudCover}%", "Could Cover"),
        CardData(R.drawable.uv, "${data.timelines[0].intervals[0].values.uvIndex}", "Ozone")
    )
    Column(
        Modifier
            .background(detailInfoBgColor)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        ) {
            row1.forEach { cardData ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = tabCardBgColor // Set background color
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = cardData.imageResId),
                            contentDescription = cardData.labelText,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(top = 15.dp, bottom = 15.dp)
                        )
                        Text(
                            text = cardData.valueText,
                            color = white
                        )
                        Text(
                            text = cardData.labelText,
                            color = white
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        ) {
            row2.forEach { cardData ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = tabCardBgColor // Set background color
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = cardData.imageResId),
                            contentDescription = cardData.labelText,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(top = 15.dp, bottom = 15.dp)
                        )
                        Text(
                            text = cardData.valueText,
                            color = white
                        )
                        Text(
                            text = cardData.labelText,
                            color = white
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        ) {
            row3.forEach { cardData ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = tabCardBgColor // Set background color
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = cardData.imageResId),
                            contentDescription = cardData.labelText,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(top = 15.dp, bottom = 15.dp)
                        )
                        Text(
                            text = cardData.valueText,
                            color = white
                        )
                        Text(
                            text = cardData.labelText,
                            color = white
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyTabContent(context: Context, data: Data) {
    // State to hold the WebView content
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    var isWebViewReady by remember { mutableStateOf(false) }

    // Preload the WebView
    LaunchedEffect(Unit) {
        isWebViewReady = false

        webViewState.value = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isWebViewReady = true // Mark as ready once the content is loaded
                }
            }

            // Load the Highcharts HTML content
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <script src="https://code.highcharts.com/highcharts.js"></script>
                    <script src="https://code.highcharts.com/modules/exporting.js"></script>
                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
                        <script src="https://code.highcharts.com/highcharts.js"></script>
                        <script src="https://code.highcharts.com/modules/data.js"></script>
                    <script src="https://code.highcharts.com/highcharts-more.js"></script>

                </head>
                <body>
                    <div id="container" style="width:100%; height:100%;"></div>
                    <script>
                        document.addEventListener('DOMContentLoaded', function () {
                            Highcharts.chart('container', ${getAreaChartConfig(data)});
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()

            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }

    // Show the content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tabBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Temperature Range",
                color = white,
                fontSize = 20.sp
            )
        }

        if (isWebViewReady && webViewState.value != null) {
            AndroidView(factory = { webViewState.value!! })
        } else {
            // Show a spinner while the WebView is loading
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                IndeterminateCircularIndicator("Loading")
            }
        }
    }
}

@Composable
fun WeatherDataTabContent(context: Context, data: Data) {
    // State to hold the WebView content
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    var isWebViewReady by remember { mutableStateOf(false) }

    // Preload the WebView
    LaunchedEffect(Unit) {
        isWebViewReady = false

        webViewState.value = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isWebViewReady = true // Mark as ready once the content is loaded
                }
            }

            // Load the Highcharts HTML content
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <script src="https://code.highcharts.com/highcharts.js"></script>
                    <script src="https://code.highcharts.com/highcharts-more.js"></script>
                    <script src="https://code.highcharts.com/modules/solid-gauge.js"></script>
                    <script src="https://code.highcharts.com/modules/exporting.js"></script>
                    <script src="https://code.highcharts.com/modules/export-data.js"></script>
                    <script src="https://code.highcharts.com/modules/accessibility.js"></script>

                </head>
                <body>
                    <div id="container" style="width:100%; height:100%;"></div>
                    <script>
                    function renderIcons() {

                      // Move icon
                      if (!this.series[0].icon) {
                        this.series[0].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8])
                          .attr({
                            stroke: '#303030',
                            'stroke-linecap': 'round',
                            'stroke-linejoin': 'round',
                            'stroke-width': 2,
                            zIndex: 10
                          })
                          .add(this.series[2].group);
                      }
                      this.series[0].icon.translate(
                        this.chartWidth / 2 - 10,
                        this.plotHeight / 2 - this.series[0].points[0].shapeArgs.innerR -
                          (this.series[0].points[0].shapeArgs.r - this.series[0].points[0].shapeArgs.innerR) / 2
                      );

                      // Exercise icon
                      if (!this.series[1].icon) {
                        this.series[1].icon = this.renderer.path(
                          ['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,
                            'M', 8, -8, 'L', 16, 0, 8, 8]
                        )
                          .attr({
                            stroke: '#ffffff',
                            'stroke-linecap': 'round',
                            'stroke-linejoin': 'round',
                            'stroke-width': 2,
                            zIndex: 10
                          })
                          .add(this.series[2].group);
                      }
                      this.series[1].icon.translate(
                        this.chartWidth / 2 - 10,
                        this.plotHeight / 2 - this.series[1].points[0].shapeArgs.innerR -
                          (this.series[1].points[0].shapeArgs.r - this.series[1].points[0].shapeArgs.innerR) / 2
                      );

                      // Stand icon
                      if (!this.series[2].icon) {
                        this.series[2].icon = this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0])
                          .attr({
                            stroke: '#303030',
                            'stroke-linecap': 'round',
                            'stroke-linejoin': 'round',
                            'stroke-width': 2,
                            zIndex: 10
                          })
                          .add(this.series[2].group);
                      }

                      this.series[2].icon.translate(
                        this.chartWidth / 2 - 10,
                        this.plotHeight / 2 - this.series[2].points[0].shapeArgs.innerR -
                          (this.series[2].points[0].shapeArgs.r - this.series[2].points[0].shapeArgs.innerR) / 2
                      );
                    }
                        document.addEventListener('DOMContentLoaded', function () {
                            Highcharts.chart('container', ${getGaugeChartConfig(data)});
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()

            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }

    // Show the content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tabBgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weather Data",
                color = white,
                fontSize = 20.sp
            )
        }

        if (isWebViewReady && webViewState.value != null) {
            AndroidView(factory = { webViewState.value!! })
        } else {
            // Show a spinner while the WebView is loading
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                IndeterminateCircularIndicator("Loading")
            }
        }
    }
}

fun getAreaChartConfig(data: Data): String {

    return """
        {
    chart: {
      type: "arearange",
    },
    title: {
      text: "Temperature variation by day",
    },
    xAxis: {
      type: "datetime",
      accessibility: {},
    },
    yAxis: {
      title: {
        text: null,
      },
    },
    tooltip: {
      crosshairs: true,
      shared: true,
      valueSuffix: "°F",
      xDateFormat: "%A, %b %e",
    },
    legend: {
      enabled: false,
    },
    plotOptions: {
      series: {
        marker: {
          fillColor: "#27A5FC",
          lineWidth: 1,
          lineColor: "#27A5FC", // inherit from series
        },
        lineColor: "#F6A02A",
        lineWidth: 1,
      },
    },
    series: [
      {
        name: "Temperatures",
        data: ${formatTimelineData(data)},
        color: {
          linearGradient: {
            x1: 0,
            x2: 0,
            y1: 0,
            y2: 1,
          },
          stops: [
            [0, "#F6A02A"],
            [1, "#27A5FC"],
          ],
        },
      },
    ],
  }
    """.trimIndent()
}

fun getGaugeChartConfig(data: Data): String {
    return """
        {
          chart: {
            type: 'solidgauge',
            height: '100%',
            events: {
              render: renderIcons
            }
          },
        
          title: {
            text: 'Stat Summary',
            style: {
              fontSize: '18px'
            }
          },
        
          tooltip: {
            borderWidth: 0,
            backgroundColor: 'none',
            shadow: false,
            style: {
              fontSize: '12px'
            },
            valueSuffix: '%',
            pointFormat: '{series.name}<br><span style="font-size:1.5em; color: {point.color}; font-weight: bold">{point.y}</span>',
            positioner: function (labelWidth) {
              return {
                x: (this.chart.chartWidth - labelWidth) / 2,
                y: (this.chart.plotHeight / 2) + 15
              };
            }
          },
        
          pane: {
            startAngle: 0,
            endAngle: 360,
            background: [{ // Track for Move
              outerRadius: '112%',
              innerRadius: '88%',
              backgroundColor: Highcharts.color(Highcharts.getOptions().colors[2])
                .setOpacity(0.3)
                .get(),
              borderWidth: 0
            }, { // Track for Exercise
              outerRadius: '87%',
              innerRadius: '63%',
              backgroundColor: Highcharts.color(Highcharts.getOptions().colors[0])
                .setOpacity(0.3)
                .get(),
              borderWidth: 0
            }, { // Track for Stand
              outerRadius: '62%',
              innerRadius: '38%',
              backgroundColor: Highcharts.color(Highcharts.getOptions().colors[3])
                .setOpacity(0.3)
                .get(),
              borderWidth: 0
            }]
          },
        
          yAxis: {
            min: 0,
            max: 100,
            lineWidth: 0,
            tickPositions: []
          },
        
          plotOptions: {
            solidgauge: {
              dataLabels: {
                enabled: false
              },
              linecap: 'round',
              stickyTracking: false,
              rounded: true
            }
          },
        
          series: [{
            name: 'Cloud Cover',
            data: [{
              color: Highcharts.getOptions().colors[2],
              radius: '112%',
              innerRadius: '88%',
              y: ${data.timelines[0].intervals[0].values.cloudCover}
            }]
          }, {
            name: 'Precipitation',
            data: [{
              color: Highcharts.getOptions().colors[0],
              radius: '87%',
              innerRadius: '63%',
              y: ${data.timelines[0].intervals[0].values.precipitationProbability}
            }]
          }, {
            name: 'Humidity',
            data: [{
              color: Highcharts.getOptions().colors[3],
              radius: '62%',
              innerRadius: '38%',
              y: ${data.timelines[0].intervals[0].values.humidity}
            }]
          }]
        }
    """.trimIndent()
}

fun formatTimelineData(data: Data): String {
    val result = StringBuilder("[")

    data.timelines[0].intervals.forEach { interval ->
        val (day, month) = parseToDayAndMonth(interval.startTime)
        val lowTemperature = interval.values.temperatureMin
        val highTemperature = interval.values.temperatureMax

        result.append("[")
        result.append("\"$day, $month\", $highTemperature, $lowTemperature")
        result.append("],")
    }

    if (result.endsWith(",")) {
        result.deleteCharAt(result.length - 1)
    }
    result.append("]")

    return result.toString()
}

fun parseToDayAndMonth(startTime: String): Pair<Int, String> {
    val formatter = org.threeten.bp.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val zonedDateTime = org.threeten.bp.ZonedDateTime.parse(startTime, formatter)

    val day = zonedDateTime.dayOfMonth
    val month = zonedDateTime.month.name.take(3).capitalize() // Get the first 3 letters and capitalize

    return Pair(day, month)
}


