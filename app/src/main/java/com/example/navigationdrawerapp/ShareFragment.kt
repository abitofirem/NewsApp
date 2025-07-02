package com.example.navigationdrawerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationdrawerapp.databinding.FragmentShareBinding // Bu satırı kendi layout dosyanıza göre güncelleyin

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null // Binding sınıfı adını güncelleyin
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false) // Binding sınıfı adını güncelleyin
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}