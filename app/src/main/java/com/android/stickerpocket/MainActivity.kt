package com.android.stickerpocket

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.stickerpocket.databinding.ActivityMainBinding
import com.facebook.cache.common.CacheKey
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.imagepipeline.cache.CacheKeyFactory
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.GiphyFrescoHandler
import com.giphy.sdk.ui.drawables.ImageFormat
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class MainActivity : AppCompatActivity(), StickerDialog.StickerDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Configure Giphy SDK
        configGiphySDK()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    private fun configGiphySDK() {
        Giphy.configure(
            this,
            BuildConfig.API_KEY,
            verificationMode = false,
            frescoHandler = object : GiphyFrescoHandler {
                override fun handle(imagePipelineConfigBuilder: ImagePipelineConfig.Builder) {
                    imagePipelineConfigBuilder
                        .setMainDiskCacheConfig(
                            DiskCacheConfig.newBuilder(this@MainActivity)
                                .setMaxCacheSize(150)
                                .setMaxCacheSizeOnLowDiskSpace(50)
                                .setMaxCacheSizeOnVeryLowDiskSpace(10)
                                .build()
                        )
                        .setCacheKeyFactory(DefaultCacheKeyFactory.getInstance())
                }
                override fun handle(okHttpClientBuilder: OkHttpClient.Builder) {
                }
            })

        val settings = GPHSettings(GPHTheme.Dark)
        settings.apply {
            imageFormat = ImageFormat.WEBP
            showSuggestionsBar = false
            renditionType = RenditionType.fixedWidth
            confirmationRenditionType = RenditionType.original
            selectedContentType = GPHContentType.gif
            showConfirmationScreen = false
        }
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
                        val gifUri = FileProvider.getUriForFile(this@MainActivity, BuildConfig.APPLICATION_ID + ".provider",gifFile);
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("image/gif")
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri)
                        startActivity(Intent.createChooser(shareIntent, "Share GIF using"))
                    } else {
                        Toast.makeText(this@MainActivity, "Please try again", Toast.LENGTH_SHORT).show()
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