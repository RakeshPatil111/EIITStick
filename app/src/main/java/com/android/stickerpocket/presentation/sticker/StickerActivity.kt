package com.android.stickerpocket.presentation.sticker

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.stickerpocket.BuildConfig
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.ActivityMainBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.GiphyConfigure
import com.appodeal.ads.Appodeal
import com.appodeal.ads.BannerCallbacks
import com.appodeal.ads.BannerView
import com.appodeal.ads.initializing.ApdInitializationError
import com.appodeal.ads.utils.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText


class StickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val interactor by lazy {
        StickerActivityInteractor()
    }
    private val viewModel: StickerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Configure Giphy SDK
        GiphyConfigure.configGiphy(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setUpAppodealSDK(binding)
        initObserver()
        initNavigation()

    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    private fun initObserver() {
        interactor.initObserver(this, viewModel)
        interactor.liveData.observe(this, Observer {
            when (it) {
                is StickerActivityInteractor.Actions.ShareSticker -> {
                    if (it.file == null) {
                        Toast.makeText(this, "File not downloaded yet", Toast.LENGTH_SHORT).show()
                    } else {
                        val gifUri = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it.file
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("image/gif")
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri)
                        startActivity(Intent.createChooser(shareIntent, "Share GIF using"))
                    }
                }

                is StickerActivityInteractor.Actions.ShowLoading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }
            }
        })
    }


   /* private fun setUpAppodealSDK(binding: ActivityMainBinding) {
        Appodeal.setLogLevel(Log.LogLevel.verbose)
        Appodeal.setTesting(true)
        Appodeal.initialize(
            this,
            BuildConfig.API_KEY,
            Appodeal.BANNER
        ) {
            errors: List<ApdInitializationError>? ->
            val initResult =
                if (errors.isNullOrEmpty()) "successfully" else "with ${errors.size} errors"
            Toast.makeText(this,"Appodeal initialized $initResult",Toast.LENGTH_SHORT)

            errors?.forEach {
                android.util.Log.e("TAG", "onInitializationFinished: ", it)
            }
        }

                if (Appodeal.canShow(Appodeal.BANNER, Companion.placementName)) {
                    Appodeal.show(this@StickerActivity, Appodeal.BANNER_BOTTOM,
                        Companion.placementName
                    )
                } else {
                    Toast.makeText(this,"Cannot show Banner",Toast.LENGTH_SHORT)
                }


        Appodeal.setBannerCallbacks(object : BannerCallbacks {

            override fun onBannerLoaded(height: Int, isPrecache: Boolean) {
                Toast.makeText(this@StickerActivity,"Banner was loaded, isPrecache: $isPrecache",Toast.LENGTH_SHORT)
            }

            override fun onBannerFailedToLoad() {
                Toast.makeText(this@StickerActivity,"Banner failed to load",Toast.LENGTH_SHORT)
            }

            override fun onBannerClicked() {
                Toast.makeText(this@StickerActivity,"Banner was clicked",Toast.LENGTH_SHORT)
            }

            override fun onBannerShowFailed() {
                Toast.makeText(this@StickerActivity,"Banner failed to show",Toast.LENGTH_SHORT)
            }

            override fun onBannerShown() {
                Toast.makeText(this@StickerActivity,"Banner was shown",Toast.LENGTH_SHORT)
            }

            override fun onBannerExpired() {
                Toast.makeText(this@StickerActivity,"Banner was expired",Toast.LENGTH_SHORT)
            }
        })
    }*/


    private fun setUpAppodealSDK(binding: ActivityMainBinding) {


        // Bind the BannerView to Appodeal
        Appodeal.setBannerViewId(R.id.banner_view)
        Appodeal.show(this, Appodeal.BANNER_VIEW)


        val constraintLayout = findViewById<ConstraintLayout>(R.id.root)
        constraintLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            constraintLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = constraintLayout.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is opened
                binding.bottomNavigation.visibility = View.GONE
            } else {
                // Keyboard is closed
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }

    }

    companion object {
        private const val placementName = "default"
    }
}