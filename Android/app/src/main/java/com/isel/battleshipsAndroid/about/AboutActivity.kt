package com.isel.battleshipsAndroid.about

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.isel.battleshipsAndroid.R
import com.isel.battleshipsAndroid.about.model.Author
import com.isel.battleshipsAndroid.about.ui.AboutView

class AboutActivity : ComponentActivity() {

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutView(
                onBackRequest = { finish() },
                onSendEmailRequested =  ::onOpenSendEmail,
                onUrlRequested = ::openUrl,
                listOfAuthor = listOfAuthor
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }


    private fun onOpenSendEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, "About the Battleship App")
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(ContentValues.TAG, "Failed to send email", e)
            Toast
                .makeText(
                    this,
                    R.string.activity_info_no_suitable_app,
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }

    private fun openUrl(url: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("Battleships", "Failed to open URL", e)
            Toast
                .makeText(
                    this,
                    R.string.activity_info_no_suitable_app,
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }
}

private val listOfAuthor: List<Author> = listOf(
    Author(
        "Ricardo Bernardino",
        "A47283@alunos.isel.pt",
        "https://github.com/Ricardo-Bernardino-dev"
    ),
    Author("David Costa", "A45935@alunos.isel.pt", "https://github.com/A45935"),
    Author("Miguel Almeida", "A47249@alunos.isel.pt", "https://github.com/miguelalmeida2")
)









