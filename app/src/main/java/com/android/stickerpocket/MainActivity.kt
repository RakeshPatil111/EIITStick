package com.android.stickerpocket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.android.stickerpocket.databinding.ActivityMainBinding
import com.giphy.sdk.ui.Giphy
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), StickerDialog.StickerDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Giphy.configure(this, BuildConfig.API_KEY)
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

    override fun cancel() {
    }
}