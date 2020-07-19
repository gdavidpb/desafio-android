package com.gdavidpb.github.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.gdavidpb.github.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpNavigationController()
    }

    private fun setUpNavigationController() {
        val appBarConfiguration by lazy {
            val destinations = setOf(
                R.id.nav_repositories
            )

            AppBarConfiguration(navController.graph).apply {
                topLevelDestinations.addAll(destinations)
            }
        }

        setSupportActionBar(toolbar)

        NavigationUI.setupWithNavController(toolbar, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }
}
