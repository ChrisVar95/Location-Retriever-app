package com.example.retrievelocationapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.retrievelocationapp.ui.theme.RetrieveLocationAppTheme
import com.example.retrievelocationapp.viewmodel.LocationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationViewModel: LocationViewModel = LocationViewModel(this)
        setContent {
            RetrieveLocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Location(viewModel = locationViewModel)
                }
            }
        }
        getPermissionsForLocation(locationViewModel)
    }
    private fun getPermissionsForLocation(viewModel: LocationViewModel) {
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            )
            { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        //Precise location access granted
                        viewModel.getLocationLiveData().getLocationData()
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        //Only approximate location access granted.
                        viewModel.getLocationLiveData().getLocationData()
                    } else -> {
                        Toast.makeText(this, "Location permissions not granted, coordinates are not retrieved",
                            Toast.LENGTH_SHORT).show()
                    }
                }

            }
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }
}

@Composable
fun Location(viewModel: LocationViewModel) {
    val location by viewModel.getLocationLiveData().observeAsState()
    
    if (location !== null) {
        // Here an API call or other functionality related to
        // retrieved location could be executed, since we have 
        // observed a new location
    }
    Column() {
        Text(text = location?.latitude.toString())
        Text(text = location?.longitude.toString())
    }
}
