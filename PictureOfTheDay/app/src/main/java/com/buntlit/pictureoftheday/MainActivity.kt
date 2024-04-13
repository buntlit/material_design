package com.buntlit.pictureoftheday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.buntlit.pictureoftheday.databinding.ActivityMainBinding
import com.buntlit.pictureoftheday.ui.clip.ChipData
import com.buntlit.pictureoftheday.ui.clip.ChipViewModel
import com.buntlit.pictureoftheday.ui.picture.PictureOfTheDayFragment

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val viewModel: ChipViewModel by lazy {
        ViewModelProvider(this)[ChipViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.getThemeId()?.let { setTheme(it) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }
        viewModel.getData().observe(this){
            settingTheme(it)
        }
    }

    private fun settingTheme(data: ChipData) {
        if (data.isNewTheme){
            recreate()
            viewModel.setData(data.chipId, data.themeId, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}