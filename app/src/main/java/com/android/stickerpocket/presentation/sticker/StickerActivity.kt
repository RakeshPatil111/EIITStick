package com.android.stickerpocket.presentation.sticker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.stickerpocket.BuildConfig
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.ActivityMainBinding
import com.android.stickerpocket.presentation.Sticker
import com.android.stickerpocket.presentation.StickerDetailsNavDirections
import com.android.stickerpocket.presentation.StickerDialog
import com.android.stickerpocket.utils.GiphyConfigure
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StickerActivity : AppCompatActivity(), StickerDialog.StickerDialogListener {

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
            if (interactor.getLocalEmoji().size == 0){
                interactor.saveEmojiToLocalDB(R.raw.emojis)
            }
            Log.d("Emoji list size:", interactor.getLocalEmoji().size.toString())
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
                    binding.progressBar.visibility = android.view.View.GONE
                    if (it.file != null) {
                        val gifUri = FileProvider.getUriForFile(
                            this@StickerActivity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it.file
                        );
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("image/gif")
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri)
                        startActivity(Intent.createChooser(shareIntent, "Share GIF using"))
                    } else {
                        Toast.makeText(this@StickerActivity, "Please try again", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                is StickerActivityInteractor.Actions.ShowLoading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }
            }
        })
    }

    override fun selectedSticker(sticker: Sticker) {
        val action = StickerDetailsNavDirections(sticker)
        navController.navigate(action)
    }

    override fun shareSticker(sticker: Sticker) {
        interactor.onShareSticker(sticker)
    }

    override fun cancel() {
    }

}