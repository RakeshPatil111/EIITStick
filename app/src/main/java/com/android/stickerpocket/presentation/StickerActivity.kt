package com.android.stickerpocket.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.stickerpocket.BuildConfig
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.ActivityMainBinding
import com.android.stickerpocket.utils.GiphyConfigure
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.GiphyFrescoHandler
import com.giphy.sdk.ui.drawables.ImageFormat
import com.giphy.sdk.ui.themes.GPHTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class StickerActivity : AppCompatActivity(), StickerDialog.StickerDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Configure Giphy SDK
        GiphyConfigure.configGiphy(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun selectedSticker(sticker: Sticker) {
        val action = StickerDetailsNavDirections(sticker)
        navController.navigate(action)
    }

    override fun shareSticker(sticker: Sticker) {
        sticker.let {stickerItem ->
            stickerItem.thumbnail?.let {stickerUrl ->
                val scope = MainScope()
                scope.launch {
                    val gifFile = downloadGifAndSaveToCache(applicationContext, stickerUrl)
                    if (gifFile != null) {
                        val gifUri = FileProvider.getUriForFile(this@StickerActivity, BuildConfig.APPLICATION_ID + ".provider",gifFile);
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("image/gif")
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri)
                        startActivity(Intent.createChooser(shareIntent, "Share GIF using"))
                    } else {
                        Toast.makeText(this@StickerActivity, "Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun cancel() {
    }

    private suspend fun downloadGifAndSaveToCache(context: Context, gifUrl: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(gifUrl)
                val connection = url.openConnection()
                connection.connect()

                // Create a temporary file in the cache directory
                val gifFile = File(context.cacheDir, "temp.gif")

                val inputStream = connection.getInputStream()
                val outputStream = FileOutputStream(gifFile)

                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                gifFile
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}