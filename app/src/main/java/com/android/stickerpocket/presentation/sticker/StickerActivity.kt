package com.android.stickerpocket.presentation.sticker

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.ActivityMainBinding
import com.android.stickerpocket.utils.GiphyConfigure
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val interactor by lazy {
        StickerActivityInteractor()
    }
    private val viewModel by viewModels<StickerActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Configure Giphy SDK
        GiphyConfigure.configGiphy(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()
        initNavigation()

        CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            if (interactor.fetchEmojiCount() == 0){
                interactor.saveEmojiToLocalDB(R.raw.emojis)
            }
            Log.d("Emoji list size:", interactor.fetchEmojiCount().toString())
        }
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

                }

                is StickerActivityInteractor.Actions.ShowLoading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }
            }
        })
    }
}