package com.haker.calculatedistance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GetLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var LOCATION_PERMISSION_REQUEST_CODE = 1

    val LOCATION_UPDATE_MIN_DISTANCE = 10
    val LOCATION_UPDATE_MIN_TIME = 500

    private var mLocationManager: LocationManager? = null

    public var pickupLat : Double = 0.0
    public var pickupLng : Double = 0.0
    private var gpsLat: Double = 0.0
    private var gpsLng: Double = 0.0
    private var cellLat: Double = 0.0
    private var cellLng: Double = 0.0

    private var key_from_btn1 = "from_btn1"
    private var key_from_btn2 = "from_btn2"

    var btnConfirm: Button?= null
    var imageView: ImageView?= null

    var currentButton = ""

    var selectedLat : Double = 0.0
    var selectedLng : Double = 0.0

    private val mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location != null) {
                //Logger.d(String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                //drawMarker(location)
                val gps = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 21f))
                mLocationManager?.removeUpdates(this)

                if (location.provider == LocationManager.GPS_PROVIDER) {//cell location
                    cellLat = location!!.latitude
                    cellLng = location!!.longitude
                }
                if (location.provider == LocationManager.NETWORK_PROVIDER) {//gps location
                    gpsLat = location!!.latitude
                    gpsLng = location!!.longitude
                }
                Log.i("cellLat", cellLat.toString())
                Log.i("cellLng", cellLng.toString())
                Log.i("gpsLat", gpsLat.toString())
                Log.i("gpsLng", gpsLng.toString())
            } else {
                //Logger.d("Location is null");
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view_to_get_lat_lng)

        if (intent.getStringExtra(key_from_btn2) != null) {
            if (intent.getStringExtra(key_from_btn2) == "yes") {
                currentButton = "Btn2"

                selectedLat = intent.getDoubleExtra("pickupLat", 0.0)
                selectedLng = intent.getDoubleExtra("pickupLng", 0.0)
            }
        }
        if (intent.getStringExtra(key_from_btn1) != null) {
            if (intent.getStringExtra(key_from_btn1) == "yes") {
                currentButton = "Btn1"

                selectedLat = intent.getDoubleExtra("pickupLat2", 0.0)
                selectedLng = intent.getDoubleExtra("pickupLng2", 0.0)
            }
        }

        Log.i("Current", currentButton)

        btnConfirm = findViewById(R.id.btnConfirm)
        imageView = findViewById(R.id.imageView)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnConfirm!!.setOnClickListener {
            pickupLat = mMap.cameraPosition.target.latitude
            pickupLng = mMap.cameraPosition.target.longitude

            onBackPressed()

        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //setUpMap()
        getCurrentLocation()
    }

    private fun setUpMap(gps: LatLng) {
        mMap.uiSettings.isZoomControlsEnabled = true

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

        var lm = getSystemService(LOCATION_SERVICE) as LocationManager

        //Get the best provider >>
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        criteria.isCostAllowed = false
        val provider = lm.getBestProvider(criteria, false)
        // <<

//        var provider = LocationManager.GPS_PROVIDER
        var location = lm.getLastKnownLocation(provider!!)
        if (location != null) {
            // add location to the location listener for location changes
            //var test = "Prov : " + location!!.provider + " Lat : " + location!!.latitude + " Lon : " + location!!.longitude



            //val latLng = LatLng(currentLatitude, currentLongitude)
            mMap.addMarker(MarkerOptions().position(gps).title("Pickup Location"))

            val zoomLevel = 10.0f //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gps, zoomLevel))
        }

        //getCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val isGPSEnabled = mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = mLocationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var location: Location? = null
        if (!(isGPSEnabled == true || isNetworkEnabled == true)) {
            //Snackbar.make(mMapView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
        } else {
            if (isNetworkEnabled == true) {
                mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME.toLong(), LOCATION_UPDATE_MIN_DISTANCE.toFloat(), mLocationListener)
                location = mLocationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            if (isGPSEnabled == true) {
                mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME.toLong(), LOCATION_UPDATE_MIN_DISTANCE.toFloat(), mLocationListener)
                location = mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        }
        if (location != null) {
            /*Logger.d(String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                    location.getLongitude()));*/
            //drawMarker(location)
            val gps = LatLng(location.latitude, location.longitude)
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 21f))
            setUpMap(gps)
        }
    }

    override fun onBackPressed() {
        var intent = Intent(this, MainActivity::class.java)
        if (currentButton == "Btn1") {
            intent.putExtra("pickupLat", pickupLat)
            intent.putExtra("pickupLng", pickupLng)
            intent.putExtra("pickupLat2", selectedLat)
            intent.putExtra("pickupLng2", selectedLng)
        }
        else {
            intent.putExtra("pickupLat2", pickupLat)
            intent.putExtra("pickupLng2", pickupLng)
            intent.putExtra("pickupLat", selectedLat)
            intent.putExtra("pickupLng", selectedLng)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}