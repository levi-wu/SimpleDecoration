package com.guang.max.libdecoration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guang.max.libdecoration.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.recycler.apply {

      adapter = SimpleDecorationVerticalAdapter().apply {
        setData(Data.getData())

      }
    }
  }
}