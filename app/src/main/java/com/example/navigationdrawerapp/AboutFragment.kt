package com.example.navigationdrawerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationdrawerapp.databinding.FragmentAboutBinding // Bu satırı kendi layout dosyanıza göre güncelleyin

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null // Binding sınıfı adını güncelleyin
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false) // Binding sınıfı adını güncelleyin
        return binding.root



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}