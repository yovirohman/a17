package com.example.a17

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

class SensorFragment : Fragment(), SensorEventListener, LocationListener, OnMapReadyCallback {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gravitySensor: Sensor
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var accelerometerTextView: TextView
    private lateinit var gravityTextView: TextView
    private lateinit var gpsTextView: TextView

    private lateinit var detailLocationButton: Button
    private lateinit var searchLocationButton: Button
    private lateinit var searchLocationEditText: EditText
    private lateinit var myLocationButton: Button // Tombol "Lokasi Saya"
    private lateinit var googleMap: GoogleMap
    private lateinit var alertSwitch: Switch
    private lateinit var locationDetailsTextView: TextView
    private lateinit var magnetometer: Sensor // Sensor magnetik
    private lateinit var magneticFieldTextView: TextView
    private var ringtone: Ringtone? = null // Tambahkan variabel ringtone
    private var mediaPlayer: MediaPlayer? = null
    private var lastLocation: Location? = null
    private var isNearMetal = false // Tambahkan variabel isNearMetal


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sensor, container, false)

        // Inisialisasi TextView, Button, dan EditText
        accelerometerTextView = view.findViewById(R.id.accelerometerTextView)
        gravityTextView = view.findViewById(R.id.gravityTextView)
        gpsTextView = view.findViewById(R.id.gpsTextView)
        magneticFieldTextView = view.findViewById(R.id.magneticFieldTextView) // Inisialisasi TextView magnetik
        alertSwitch = view.findViewById(R.id.alertSwitch)
        detailLocationButton = view.findViewById(R.id.detailLocationButton)
        searchLocationButton = view.findViewById(R.id.searchLocationButton)
        searchLocationEditText = view.findViewById(R.id.searchLocationEditText)
        myLocationButton = view.findViewById(R.id.myLocationButton) // Inisialisasi tombol "Lokasi Saya"
        locationDetailsTextView = view.findViewById(R.id.locationDetailsTextView)

        // Inisialisasi sensor manager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!!
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!! // Pastikan magnetometer diinisialisasi dengan benar


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Inisialisasi Google Maps
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inisialisasi media player
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.nada_emergency)

        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(requireContext(), notification)

        // Set listener untuk tombol detail lokasi
        detailLocationButton.setOnClickListener {
            lastLocation?.let {
                getLocationDetails(it)
            }
        }

        // Set listener untuk tombol cari lokasi
        searchLocationButton.setOnClickListener {
            val locationName = searchLocationEditText.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            }
        }

        // Set listener untuk tombol "Lokasi Saya"
        myLocationButton.setOnClickListener {
            lastLocation?.let {
                moveToMyLocation(it)
            }
        }

        return view
    }

    private fun moveToMyLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)) // Zoom ke lokasi terakhir
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocationName(locationName, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val latLng = LatLng(address.latitude, address.longitude)
            googleMap.addMarker(MarkerOptions().position(latLng).title(locationName))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            locationDetailsTextView.text = "Lokasi ditemukan: ${address.getAddressLine(0)}"
        } else {
            locationDetailsTextView.text = "Lokasi tidak ditemukan"
        }
    }

    private fun getLocationDetails(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val fullAddress = StringBuilder()
            fullAddress.append("Nama Jalan: ${address.getAddressLine(0) ?: "Tidak Diketahui"}\n")

            locationDetailsTextView.text = fullAddress.toString()
        } else {
            locationDetailsTextView.text = "Tidak ada detail lokasi"
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                lastLocation = it
                val latLng = LatLng(it.latitude, it.longitude)
                googleMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Saat Ini"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                gpsTextView.text = "GPS: Latitude: ${it.latitude}, Longitude: ${it.longitude}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL) // Daftarkan listener sensor magnetik

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        mediaPlayer?.release()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    accelerometerTextView.text = "Akselerometer: x: $x, y: $y, z: $z"
                }
                Sensor.TYPE_GRAVITY -> {
                    val xGravity = it.values[0]
                    val yGravity = it.values[1]
                    val zGravity = it.values[2]
                    gravityTextView.text = "Gravitasi: x: $xGravity, y: $yGravity, z: $zGravity"

                    val stableThreshold = 9.8

                    if (yGravity > 7 && alertSwitch.isChecked) {
                        if (!mediaPlayer?.isPlaying!!) {
                            mediaPlayer?.start()
                        } else {

                        }
                    } else if (yGravity in (stableThreshold - 0.5)..(stableThreshold + 0.5) && mediaPlayer?.isPlaying!!) {
                        mediaPlayer?.pause()
                    } else {

                    }
                }

                Sensor.TYPE_MAGNETIC_FIELD -> {
                    // Sensor magnetik: Menghitung kekuatan medan magnet
                    val magneticFieldStrength = sqrt(
                        it.values[0].toDouble().pow(2.0) +
                                it.values[1].toDouble().pow(2.0) +
                                it.values[2].toDouble().pow(2.0)
                    )

                    magneticFieldTextView.text = "Medan Magnet: $magneticFieldStrength µT"


                    if (magneticFieldStrength > 100 && !isNearMetal) {
                        // Jika mendeteksi logam dan belum berada dekat logam sebelumnya
                        isNearMetal = true
                        ringtone?.play()
                    } else if (magneticFieldStrength < 50 && isNearMetal) {
                        // Jika menjauh dari logam
                        isNearMetal = false
                        ringtone?.stop()
                    } else {

                    }
                }

                else -> {}
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Terkini"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        lastLocation?.let {
            val distance = location.distanceTo(it)
            if (distance > 1 && alertSwitch.isChecked) {
                mediaPlayer?.start()
            }
        }

        lastLocation = location
        gpsTextView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
    }
}
