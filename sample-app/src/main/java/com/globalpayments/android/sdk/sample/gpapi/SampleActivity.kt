@file:OptIn(ExperimentalMaterial3Api::class)

package com.globalpayments.android.sdk.sample.gpapi

import android.os.Bundle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.globalpayments.android.sdk.sample.BuildConfig
import com.globalpayments.android.sdk.sample.R
import com.globalpayments.android.sdk.sample.gpapi.navigation.NavigationManager
import com.globalpayments.android.sdk.sample.gpapi.navigation.SampleAppNavHost
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.ConfigDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.DirectionBack
import com.globalpayments.android.sdk.sample.gpapi.navigation.directions.HomeDirection
import com.globalpayments.android.sdk.sample.gpapi.navigation.navigate
import com.globalpayments.android.sdk.sample.utils.AppPreferences
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPAPIConfigurationUtils
import com.globalpayments.android.sdk.sample.utils.configuration.GPEcomConfiguration
import com.globalpayments.android.sdk.sample.utils.configuration.GPEcomConfigurationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SampleActivity : FragmentActivity() {

    private val sharedPreferences by lazy { AppPreferences(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SampleAppScreen()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            if (BuildConfig.USE_GPECOM)
                GPEcomConfigurationUtils.initializeDefaultGPEcomConfiguration(
                    sharedPreferences.gpEcomConfiguration ?: GPEcomConfiguration.fromBuildConfig()
                )
            else
                GPAPIConfigurationUtils.initializeDefaultGPAPIConfiguration(
                    sharedPreferences.gpAPIConfiguration ?: GPAPIConfiguration.fromBuildConfig()
                )
        }
    }
}

@Composable
fun SampleAppScreen() {

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(key1 = Unit) {
        NavigationManager.commands.collect { direction ->
            when (direction) {
                DirectionBack -> backPressedDispatcher?.onBackPressed()
                else -> navController.navigate(
                    route = direction::class.toString(),
                    args = direction.arguments,
                    navOptions = navOptions(direction.navBuilder)
                )
            }

        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        drawerGesturesEnabled = false,
        topBar = {
            val currentNavEntry by navController.currentBackStackEntryAsState()
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        modifier = Modifier.size(130.dp, 20.dp),
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = null
                    )
                },
                navigationIcon = {
                    if (currentNavEntry?.destination?.route != HomeDirection::class.toString()) {
                        IconButton(onClick = { backPressedDispatcher?.onBackPressed() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color(0xFF148EE6) //TODO extract color
                            )
                        }
                    }
                },
                actions = {
                    if (currentNavEntry?.destination?.route != ConfigDirection::class.toString()) {
                        IconButton(onClick = { coroutineScope.launch { NavigationManager.navigate(ConfigDirection) } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings),
                                contentDescription = null,
                                tint = Color(0xFF148EE6) //TODO extract color
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        content = {
            SampleAppNavHost(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(it),
                navController = navController
            )
        }
    )
}
