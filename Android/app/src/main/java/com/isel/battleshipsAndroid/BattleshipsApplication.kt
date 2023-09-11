package com.isel.battleshipsAndroid

import android.app.Application
import android.util.Log
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.isel.battleshipsAndroid.about.model.AboutService
import com.isel.battleshipsAndroid.about.model.RealAboutService
import com.isel.battleshipsAndroid.leaderboard.model.LeaderboardService
import com.isel.battleshipsAndroid.leaderboard.model.RealLeaderboardService
import com.isel.battleshipsAndroid.login.UserInfoSharedPrefs
import com.isel.battleshipsAndroid.login.model.LoginService
import com.isel.battleshipsAndroid.login.model.RealLoginService
import com.isel.battleshipsAndroid.login.model.UserInfoRepository
import com.isel.battleshipsAndroid.utils.BattleshipsWorker
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


const val TAG = "BattleshipsApp"
const val HOST = "https://5648-85-243-39-219.eu.ngrok.io"


/**
 * The contract for the object that holds all the globally relevant dependencies.
 */
interface DependenciesContainer {
    val leaderboardService : LeaderboardService
    val loginService : LoginService
    val aboutService: AboutService
    val userInfoRepo : UserInfoRepository
}

/**
 * The application class to be used as a Service Locator.
 */
class BattleshipsApplication : DependenciesContainer, Application() {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            //.cache(Cache(directory = cacheDir, maxSize = 50 * 1024 * 1024))
            .build()
    }

    private val jsonEncoder: Gson by lazy {
        GsonBuilder()
            .create()
    }

    override val leaderboardService: LeaderboardService
        get() = RealLeaderboardService(httpClient,jsonEncoder)
    override val loginService: LoginService
        get() = RealLoginService(httpClient, jsonEncoder)
    override val aboutService: AboutService
        get() = RealAboutService(httpClient,jsonEncoder)
    override val userInfoRepo: UserInfoRepository
        get() = UserInfoSharedPrefs(this)

    private val workerConstraints  = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresCharging(true)
        .build()

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "BattleshipsApplication.onCreate() on process ${android.os.Process.myPid()}")

        val workRequest =
            PeriodicWorkRequestBuilder<BattleshipsWorker>(repeatInterval = 12, TimeUnit.HOURS)
                .setConstraints(workerConstraints)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BattleshipsWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        Log.v(TAG, "BattleshipsWorker was scheduled")
    }

}
