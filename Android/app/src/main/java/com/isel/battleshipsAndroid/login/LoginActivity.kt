package com.isel.battleshipsAndroid.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.work.Constraints
import androidx.work.NetworkType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.isel.battleshipsAndroid.DependenciesContainer
import com.isel.battleshipsAndroid.TAG
import com.isel.battleshipsAndroid.login.model.*
import com.isel.battleshipsAndroid.login.ui.LoginScreenState
import com.isel.battleshipsAndroid.login.ui.LoginView
import com.isel.battleshipsAndroid.ui.RefreshingState
import com.isel.battleshipsAndroid.utils.viewModelInit
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.internal.wait


class LoginActivity : ComponentActivity() {

    private val repo by lazy {
        (application as DependenciesContainer)
    }
    private val viewModel: LoginViewModel by viewModels {
        viewModelInit {
            LoginViewModel(repo.loginService)
        }
    }

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(
            TAG,
            "BattleshipsApplication Login Activity.onCreate() on process ${android.os.Process.myPid()}"
        )

        setContent {
            val loadingState by viewModel.isLoading.collectAsState()
            val token by viewModel.token.collectAsState()
            val error by viewModel.error.collectAsState()
            LoginView(
                state = LoginScreenState(token, loadingState,error),
                onSignupRequest = { username, password ->
                    viewModel.fetchRegisterToken(username, password)
                    runBlocking {
                        launch {
                            while (viewModel.isLoading.value);
                            val tok: Token? = viewModel.token.value
                            if (tok != null) {
                                repo.userInfoRepo.userInfo = UserInfo(username, tok.token)
                                if (repo.userInfoRepo.userInfo != null)
                                    finish()
                            }
                        }
                    }
                    viewModel.resetError()
                },
                onSignInRequest = { username, password ->
                    viewModel.fetchLoginToken(username, password)
                    runBlocking {
                        launch {
                            while (viewModel.isLoading.value);
                            val tok: Token? = viewModel.token.value
                            if (tok != null) {
                                repo.userInfoRepo.userInfo = UserInfo(username, tok.token)
                                if (repo.userInfoRepo.userInfo != null)
                                    finish()
                            }
                        }
                    }
                    viewModel.resetError()
                },
                onBackRequest = {
                    finish()
                }
            )
        }

    }

}
