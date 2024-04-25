package com.junmo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.junmo.airquality.databinding.ActivityWeatherBinding

class TwoFragment : Fragment() {
    lateinit var binding: ActivityWeatherBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }
}