package com.isel.battleshipsAndroid.me

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.isel.battleshipsAndroid.DependenciesContainer
import com.isel.battleshipsAndroid.me.model.MeViewModel
import com.isel.battleshipsAndroid.me.model.RealMeService
import com.isel.battleshipsAndroid.me.ui.MeScreenState
import com.isel.battleshipsAndroid.me.ui.MeView
import com.isel.battleshipsAndroid.utils.viewModelInit
import okhttp3.OkHttpClient

class MeActivity : ComponentActivity() {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            //.cache(Cache(directory = cacheDir, maxSize = 50 * 1024 * 1024))
            .build()
    }

    private val jsonEncoder: Gson by lazy {
        GsonBuilder()
            .create()
    }

    private val repo by lazy {
        (application as DependenciesContainer)
    }

    private val viewModel: MeViewModel by viewModels {
        viewModelInit {
            require(repo.userInfoRepo.userInfo != null)
            MeViewModel(
                RealMeService(repo.userInfoRepo.userInfo!!, httpClient, jsonEncoder)
            )
        }
    }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, MeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (repo.userInfoRepo.userInfo == null) throw IllegalStateException()
        viewModel.fetchAllInfo()
        setContent {
            val error by viewModel.error.collectAsState()
            val myInfo by viewModel.myInfo.collectAsState()
            val myGamesHistory by viewModel.myGamesHistory.collectAsState()
            MeView(
                state = MeScreenState(
                    myInfo,
                    myGamesHistory,
                    error,
                ),
                onBackRequest = { finish() },
                onErrorReset = { viewModel.resetError()},
            )
        }
    }
}

