package com.example.locationapp

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat

@Composable
fun LocationDisplay(
    viewModel: LocationViewModel,
    locationUtils: LocationUtils,
    context: Context
) {
    val location = viewModel.location.value

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            // Notice that we are using RequestMultiplePermissions() here,
            // if using RequestPermission(), context object in onResult will be of type Boolean
            onResult = { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                ) {
                    // I have access to location
                    locationUtils.requestLocationUpdates(viewModel)
                    Toast.makeText(
                        context,
                        "Location granted",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    val fineLocationGranted = ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val coarseLocationGranted = ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    println("Fine location granted: $fineLocationGranted")
                    println("Coarse location granted: $coarseLocationGranted")

                    val rationaleRequired = fineLocationGranted && coarseLocationGranted

                    // User had denied the permission before, so we need to show rationale
                    if (rationaleRequired) {
                        // Show rationale
                        Toast.makeText(
                            context,
                            "Location permission is required for this feature",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Permission denied, but never ask again is not checked
                        Toast.makeText(
                            context,
                            "Please enable location in settings",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            location?.let { locationData ->
                Text(text = "Location: ${locationData.latitude}, ${locationData.longitude}")
            } ?: Text(text = "No location available")

            Button(
                onClick = {
                    // Check if location is enabled
                    if (!locationUtils.isLocationEnabled()) {
                        Toast.makeText(
                            context,
                            "Please enable location services",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    if (locationUtils.hasLocationPermission()) {
                        // Location permission already granted, let's update the current location
                        locationUtils.requestLocationUpdates(viewModel)
                    } else {
                        // Request location permission
                        requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            ) {
                Text(text = "Get location")
            }
        }
    }
}