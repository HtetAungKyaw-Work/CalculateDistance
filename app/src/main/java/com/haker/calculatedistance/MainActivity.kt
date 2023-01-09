package com.haker.calculatedistance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var btnLoc1: Button?= null
    var btnLoc2: Button?= null
    var btnCalculate: Button?= null
    var txtLoc1: TextView?= null
    var txtLoc2: TextView?= null
    var txtResult: TextView?= null

    private var pickupLat: Double = 0.0
    private var pickupLng: Double = 0.0
    private var pickupLat2: Double = 0.0
    private var pickupLng2: Double = 0.0

    private var key_from_btn1 = "from_btn1"
    private var key_from_btn2 = "from_btn2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLoc1 = findViewById(R.id.btnLoc1)
        btnLoc2 = findViewById(R.id.btnLoc2)
        btnCalculate = findViewById(R.id.btnCalculate)
        txtLoc1 = findViewById(R.id.txtLoc1)
        txtLoc2 = findViewById(R.id.txtLoc2)
        txtResult = findViewById(R.id.txtResult)

        btnLoc1!!.setOnClickListener {
            var intent = Intent(this, GetLocationActivity::class.java)
            intent.putExtra(key_from_btn1, "yes")
            intent.putExtra("pickupLat2", pickupLat2)
            intent.putExtra("pickupLng2", pickupLng2)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        if (intent.getDoubleExtra("pickupLat", 0.0) != null) {
            pickupLat = intent.getDoubleExtra("pickupLat", 0.0)
            pickupLng = intent.getDoubleExtra("pickupLng", 0.0)
            pickupLat2 = intent.getDoubleExtra("pickupLat2", 0.0)
            pickupLng2 = intent.getDoubleExtra("pickupLng2", 0.0)

            txtLoc1!!.text = "Latitude : $pickupLat & Longitude : $pickupLng"
            txtLoc2!!.text = "Latitude : $pickupLat2 & Longitude : $pickupLng2"
        }

        btnLoc2!!.setOnClickListener {
            var intent = Intent(this, GetLocationActivity::class.java)
            intent.putExtra(key_from_btn2, "yes")
            intent.putExtra("pickupLat", pickupLat)
            intent.putExtra("pickupLng", pickupLng)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        if (intent.getDoubleExtra("pickupLat2", 0.0) != null) {

            pickupLat = intent.getDoubleExtra("pickupLat", 0.0)
            pickupLng = intent.getDoubleExtra("pickupLng", 0.0)
            pickupLat2 = intent.getDoubleExtra("pickupLat2", 0.0)
            pickupLng2 = intent.getDoubleExtra("pickupLng2", 0.0)

            txtLoc1!!.text = "Latitude : $pickupLat & Longitude : $pickupLng"
            txtLoc2!!.text = "Latitude : $pickupLat2 & Longitude : $pickupLng2"
        }

        btnCalculate!!.setOnClickListener {
            getDistanceFromTwoLatLng(pickupLat, pickupLng, pickupLat2, pickupLng2)
        }
    }

    fun getDistanceFromTwoLatLng(lat1: Double, lon1: Double, lat2: Double, lon2: Double) {
        var R = 6371 // Radius of the earth in km
        var dLat = deg2rad(lat2-lat1)  // deg2rad below
        var dLon = deg2rad(lon2-lon1)
        var a =
            Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                    Math.sin(dLon/2) * Math.sin(dLon/2)

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
        var d = R * c // Distance in km
        txtResult!!.text = "Distance between these two locations : $d km"
    }

    fun deg2rad(deg: Double): Double {
        return deg * (Math.PI/180)
    }
}