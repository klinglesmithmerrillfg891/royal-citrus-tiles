package com.royalcitrustiles.puzzle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.royalcitrustiles.puzzle.core.di.ServiceLocator
import com.royalcitrustiles.puzzle.core.navigation.Navigator
import com.royalcitrustiles.puzzle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        val created = ActivityMainBinding.inflate(layoutInflater)
        binding = created
        setContentView(created.root)
        if (savedInstanceState == null) {
            Navigator.showSplash(supportFragmentManager)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
