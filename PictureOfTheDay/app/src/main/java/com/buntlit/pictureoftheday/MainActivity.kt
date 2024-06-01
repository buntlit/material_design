package com.buntlit.pictureoftheday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.buntlit.pictureoftheday.databinding.ActivityMainBinding
import com.buntlit.pictureoftheday.ui.settings.SettingsData
import com.buntlit.pictureoftheday.ui.settings.SettingsViewModel

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.getThemeId()?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        init()
    }

//    private fun menuBehavior() {
//        addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_action_bar, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId) {
//                    R.id.app_bar_fav -> {
//                        false
//                    }
//                    R.id.settings_fragment -> {
//                        menuItem.onNavDestinationSelected(navController)
//                        checkDestination(menuItem.itemId)
//                        navController.popBackStack(R.id.settings_fragment,
//                            inclusive = false,
//                            saveState = true
//                        )
//                        true
//
//                    }
//                    else -> false
//
//                }
//            }
//
//        })
//    }

    private fun init(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.navigation_graph_pod,
                    R.id.navigation_graph_rovers
                )
            )
        binding?.bottomNavigationView?.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
//        menuBehavior()
//        bottomNavigationBehavior()

        viewModel.getData().observe(this) {
            settingTheme(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

//    @SuppressLint("AppCompatMethod")
//    private fun bottomNavigationBehavior() {
//        binding?.bottomNavigationView?.setOnItemSelectedListener {
//            it.onNavDestinationSelected(navController)
//            invalidateOptionsMenu()
//            true
//            when (it.itemId) {
//                R.id.graph_navigation_pod -> {
//
//                    true
//                }
//                R.id.graph_navigation_rovers -> {
//                    closeOptionsMenu()
//                    it.onNavDestinationSelected(navController)
//                    true
//                }
//                else -> false
//            }
//        }
//        binding?.bottomNavigationView?.setOnItemReselectedListener {
//            when (it.itemId) {
//                R.id.navigation_one -> {
//                    navController.popBackStack(
//                        R.id.fragment_pod,
//                        inclusive = false,
//                        saveState = true
//                    )
//                }
//                R.id.navigation_two -> {
//                    navController.popBackStack(
//                        R.id.rovers_fragment,
//                        inclusive = false,
//                        saveState = true
//                    )
//                }
//            }
//        }
//    }


//    private fun checkDestination(destination: Int): Boolean {
//        return if (navController.graph.id == destination) {
//            false
//        } else {
//        val startDestination = navController.graph.startDestinationId
//        navController.navigate(destination, null, navOptions = navOptions {
//            launchSingleTop = true
//            restoreState = true
//            popUpTo(startDestination) {
//                saveState = true
//                inclusive = false
//            }
//
//        })
//            navController.navigate(destination)
//        return true
//        }
//    }

    private fun settingTheme(data: SettingsData) {
        if (data.isNewTheme) {
            recreate()
            viewModel.setData(data.chipId, data.themeId, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}