package com.example.weatherforecasting

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.weatherforecasting.databinding.ActivityMainBinding
import com.shashank.sony.fancytoastlib.FancyToast
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private val  binding : ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val searchTextView = searchView.findViewById<TextView>(
            searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        )
        searchTextView.setTextColor(Color.BLACK)
        fetchWeatherdata("DELHI")
        searchCity()


    }

    private fun  searchCity(){
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchWeatherdata(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return  true
            }

        })
    }



    private fun fetchWeatherdata(Cityname:String) {

        if(Cityname.isEmpty() || Cityname==null){
            Toast.makeText(this@MainActivity, "Please Enter city name", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(Cityname,"01ce2e68dd27ca739d0eb285b281b0e3","metric")
        response.enqueue(object:Callback<weatherapp>{
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responsebody = response.body()
                if(response.isSuccessful && responsebody!=null){
                    val temp = responsebody.main.temp.toString()
                    val humidity = responsebody.main.humidity.toString()
                    val windspeed = responsebody.wind.speed
                    val sunrise = responsebody.sys.sunrise.toLong()
                    val sunset = responsebody.sys.sunset.toLong()
                    val sealevel = responsebody.main.pressure
                    val condition = responsebody.weather.firstOrNull()?.main?:"unknown"
                    val mintemp = responsebody.main.temp_min
                    val maxtemp = responsebody.main.temp_max

                    Log.d("TAG","onresponse $temp")
                    binding.humidity.text=humidity
                    binding.windspeed.text=windspeed.toString()
                    binding.min.text="MIN:$mintemp°C"
                    binding.max.text="MAX:$maxtemp°C"
                    binding.weathertype.text=condition
                    binding.sealevel.text=sealevel.toString()
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.rain.text=condition
                    binding.dayview.text=dayname(System.currentTimeMillis())
                    binding.dateview.text=date(System.currentTimeMillis())
                    binding.cityname.text="$Cityname"
                    binding.temp.text="$temp°C"

                    changeWeatherpic(condition)





                }else{
                    FancyToast.makeText(this@MainActivity, "City Not Found", FancyToast.LENGTH_SHORT,
                        FancyToast.INFO,false).show()
                }

            }



            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                FancyToast.makeText(this@MainActivity, "failed to fetch data.Please try again later", FancyToast.LENGTH_SHORT,FancyToast.INFO,false ).show()

            }

        })

    }
    private fun changeWeatherpic(condition: String) {
        binding.animationView2.cancelAnimation()



        when(condition) {
            "Mist", "Foggy","Haze"->{
                Log.d("TAG", "Setting haze animation")
                binding.animationView2.setAnimation(R.raw.haze)

            }
            "Partly Cloudy", "Clouds", "Overcast"-> {
                Log.d("TAG", "Setting cloudy animation")
                binding.animationView2.setAnimation(R.raw.cloudy)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard"  -> {
                Log.d("TAG", "Setting rainy animation")
                binding.animationView2.setAnimation(R.raw.rainy)
            }
            "Clear Sky", "Sunny", "Clear","Hot", "Heatwave" -> {
                Log.d("TAG", "Setting sunny animation")
                binding.animationView2.setAnimation(R.raw.sunny)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                Log.d("TAG", "Setting snowy animation")
                binding.animationView2.setAnimation(R.raw.snowy)
            }
            "Stormy", "Thunderstorm", "Lightning", "Hailstorm" -> {
                Log.d("TAG", "Setting storm animation")
                binding.animationView2.setAnimation(R.raw.storm)
            }
            "Windy", "Breezy", "Gale"  -> {
                Log.d("TAG", "Setting windy animation")
                binding.animationView2.setAnimation(R.raw.windy)
            } else -> {
            Log.d("TAG", "Setting default sunny animation")
            binding.animationView2.setAnimation(R.raw.sunny)
        }
        }
        binding.animationView2.playAnimation()
    }

    fun dayname(timestamp: Long):String
    {
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun date(timestamp: Long):String
    {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long):String
    {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }


}


