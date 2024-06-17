package com.android.stickerpocket.presentation.sticker

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
}