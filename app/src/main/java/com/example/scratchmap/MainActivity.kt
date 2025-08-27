package com.example.scratchmap

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scratchmap.ui.glassmorphism
import com.example.scratchmap.ui.theme.ScratchMapTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScratchMapTheme {
                ScratchMapApp()
            }
        }
    }
}

@Composable
fun ScratchMapApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(application))

    val mapView = remember { MapView(context) }

    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasPermissions) {
            MapViewComposable(
                mapView = mapView,
                onLocationChanged = { location ->
                    mainViewModel.updateLocation(location.latitude, location.longitude)
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Location permission is required to use this app.")
            }
        }

        BottomButtons(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            onMyLocationClick = {
                val myLocationOverlay = mapView.overlays.firstOrNull { it is MyLocationNewOverlay } as? MyLocationNewOverlay
                myLocationOverlay?.myLocation?.let {
                    mapView.controller.animateTo(it)
                }
            },
            onProgressClick = {
                Toast.makeText(context, "Progress screen coming soon!", Toast.LENGTH_SHORT).show()
            },
            onSettingsClick = {
                Toast.makeText(context, "Settings screen coming soon!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun BottomButtons(
    modifier: Modifier = Modifier,
    onMyLocationClick: () -> Unit,
    onProgressClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // My Location Button
        IconButton(
            onClick = onMyLocationClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .glassmorphism(shape = CircleShape)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "My Location")
        }

        // Progress Button
        IconButton(
            onClick = onProgressClick,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .glassmorphism(shape = CircleShape)
        ) {
            Icon(Icons.Filled.DonutLarge, contentDescription = "Progress", modifier = Modifier.size(48.dp))
        }

        // Settings Button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .glassmorphism(shape = CircleShape)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}


@Composable
fun MapViewComposable(
    mapView: MapView,
    onLocationChanged: (Location) -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    AndroidView({ mapView }) { map ->
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(3.0)
        map.controller.setCenter(GeoPoint(51.5074, -0.1278)) // Center on London

        // Add location overlay
        val locationOverlay = MyLocationOverlayWithCallback(onLocationChanged, GpsMyLocationProvider(context), map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        map.overlays.add(locationOverlay)
    }
}

class MyLocationOverlayWithCallback(
    private val onLocationChanged: (Location) -> Unit,
    gpsMyLocationProvider: GpsMyLocationProvider,
    mapView: MapView
) : MyLocationNewOverlay(gpsMyLocationProvider, mapView) {
    override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
        super.onLocationChanged(location, source)
        location?.let { onLocationChanged(it) }
    }
}
